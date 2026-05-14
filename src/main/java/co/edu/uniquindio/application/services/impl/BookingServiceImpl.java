package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.bookingDTO.BookingDTO;
import co.edu.uniquindio.application.dto.bookingDTO.BookingListItemDTO;
import co.edu.uniquindio.application.dto.bookingDTO.CreateBookingDTO;
import co.edu.uniquindio.application.dto.bookingDTO.SearchBookingDTO;
import co.edu.uniquindio.application.dto.bookingDTO.UserBookingDTO;
import co.edu.uniquindio.application.exceptions.*;
import co.edu.uniquindio.application.mappers.BookingMapper;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.BookingState;
import co.edu.uniquindio.application.model.enums.State;
import co.edu.uniquindio.application.repositories.CommentRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.repositories.spec.BookingSpecifications;
import co.edu.uniquindio.application.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final CurrentUserServiceImpl currentUserService;

    @Override
    public void create(String id, String userId, CreateBookingDTO createBookingDTO) throws Exception {

        if (!createBookingDTO.checkIn().plusDays(1).isBefore(createBookingDTO.checkOut())
                && !createBookingDTO.checkIn().plusDays(1).isEqual(createBookingDTO.checkOut())) {
            throw new BadRequestException("La reserva debe ser mínimo de 1 noche");
        }

        if (createBookingDTO.checkIn().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("el checkIn es invalido");
        }

        if (createBookingDTO.checkIn().isAfter(createBookingDTO.checkOut())) {
            throw new BadRequestException("Datos incorrectos o la fecha de checkIn está despues de la fecha de check Out");
        }

        boolean solapa = bookingRepository.existsOverlappingBooking(id, createBookingDTO.checkIn(), createBookingDTO.checkOut());
        if (solapa) {
            throw new ValueConflictException("fechas no disponibles");
        }

        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el alojamiento"));

        if (place.getState() != State.ACTIVE) {
            throw new BadRequestException("El alojamiento no está activo");
        }

        // === Validación de capacidad ===
        Integer guests = createBookingDTO.guest_number();

        if (guests > place.getCapacity()) {
            throw new BadRequestException("Excede capacidad del lugar");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario"));

        Booking booking = bookingMapper.toEntity(createBookingDTO, place, user);
        if (booking.getBookingState() == null) booking.setBookingState(BookingState.PENDING);

        bookingRepository.save(booking);
    }

    @Override
    public void delete(String id) throws Exception {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isEmpty()) {
            throw new ResourceNotFoundException("No existe esta reserva");
        }

        if (!Objects.equals(currentUserService.getCurrentUser(), booking.get().getUser().getId())) {
            throw new ForbiddenException("No te pertenece esta reserva");
        }

        if (booking.get().getBookingState() == BookingState.PENDING
                || booking.get().getBookingState() == BookingState.CONFIRMED) {
            LocalDateTime checkIn = booking.get().getCheckIn();
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(checkIn.minusHours(48))) {
                booking.get().setBookingState(BookingState.CANCELED);
                bookingRepository.save(booking.get());
            } else {
                throw new ValueConflictException("solo puedes cancelar una reserva 48 horas antes de la fecha de check in");
            }
        } else {
            throw new UnauthorizedException("no puedes cancelar esta reserva");
        }
    }
    @Override
    public void rejectByHost(String bookingId) throws Exception {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isEmpty()) {
            throw new ResourceNotFoundException("No existe esta reserva");
        }

        Booking booking = optionalBooking.get();
        String currentUserId = currentUserService.getCurrentUser();

        if (!Objects.equals(booking.getPlace().getUser().getId(), currentUserId)) {
            throw new ForbiddenException("No eres el anfitrión de esta reserva");
        }

        if (booking.getBookingState() != BookingState.PENDING) {
            throw new ValueConflictException("Solo puedes rechazar reservas en estado PENDING");
        }

        booking.setBookingState(BookingState.REJECTED);
        bookingRepository.save(booking);
    }
    @Override
    public List<BookingDTO> listBookings(String id, int page, SearchBookingDTO searchBookingDTO) throws Exception {

        Optional<Place> place = placeRepository.findById(id);

        if (searchBookingDTO.guest_number() != null && searchBookingDTO.guest_number() <= 0) {
            throw new BadRequestException("el numero de huespedes no puede ser menor o 0");
        }
        return getBookingPlaceDTOS(id, page, searchBookingDTO, place.isEmpty(), place);
    }

    @Override
    public List<BookingDTO> listBookingsUser(String id, int page, SearchBookingDTO searchBookingDTO) throws Exception {

        Optional<User> user = userRepository.findById(id);
        return getBookingUserDTOS(id, page, searchBookingDTO, user.isEmpty(), user);
    }

    @Override
    public void confirm(String bookingId) throws Exception {
        var b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe esta reserva"));

        // dueño del alojamiento
        String ownerId = b.getPlace().getUser().getId();

        if (!Objects.equals(ownerId, currentUserService.getCurrentUser())) {
            throw new ForbiddenException("No eres el anfitrión de este alojamiento");
        }

        if (b.getBookingState() != BookingState.PENDING) {
            throw new BadRequestException("Solo se puede confirmar si está PENDING");
        }

        if (!b.getCheckIn().isAfter(LocalDateTime.now())) {
            b.setBookingState(BookingState.REJECTED);
            bookingRepository.save(b);
            throw new BadRequestException("No se puede confirmar una reserva cuya fecha de entrada ya venció.");
        }

        b.setBookingState(BookingState.CONFIRMED);
        bookingRepository.save(b);
    }

    @NotNull
    private List<BookingDTO> getBookingUserDTOS(String id, int page, SearchBookingDTO searchBookingDTO, boolean empty, Optional<User> user) {
        if (empty) {
            throw new ResourceNotFoundException("No existe el usuario");
        }

        Pageable pageable = PageRequest.of(page, 10);
        Specification<Booking> spec = Specification.allOf(
                BookingSpecifications.byUserId(id),
                BookingSpecifications.withState(searchBookingDTO.state()),
                BookingSpecifications.fromDate(searchBookingDTO.checkIn()),
                BookingSpecifications.toDate(searchBookingDTO.checkOut()),
                BookingSpecifications.withGuests(searchBookingDTO.guest_number())
        );
        Page<Booking> bookings = bookingRepository.findAll(spec, pageable);

        return bookings.stream()
                .map(bookingMapper::toBookingDTO)
                .toList();
    }

    @NotNull
    private List<BookingDTO> getBookingPlaceDTOS(String id, int page, SearchBookingDTO searchBookingDTO, boolean empty, Optional<Place> place) {
        if (empty) {
            throw new ResourceNotFoundException("No existe el alojamiento");
        }

        Pageable pageable = PageRequest.of(page, 10);
        Specification<Booking> spec = Specification.allOf(
                BookingSpecifications.byPlaceId(id),
                BookingSpecifications.withState(searchBookingDTO.state()),
                BookingSpecifications.fromDate(searchBookingDTO.checkIn()),
                BookingSpecifications.toDate(searchBookingDTO.checkOut()),
                BookingSpecifications.withGuests(searchBookingDTO.guest_number())
        );
        Page<Booking> bookings = bookingRepository.findAll(spec, pageable);

        return bookings.stream()
                .map(bookingMapper::toBookingDTO)
                .toList();
    }

    @Override
    public List<BookingListItemDTO> listByPlace(String placeId,
                                                BookingState state,
                                                LocalDateTime from,
                                                LocalDateTime to,
                                                Integer guests) throws Exception {
        Specification<Booking> spec = Specification.allOf(
                BookingSpecifications.byPlaceId(placeId),
                BookingSpecifications.withState(state),
                BookingSpecifications.fromDate(from),
                BookingSpecifications.toDate(to),
                BookingSpecifications.withGuests(guests)
        );

        return bookingRepository.findAll(spec)
                .stream()
                .map(booking -> new BookingListItemDTO(
                        booking.getId(),
                        booking.getBookingState(),
                        userMapper.toUserDTO(booking.getUser()),
                        booking.getCheckIn(),
                        booking.getCheckOut(),
                        booking.getGuest_number(),
                        commentRepository.existsByBookingId(booking.getId())
                ))
                .toList();
    }

    // 👇 NUEVOooo: "Mis reservas" para el usuario (UserBookingDTO)
    @Override
    public List<UserBookingDTO> listUserBookings(String userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario"));

        List<Booking> bookings = bookingRepository.findByUser(user);

        return bookings.stream()
                .map(booking -> new UserBookingDTO(
                        booking.getId(),
                        booking.getBookingState(),
                        booking.getCheckIn().toLocalDate(),
                        booking.getCheckOut().toLocalDate(),
                        booking.getGuest_number(),
                        booking.getPlace().getId(),
                        booking.getPlace().getTitle(),
                        booking.getPlace().getPics_url() != null && !booking.getPlace().getPics_url().isEmpty()
                                ? booking.getPlace().getPics_url().get(0)
                                : null,
                        booking.getPlace().getCapacity(),
                        commentRepository.existsByBookingId(booking.getId())
                ))
                .toList();
    }

    /**
     * Actualiza el estado de una booking a COMPLETED si su checkOut ya pasó.
     * @param bookingId el ID de la booking a actualizar
     * @return true si fue actualizada, false si no cumple las condiciones
     */
    @Override
    @Transactional
    public boolean updateStatusIfCompleted(String bookingId) throws Exception {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isEmpty()) {
            return false;
        }

        Booking booking = optionalBooking.get();

        // Si NO está CONFIRMED, no hacer nada
        if (booking.getBookingState() != BookingState.CONFIRMED) {
            return false;
        }

        // Si el checkOut NO ha pasado aún, no hacer nada
        if (booking.getCheckOut().isAfter(LocalDateTime.now())) {
            return false;
        }

        // Actualizar a COMPLETED
        booking.setBookingState(BookingState.COMPLETED);
        bookingRepository.save(booking);

        return true;
    }
}
