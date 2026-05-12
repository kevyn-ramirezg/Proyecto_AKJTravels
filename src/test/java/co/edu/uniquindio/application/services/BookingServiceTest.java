package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.bookingDTO.CreateBookingDTO;
import co.edu.uniquindio.application.exceptions.BadRequestException;
import co.edu.uniquindio.application.exceptions.ForbiddenException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.mappers.BookingMapper;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.BookingState;
import co.edu.uniquindio.application.model.enums.State;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.CommentRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.impl.BookingServiceImpl;
import co.edu.uniquindio.application.services.impl.CurrentUserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserServiceImpl currentUserService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("Debe crear una reserva pendiente cuando los datos son validos")
    void shouldCreatePendingBooking() throws Exception {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(5);
        LocalDateTime checkOut = checkIn.plusDays(2);
        CreateBookingDTO dto = new CreateBookingDTO(checkIn, checkOut, 2);
        Place place = place("place-1", host("host-1"), State.ACTIVE, 4);
        User guest = guest("user-1");
        Booking mappedBooking = booking("booking-1", place, guest, checkIn, checkOut, BookingState.PENDING);

        when(bookingRepository.existsOverlappingBooking("place-1", checkIn, checkOut)).thenReturn(false);
        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(guest));
        when(bookingMapper.toEntity(dto, place, guest)).thenReturn(mappedBooking);

        bookingService.create("place-1", "user-1", dto);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertEquals(BookingState.PENDING, captor.getValue().getBookingState());
    }

    @Test
    @DisplayName("Debe rechazar reservas de menos de una noche")
    void shouldRejectBookingShorterThanOneNight() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(5);
        CreateBookingDTO dto = new CreateBookingDTO(checkIn, checkIn.plusHours(12), 2);

        assertThrows(BadRequestException.class, () -> bookingService.create("place-1", "user-1", dto));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar reserva cuando las fechas se solapan")
    void shouldRejectOverlappingBooking() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(5);
        LocalDateTime checkOut = checkIn.plusDays(2);
        CreateBookingDTO dto = new CreateBookingDTO(checkIn, checkOut, 2);

        when(bookingRepository.existsOverlappingBooking("place-1", checkIn, checkOut)).thenReturn(true);

        assertThrows(ValueConflictException.class, () -> bookingService.create("place-1", "user-1", dto));

        verify(placeRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar reserva cuando excede la capacidad del alojamiento")
    void shouldRejectBookingWhenCapacityIsExceeded() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(5);
        LocalDateTime checkOut = checkIn.plusDays(2);
        CreateBookingDTO dto = new CreateBookingDTO(checkIn, checkOut, 5);
        Place place = place("place-1", host("host-1"), State.ACTIVE, 4);

        when(bookingRepository.existsOverlappingBooking("place-1", checkIn, checkOut)).thenReturn(false);
        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));

        assertThrows(BadRequestException.class, () -> bookingService.create("place-1", "user-1", dto));

        verify(userRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe cancelar una reserva propia si faltan mas de 48 horas")
    void shouldCancelOwnBookingBeforeFortyEightHours() throws Exception {
        User guest = guest("user-1");
        Booking booking = booking("booking-1", place("place-1", host("host-1"), State.ACTIVE, 4), guest,
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(7), BookingState.CONFIRMED);

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(currentUserService.getCurrentUser()).thenReturn("user-1");

        bookingService.delete("booking-1");

        assertEquals(BookingState.CANCELED, booking.getBookingState());
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Debe impedir cancelar una reserva que no pertenece al usuario actual")
    void shouldRejectCancelWhenBookingDoesNotBelongToCurrentUser() {
        User guest = guest("user-1");
        Booking booking = booking("booking-1", place("place-1", host("host-1"), State.ACTIVE, 4), guest,
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(7), BookingState.CONFIRMED);

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(currentUserService.getCurrentUser()).thenReturn("other-user");

        assertThrows(ForbiddenException.class, () -> bookingService.delete("booking-1"));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe confirmar una reserva pendiente cuando el usuario actual es el anfitrion")
    void shouldConfirmPendingBookingByHost() throws Exception {
        User host = host("host-1");
        Booking booking = booking("booking-1", place("place-1", host, State.ACTIVE, 4), guest("user-1"),
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(7), BookingState.PENDING);

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(currentUserService.getCurrentUser()).thenReturn("host-1");

        bookingService.confirm("booking-1");

        assertEquals(BookingState.CONFIRMED, booking.getBookingState());
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Debe rechazar confirmacion si el usuario actual no es el anfitrion")
    void shouldRejectConfirmWhenCurrentUserIsNotHost() {
        User host = host("host-1");
        Booking booking = booking("booking-1", place("place-1", host, State.ACTIVE, 4), guest("user-1"),
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(7), BookingState.PENDING);

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(currentUserService.getCurrentUser()).thenReturn("other-host");

        assertThrows(ForbiddenException.class, () -> bookingService.confirm("booking-1"));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe completar una reserva confirmada cuando el check-out ya paso")
    void shouldMarkConfirmedBookingAsCompletedAfterCheckout() throws Exception {
        Booking booking = booking("booking-1", place("place-1", host("host-1"), State.ACTIVE, 4), guest("user-1"),
                LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(1), BookingState.CONFIRMED);
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));

        boolean updated = bookingService.updateStatusIfCompleted("booking-1");

        assertTrue(updated);
        assertEquals(BookingState.COMPLETED, booking.getBookingState());
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("No debe completar reservas que aun no han terminado")
    void shouldNotCompleteBookingBeforeCheckout() throws Exception {
        Booking booking = booking("booking-1", place("place-1", host("host-1"), State.ACTIVE, 4), guest("user-1"),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingState.CONFIRMED);
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));

        boolean updated = bookingService.updateStatusIfCompleted("booking-1");

        assertFalse(updated);
        assertEquals(BookingState.CONFIRMED, booking.getBookingState());
        verify(bookingRepository, never()).save(any());
    }

    private User host(String id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private User guest(String id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Place place(String id, User host, State state, int capacity) {
        Place place = new Place();
        place.setId(id);
        place.setUser(host);
        place.setState(state);
        place.setCapacity(capacity);
        return place;
    }

    private Booking booking(String id, Place place, User guest, LocalDateTime checkIn,
                            LocalDateTime checkOut, BookingState state) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setPlace(place);
        booking.setUser(guest);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setGuest_number(2);
        booking.setBookingState(state);
        booking.setCreatedAt(LocalDateTime.now());
        return booking;
    }
}
