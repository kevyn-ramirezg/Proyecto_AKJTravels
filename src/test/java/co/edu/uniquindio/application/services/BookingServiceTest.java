package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.bookingDTO.CreateBookingDTO;
import co.edu.uniquindio.application.dto.bookingDTO.BookingDTO;
import co.edu.uniquindio.application.dto.bookingDTO.SearchBookingDTO;
import co.edu.uniquindio.application.exceptions.BadRequestException;
import co.edu.uniquindio.application.exceptions.ResourceNotFoundException;
import co.edu.uniquindio.application.exceptions.UnauthorizedException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.mappers.BookingMapper;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.BookingState;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.services.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    /*
    @Mock BookingMapper bookingMapper;
    @Mock BookingRepository bookingRepository;
    @Mock PlaceRepository accommodationRepository;

    @InjectMocks BookingServiceImpl bookingService;

    private String accommodationId;

    @BeforeEach
    void init() {
        accommodationId = UUID.randomUUID().toString();
    }

    private CreateBookingDTO makeCreateDto(LocalDateTime in, LocalDateTime out, int guests){
        // tu record CreateBookingDTO(in, out, guests)
        return new CreateBookingDTO(in, out, guests);
    }

    private Place makePlaceWithOwner(){
        Place a = new Place();
        a.setId(accommodationId);
        User owner = new User();
        owner.setId(UUID.randomUUID().toString());
        a.setUser(owner);
        return a;
    }

    private Booking makeBooking(LocalDateTime in, BookingState state){
        Booking b = new Booking();
        b.setId(UUID.randomUUID().toString());
        b.setPlace(makePlaceWithOwner());
        b.setUser(b.getPlace().getUser());
        b.setBookingState(state);
        b.setCheckIn(in);
        b.setCheckOut(in.plusDays(1));
        b.setGuest_number(2);
        b.setCreatedAt(LocalDateTime.now());
        return b;
    }

    @Test
    @DisplayName("delete(): reserva inexistente -> NotFound")
    void deleteFailsWhenBookingMissing() {
        when(bookingRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.delete("x"));
    }

    @Test
    @DisplayName("listBookings(): guests <= 0 -> BadRequest")
    void listBookingsFailsWhenInvalidGuests() {
        SearchBookingDTO filters = new SearchBookingDTO(null, null, null, 0);
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(makePlaceWithOwner()));
        assertThrows(BadRequestException.class, () -> bookingService.listBookings(accommodationId, 0, filters));
    }

    @Test
    @DisplayName("listBookings(): alojamiento inexistente -> NotFound")
    void listBookingsFailsWhenAccommodationMissing() {
        SearchBookingDTO filters = new SearchBookingDTO(null, null, null, null);
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookingService.listBookings(accommodationId, 0, filters));
    }

    // ---------- create()

    /*@Test
    @DisplayName("create(): checkIn en pasado -> BadRequest")
    void createFailsWhenCheckInPast() {
        CreateBookingDTO dto = makeCreateDto(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), 2);
        assertThrows(BadRequestException.class, () -> bookingService.create(accommodationId, dto));
    }*/

    /*@Test
    @DisplayName("create(): checkIn > checkOut -> BadRequest")
    void createFailsWhenCheckInAfterCheckOut() {
        LocalDateTime in = LocalDateTime.now().plusDays(3);
        CreateBookingDTO dto = makeCreateDto(in, in.minusHours(1), 2);
        assertThrows(BadRequestException.class, () -> bookingService.create(accommodationId, dto));
    }*/

    /*@Test
    @DisplayName("create(): fechas traslapadas -> ValueConflict")
    void createFailsWhenOverlapping() throws Exception {
        LocalDateTime in = LocalDateTime.now().plusDays(2);
        LocalDateTime out = in.plusDays(2);
        when(bookingRepository.existsOverlappingBooking(eq(accommodationId), any(), any()))
                .thenReturn(true);

        CreateBookingDTO dto = makeCreateDto(in, out, 2);
        assertThrows(ValueConflictException.class, () -> bookingService.create(accommodationId, dto));
    }*/

    /*@Test
    @DisplayName("create(): alojamiento inexistente -> NotFound")
    void createFailsWhenAccommodationMissing() {
        LocalDateTime in = LocalDateTime.now().plusDays(2);
        LocalDateTime out = in.plusDays(1);
        when(bookingRepository.existsOverlappingBooking(anyString(), any(), any())).thenReturn(false);
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());

        CreateBookingDTO dto = makeCreateDto(in, out, 2);
        assertThrows(ResourceNotFoundException.class, () -> bookingService.create(accommodationId, dto));
    }*/

    /*@Test
    @DisplayName("create(): ok -> mapea y guarda")
    void createSuccessSavesBooking() throws Exception {
        LocalDateTime in = LocalDateTime.now().plusDays(2);
        LocalDateTime out = in.plusDays(1);
        when(bookingRepository.existsOverlappingBooking(anyString(), any(), any())).thenReturn(false);
        Place acc = makePlaceWithOwner();
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(acc));

        Booking mapped = makeBooking(in, BookingState.PENDING);
        when(bookingMapper.toEntity(any(), eq(acc), eq(acc.getUser()))).thenReturn(mapped);

        CreateBookingDTO dto = makeCreateDto(in, out, 2);
        bookingService.create(accommodationId, dto);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertEquals(mapped.getId(), captor.getValue().getId());
    }*/

    // ---------- delete()



    /*@Test
    @DisplayName("delete(): estado ≠ PENDING -> Unauthorized")
    void deleteFailsWhenNotPending() {
        Booking b = makeBooking(LocalDateTime.now().plusDays(3), BookingState.COMPLETED);
        when(bookingRepository.findById(b.getId())).thenReturn(Optional.of(b));
        assertThrows(UnauthorizedException.class, () -> bookingService.delete(b.getId()));
    }*/

    /*@Test
    @DisplayName("delete(): < 48h -> ValueConflict")
    void deleteFailsWhenLessThan48h() {
        Booking b = makeBooking(LocalDateTime.now().plusHours(47), BookingState.PENDING);
        when(bookingRepository.findById(b.getId())).thenReturn(Optional.of(b));
        assertThrows(ValueConflictException.class, () -> bookingService.delete(b.getId()));
    }*/

    /*@Test
    @DisplayName("delete(): ≥ 48h -> CANCELED y guarda")
    void deleteSuccessWhen48hOrMore() throws Exception {
        Booking b = makeBooking(LocalDateTime.now().plusHours(49), BookingState.PENDING);
        when(bookingRepository.findById(b.getId())).thenReturn(Optional.of(b));

        bookingService.delete(b.getId());

        assertEquals(BookingState.CANCELED, b.getBookingState());
        verify(bookingRepository).save(b);
    }*/

    // ---------- listBookings()



    /*@Test
    @DisplayName("listBookings(): ok -> mapea y devuelve")
    void listBookingsReturnsDtos() throws Exception {
        SearchBookingDTO filters = new SearchBookingDTO(null, null, null, null);
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(makePlaceWithOwner()));

        Booking booking = makeBooking(LocalDateTime.now().plusDays(10), BookingState.PENDING);
        when(bookingRepository.findBookingsByPlaceWithFilters(eq(accommodationId), eq(filters), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingMapper.toBookingDTO(any(Booking.class))).thenReturn(
                new BookingDTO(
                        "Reserva prueba",              // title
                        null,                          // State (puede ser null)
                        null,                          // UserDTO
                        booking.getCheckIn().toLocalDate(),
                        booking.getCheckOut().toLocalDate(),
                        booking.getGuest_number()
                )
        );

        var result = bookingService.listBookings(accommodationId, 0, filters);
        assertEquals(1, result.size());
        verify(bookingMapper, times(1)).toBookingDTO(any());
    }*/
}
