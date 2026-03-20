package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.placeDTO.*;
import co.edu.uniquindio.application.exceptions.ResourceNotFoundException;
import co.edu.uniquindio.application.exceptions.UnauthorizedException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.mappers.PlaceMapper;
import co.edu.uniquindio.application.mappers.ShowPlaceMapper;
import co.edu.uniquindio.application.mappers.StatsMapper;
import co.edu.uniquindio.application.model.Coordinates;
import co.edu.uniquindio.application.model.Location;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.Services;
import co.edu.uniquindio.application.model.enums.BookingState;
import co.edu.uniquindio.application.model.enums.PlaceType;
import co.edu.uniquindio.application.model.enums.State;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.CommentRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.impl.PlaceServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {
    /*
    @Mock
    PlaceMapper placeMapper;
    @Mock ShowPlaceMapper showPlaceMapper;
    @Mock
    PlaceRepository placeRepository;
    @Mock BookingRepository bookingRepository;
    @Mock UserRepository userRepository;
    @Mock CommentRepository commentRepository;
    @Mock StatsMapper statsMapper;

    @InjectMocks
    PlaceServiceImpl service;


    private CreatePlaceDTO makeCreateDTO(float lat, float lon, String title) {
        return new CreatePlaceDTO(
                title,                              // title (5..25 chars recomendado por validaciones)
                "Descripción suficientemente larga para pasar validación.", // description
                150_000,                            // price
                List.of("https://img/1.jpg"),       // picsUrl
                PlaceType.APARTMENT,        // accommodationType
                4,                                  // capacity
                "Colombia",                         // country
                "Valle",                            // department
                "Cali",                             // city
                "San Antonio",                      // neighborhood
                "Cra 1 # 2-3",                      // street
                "A1B2C3",                           // postalCode (patrón 4..10 alfanumérico)
                List.copyOf(EnumSet.of(Services.WIFI, Services.GYM)), // services >=1
                lat, lon                            // latitude, longitude
        );
    }

    private Place makeActivePlaceAt(float lat, float lon, String title) {
        Coordinates coords = new Coordinates();
        coords.setLatitude(lat);
        coords.setLongitude(lon);
        Location loc = new Location();
        loc.setCoordinates(coords);

        Place a = new Place();
        a.setTitle(title);
        a.setLocation(loc);
        a.setState(State.ACTIVE);
        return a;
    }

    // ----------------- create() -----------------

    @Test
    @DisplayName("create(): usuario inexistente -> ResourceNotFound")
    void create_UserNotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());

        var dto = makeCreateDTO(4.0f, -76.0f, "Casa Centro");
        assertThrows(ResourceNotFoundException.class, () -> service.create("u1", dto));
    }

    @Test
    @DisplayName("create(): duplicado por cercanía (<=5) y mismo título -> ValueConflict")
    void create_DuplicateByProximityAndTitle() throws Exception {
        when(userRepository.findById("u1")).thenReturn(Optional.of(new User()));
        // Alojamiento activo existente en mismas coords y mismo título -> distancia 0
        when(placeRepository.findByState(State.ACTIVE))
                .thenReturn(List.of(makeActivePlaceAt(4.0f, -76.0f, "Casa Centro")));

        var dto = makeCreateDTO(4.0f, -76.0f, "Casa Centro");
        assertThrows(ValueConflictException.class, () -> service.create("u1", dto));
    }

    @Test
    @DisplayName("create(): ok -> mapea, asigna user y guarda")
    void create_Success() throws Exception {
        User u = new User(); u.setId("u1");
        when(userRepository.findById("u1")).thenReturn(Optional.of(u));
        when(placeRepository.findByState(State.ACTIVE)).thenReturn(List.of()); // no dup

        Place mapped = new Place();
        when(placeMapper.toEntity(any(CreatePlaceDTO.class))).thenReturn(mapped);

        var dto = makeCreateDTO(4.1f, -76.1f, "Casa Norte");
        service.create("u1", dto);

        ArgumentCaptor<Place> cap = ArgumentCaptor.forClass(Place.class);
        verify(placeRepository).save(cap.capture());
        assertSame(u, cap.getValue().getUser());
    }

    // ----------------- update() -----------------

    @Test
    @DisplayName("update(): alojamiento inexistente -> ResourceNotFound")
    void update_NotFound() {
        when(placeRepository.findById("a1")).thenReturn(Optional.empty());
        var update = new EditPlaceDTO(
                "Titulo Nuevo", "Descripción nueva suficiente", 4, 200_000.0,
                "Colombia", "Valle", "Cali", "San Antonio", "Cra 1 # 2-3",
                "Z9X8Y7", List.of("https://img/2.jpg"),
                List.copyOf(EnumSet.of(Services.WIFI)), PlaceType.HOUSE
        );
        assertThrows(ResourceNotFoundException.class, () -> service.edit("a1", update));
    }

    @Test
    @DisplayName("update(): ok -> aplica mapper y guarda")
    void update_Success() throws Exception {
        Place acc = new Place(); acc.setId("a1");
        when(placeRepository.findById("a1")).thenReturn(Optional.of(acc));

        var update = new EditPlaceDTO(
                "Titulo Nuevo", "Descripción nueva suficiente", 4, 200_000.0,
                "Colombia", "Valle", "Cali", "San Antonio", "Cra 1 # 2-3",
                "Z9X8Y7", List.of("https://img/2.jpg"),
                List.copyOf(EnumSet.of(Services.WIFI)), PlaceType.HOUSE
        );

        service.edit("a1", update);

        verify(placeMapper).editPlaceFromDto(eq(update), eq(acc));
        verify(placeRepository).save(acc);
    }

    // ----------------- delete() -----------------

    @Test
    @DisplayName("delete(): alojamiento inexistente -> ResourceNotFound")
    void delete_NotFound() {
        when(placeRepository.findById("a1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete("a1"));
    }

    @Test
    @DisplayName("delete(): tiene reservas PENDING -> Unauthorized")
    void delete_WithPendingBooking() {
        Place acc = new Place(); acc.setId("a1");
        when(placeRepository.findById("a1")).thenReturn(Optional.of(acc));
        when(bookingRepository.findByPlaceIdAndBookingState("a1", BookingState.PENDING))
                .thenReturn(Optional.of(new co.edu.uniquindio.application.model.Booking()));

        assertThrows(UnauthorizedException.class, () -> service.delete("a1"));
    }

    @Test
    @DisplayName("delete(): sin PENDING -> pone INACTIVE y guarda")
    void delete_SetsInactiveAndSaves() throws Exception {
        Place acc = new Place(); acc.setId("a1"); acc.setState(State.ACTIVE);
        when(placeRepository.findById("a1")).thenReturn(Optional.of(acc));
        when(bookingRepository.findByPlaceIdAndBookingState("a1", BookingState.PENDING))
                .thenReturn(Optional.empty());

        service.delete("a1");

        assertEquals(State.INACTIVE, acc.getState());
        verify(placeRepository).save(acc);
    }

    @Test
    @DisplayName("listAllAmenities(): alojamiento inexistente -> ResourceNotFound")
    void listAllServices_NotFound() {
        when(placeRepository.findById("a1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.listAllServices("a1"));
    }

    @Test
    @DisplayName("listAllAmenities(): ok -> retorna services del alojamiento")
    void listAllAmenities_ReturnsServices() throws Exception {
        Place acc = new Place(); acc.setId("a1");
        List<Services> amenities = new ArrayList<>();
        amenities.add(Services.WIFI);
        amenities.add(Services.POOL);
        acc.setAmenities(amenities);

        when(placeRepository.findById("a1")).thenReturn(Optional.of(acc));

        var res = service.listAllServices("a1");
        assertEquals(2, res.size());
        assertTrue(res.containsAll(amenities));
    }

    // ----------------- search() -----------------

    /*@Test
    @DisplayName("search(): sin resultados -> ResourceNotFound")
    void search_Empty() {
        ListPlaceDTO filters = new ListPlaceDTO(
                "Cali",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                2
        );
        when(placeRepository.searchPlaces(eq(filters), any(Pageable.class)))
                .thenReturn(Page.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.search(filters, 0));
    }*/

    /*@Test
    @DisplayName("search(): ok -> mapea y devuelve lista")
    void search_ReturnsMappedList() throws Exception {
        ListPlaceDTO filters = new ListPlaceDTO(
                "Cali",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                2
        );

        Place acc = new Place();
        when(placeRepository.searchPlaces(eq(filters), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(acc)));

        when(showPlaceMapper.toPlaceDTO(acc))
                .thenReturn(new PlaceDTO("Casa Centro", 150_000, "https://img/1.jpg", 4.6, "Cali"));

        var result = service.search(filters, 0);

        assertEquals(1, result.size());
        assertEquals("Casa Centro", result.get(0).title());
        verify(showPlaceMapper, times(1)).toPlaceDTO(acc);
    }*/

    // ----------------- listAllAmenities() (método público en la impl) -----------------


    // ----------------- stats() -----------------

    /*@Test
    @DisplayName("stats(): agrega métricas desde repos y delega en mapper")
    void stats_ReturnsFromMapper() throws Exception {
        String accId = "a1";
        StatsDateDTO range = new StatsDateDTO(null, null);

        // Stubs de agregaciones que usa el servicio
        when(commentRepository.findAverageRatingByPlaceId(eq(accId), any(), any())).thenReturn(4.2);
        when(commentRepository.countByPlaceId(eq(accId), any(), any())).thenReturn(7L);
        when(bookingRepository.countByPlaceIdAndBetween(eq(accId), any(), any())).thenReturn(12L);
        when(bookingRepository.findAverageOccupancyByPlaceId(eq(accId), any(), any())).thenReturn(0.65);
        when(bookingRepository.countCancellationsByPlaceId(eq(accId), eq(BookingState.CANCELED), any(), any())).thenReturn(2);
        when(bookingRepository.findAverageRevenueByPlaceId(eq(accId), eq(BookingState.COMPLETED), any(), any())).thenReturn(350.0);

        // El mapper construye PlaceStatsDTO (8 campos). El servicio le pasa 6 valores; el mapper puede setear los otros 2.
        var dto = new PlaceStatsDTO(
                4.2, 7L, 12L, 0.65, 2,
                null, null, // lastReservation, nextAvailableDate
                350.0
        );

        when(statsMapper.toPlaceStatsDTO(
                eq(4.2), eq(7L), eq(12L), eq(0.65), eq(2), eq(350.0)
        )).thenReturn(dto);

        var res = service.stats(accId, range);

        assertNotNull(res);
        assertEquals(4.2, res.averageRating());
        assertEquals(7L, res.totalComments());
        assertEquals(12L, res.totalReservations());
        assertEquals(0.65, res.occupancyRate());
        assertEquals(2, res.cancellations());
        assertEquals(350.0, res.totalRevenue());

        verify(statsMapper).toPlaceStatsDTO(4.2, 7L, 12L, 0.65, 2, 350.0);
    }*/

    // ----------------- listAllAccommodationsHost() -----------------

    /*@Test
    @DisplayName("listAllAccommodationsHost(): mapea el page a DTOs")
    void listAllAccommodationsHost_Maps() throws Exception {
        Place a1 = new Place();
        Place a2 = new Place();

        when(placeRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(a1, a2)));

        when(showPlaceMapper.toPlaceDTO(a1))
                .thenReturn(new PlaceDTO("A1", 100_000, "ph1", 4.0, "Cali"));
        when(showPlaceMapper.toPlaceDTO(a2))
                .thenReturn(new PlaceDTO("A2", 120_000, "ph2", 4.5, "Cali"));

        var list = service.listAllPlacesHost("host-1");

        assertEquals(2, list.size());
        assertEquals("A1", list.get(0).title());
        assertEquals("A2", list.get(1).title());
        verify(placeRepository).findAll(any(Pageable.class));
        verify(showPlaceMapper, times(1)).toPlaceDTO(a1);
        verify(showPlaceMapper, times(1)).toPlaceDTO(a2);
    }*/
}
