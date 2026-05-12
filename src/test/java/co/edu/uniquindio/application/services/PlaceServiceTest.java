package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.placeDTO.CreatePlaceDTO;
import co.edu.uniquindio.application.dto.placeDTO.EditPlaceDTO;
import co.edu.uniquindio.application.dto.placeDTO.ListPlaceDTO;
import co.edu.uniquindio.application.dto.placeDTO.PlaceDTO;
import co.edu.uniquindio.application.dto.placeDTO.PlaceDetailDTO;
import co.edu.uniquindio.application.dto.placeDTO.PlaceStatsDTO;
import co.edu.uniquindio.application.exceptions.BadRequestException;
import co.edu.uniquindio.application.exceptions.ResourceNotFoundException;
import co.edu.uniquindio.application.exceptions.UnauthorizedException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.mappers.PlaceDetailMapper;
import co.edu.uniquindio.application.mappers.PlaceMapper;
import co.edu.uniquindio.application.mappers.ShowPlaceMapper;
import co.edu.uniquindio.application.mappers.StatsMapper;
import co.edu.uniquindio.application.model.Coordinates;
import co.edu.uniquindio.application.model.Location;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.BookingState;
import co.edu.uniquindio.application.model.enums.PlaceType;
import co.edu.uniquindio.application.model.enums.Role;
import co.edu.uniquindio.application.model.enums.Services;
import co.edu.uniquindio.application.model.enums.State;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.CommentRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.impl.CurrentUserServiceImpl;
import co.edu.uniquindio.application.services.impl.PlaceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock private CurrentUserServiceImpl currentUserService;
    @Mock private PlaceMapper placeMapper;
    @Mock private ShowPlaceMapper showPlaceMapper;
    @Mock private PlaceRepository placeRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private StatsMapper statsMapper;
    @Mock private PlaceDetailMapper placeDetailMapper;

    private PlaceServiceImpl placeService;

    @BeforeEach
    void setUp() {
        placeService = new PlaceServiceImpl(
                currentUserService,
                placeMapper,
                showPlaceMapper,
                placeRepository,
                bookingRepository,
                userRepository,
                commentRepository,
                statsMapper,
                placeDetailMapper
        );
    }

    @Test
    @DisplayName("Debe crear alojamiento cuando el anfitrión existe y no hay duplicado cercano")
    void createPlace() throws Exception {
        User host = user("host-1");
        CreatePlaceDTO dto = createPlaceDTO("Casa Campestre");
        Place place = place("place-1", host, "Casa Campestre");

        when(userRepository.findById("host-1")).thenReturn(Optional.of(host));
        when(placeRepository.findByState(State.ACTIVE)).thenReturn(List.of());
        when(placeMapper.toEntity(dto)).thenReturn(place);
        when(placeRepository.save(place)).thenReturn(place);

        String id = placeService.create("host-1", dto);

        assertEquals("place-1", id);
        assertSame(host, place.getUser());
        verify(placeRepository).save(place);
    }

    @Test
    @DisplayName("Debe rechazar creación si el usuario no existe")
    void createPlaceRejectsUnknownUser() {
        when(userRepository.findById("host-1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> placeService.create("host-1", createPlaceDTO("Casa Campestre")));
        verify(placeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar creación de alojamiento duplicado por título y cercanía")
    void createPlaceRejectsDuplicatedNearbyPlace() {
        User host = user("host-1");
        CreatePlaceDTO dto = createPlaceDTO("Casa Campestre");

        Place existing = place("place-existing", host, "casa campestre");
        existing.setLocation(new Location(
                "Colombia",
                "Quindío",
                "Armenia",
                "Centro",
                "Calle 1",
                "630001",
                new Coordinates(4.5f, -75.7f)
        ));

        when(userRepository.findById("host-1")).thenReturn(Optional.of(host));
        when(placeRepository.findByState(State.ACTIVE)).thenReturn(List.of(existing));

        assertThrows(ValueConflictException.class, () -> placeService.create("host-1", dto));

        verify(placeRepository, never()).save(any());
        verify(placeMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("Debe editar alojamiento cuando el usuario actual es propietario")
    void editOwnedPlace() throws Exception {
        User host = user("host-1");
        Place place = place("place-1", host, "Casa Campestre");
        EditPlaceDTO dto = editPlaceDTO();

        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(currentUserService.getCurrentUser()).thenReturn("host-1");

        placeService.edit("place-1", dto);

        verify(placeMapper).editPlaceFromDto(dto, place);
        verify(placeRepository).save(place);
    }

    @Test
    @DisplayName("Debe rechazar edición de alojamiento ajeno")
    void editRejectsDifferentOwner() {
        Place place = place("place-1", user("host-1"), "Casa Campestre");
        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(currentUserService.getCurrentUser()).thenReturn("other-host");

        assertThrows(UnauthorizedException.class, () -> placeService.edit("place-1", editPlaceDTO()));
        verify(placeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar lógicamente alojamiento propio sin reservas futuras activas")
    void deleteOwnedPlace() throws Exception {
        Place place = place("place-1", user("host-1"), "Casa Campestre");
        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(currentUserService.getCurrentUser()).thenReturn("host-1");
        when(bookingRepository.existsByPlace_IdAndCheckInAfterAndBookingStateIn(eq("place-1"), any(LocalDateTime.class), eq(List.of(BookingState.PENDING, BookingState.CONFIRMED)))).thenReturn(false);

        placeService.delete("place-1");

        assertEquals(State.DELETED, place.getState());
        verify(placeRepository).save(place);
    }

    @Test
    @DisplayName("Debe impedir eliminación si hay reservas futuras activas")
    void deleteRejectsFutureActiveBookings() {
        Place place = place("place-1", user("host-1"), "Casa Campestre");
        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(currentUserService.getCurrentUser()).thenReturn("host-1");
        when(bookingRepository.existsByPlace_IdAndCheckInAfterAndBookingStateIn(eq("place-1"), any(LocalDateTime.class), anyList())).thenReturn(true);

        assertThrows(ValueConflictException.class, () -> placeService.delete("place-1"));
        verify(placeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe buscar alojamientos con filtros válidos")
    void searchPlaces() throws Exception {
        ListPlaceDTO filters = new ListPlaceDTO();
        filters.setCity("Armenia");
        filters.setMinimum(100.0);
        filters.setMaximum(300.0);
        Place place = place("place-1", user("host-1"), "Casa Campestre");
        PlaceDTO dto = new PlaceDTO("place-1", "Casa Campestre", 200.0, "https://img.com/a.jpg", State.ACTIVE, 4.5, "Armenia");

        when(placeRepository.searchPlaces(eq(filters), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(place)));
        when(showPlaceMapper.toPlaceDTO(place)).thenReturn(dto);

        List<PlaceDTO> result = placeService.search(filters, 0);

        assertEquals(1, result.size());
        assertEquals("place-1", result.get(0).id());
    }

    @Test
    @DisplayName("Debe rechazar búsqueda cuando precio mínimo supera precio máximo")
    void searchRejectsInvalidPriceRange() {
        ListPlaceDTO filters = new ListPlaceDTO();
        filters.setMinimum(500.0);
        filters.setMaximum(100.0);

        assertThrows(BadRequestException.class, () -> placeService.search(filters, 0));
        verify(placeRepository, never()).searchPlaces(any(), any());
    }

    @Test
    @DisplayName("Debe rechazar búsqueda sin resultados")
    void searchRejectsEmptyResults() {
        ListPlaceDTO filters = new ListPlaceDTO();
        when(placeRepository.searchPlaces(eq(filters), any(Pageable.class))).thenReturn(Page.<Place>empty());

        assertThrows(ResourceNotFoundException.class, () -> placeService.search(filters, 0));
    }

    @Test
    @DisplayName("Debe obtener detalle de alojamiento")
    void getPlaceDetail() throws Exception {
        Place place = place("place-1", user("host-1"), "Casa Campestre");
        PlaceDetailDTO dto = new PlaceDetailDTO("place-1", 4.5, -75.7, 200.0, List.of("https://img.com/a.jpg"), "Descripción larga", List.of(Services.WIFI), "Casa Campestre", 4, 4.5, "Calle 1", "Centro", "Armenia", "Quindío", "Colombia", "630001", null);

        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(placeDetailMapper.toPlaceDetailDTO(place)).thenReturn(dto);

        PlaceDetailDTO result = placeService.get("place-1");

        assertEquals("place-1", result.id());
        assertEquals("Casa Campestre", result.title());
    }

    @Test
    @DisplayName("Debe calcular estadísticas sin rango de fechas")
    void statsWithoutDateRange() throws Exception {
        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place("place-1", user("host-1"), "Casa Campestre")));
        when(bookingRepository.countByPlaceId("place-1")).thenReturn(3L);
        when(commentRepository.avgRatingByPlaceId("place-1")).thenReturn(4.25);

        PlaceStatsDTO result = placeService.stats("place-1", null, null);

        assertEquals(3L, result.reservations());
        assertEquals(4.25, result.averageRating());
    }

    @Test
    @DisplayName("Debe guardar URLs de imágenes cuando el usuario actual es propietario")
    void setImages() throws Exception {
        Place place = place("place-1", user("host-1"), "Casa Campestre");
        List<String> urls = List.of("https://img.com/a.jpg", "https://img.com/b.png");

        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(currentUserService.getCurrentUser()).thenReturn("host-1");
        when(placeRepository.save(place)).thenReturn(place);

        Place result = placeService.setImages("place-1", urls);

        assertSame(place, result);
        assertEquals(urls, place.getPics_url());
        verify(placeRepository).save(place);
    }

    @Test
    @DisplayName("Debe rechazar más de diez imágenes")
    void setImagesRejectsMoreThanTenUrls() {
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < 11; i++) urls.add("https://img.com/" + i + ".jpg");

        assertThrows(BadRequestException.class, () -> placeService.setImages("place-1", urls));
        verify(placeRepository, never()).save(any());
    }

    private CreatePlaceDTO createPlaceDTO(String title) {
        return new CreatePlaceDTO(
                title,
                "Descripción suficientemente larga para validar el alojamiento",
                200.0,
                List.of("https://img.com/a.jpg"),
                PlaceType.HOUSE,
                4,
                "Colombia",
                "Quindío",
                "Armenia",
                "Centro",
                "Calle 1",
                "630001",
                List.of(Services.WIFI, Services.PARKING),
                4.5f,
                -75.7f
        );
    }

    private EditPlaceDTO editPlaceDTO() {
        return new EditPlaceDTO(
                "Casa Editada",
                "Descripción editada suficientemente larga",
                5,
                250.0,
                "Colombia",
                "Quindío",
                "Armenia",
                "Centro",
                "Calle 2",
                "630002",
                List.of("https://img.com/edit.jpg"),
                List.of(Services.WIFI),
                PlaceType.HOUSE
        );
    }

    private Place place(String id, User owner, String title) {
        Place place = new Place();
        place.setId(id);
        place.setTitle(title);
        place.setDescription("Descripción suficientemente larga");
        place.setPrice(200.0);
        place.setPics_url(new ArrayList<>(List.of("https://img.com/a.jpg")));
        place.setAmenities(new ArrayList<>(List.of(Services.WIFI, Services.PARKING)));
        place.setCapacity(4);
        place.setState(State.ACTIVE);
        place.setTotalRatings(0);
        place.setAverageRatings(4.5);
        place.setPlaceType(PlaceType.HOUSE);
        place.setUser(owner);
        place.setCreatedAt(LocalDateTime.now().minusDays(1));
        place.setLocation(new Location("Colombia", "Quindío", "Armenia", "Centro", "Calle 1", "630001", new Coordinates(4.5f, -75.7f)));
        place.setComments(new ArrayList<>());
        return place;
    }

    private User user(String id) {
        User user = new User();
        user.setId(id);
        user.setName("Host");
        user.setLastName("Demo");
        user.setEmail(id + "@test.com");
        user.setPhone("3001234567");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCountry("Colombia");
        user.setRole(Role.HOST);
        user.setPassword("encoded-password");
        user.setState(State.ACTIVE);
        user.setCreatedAt(LocalDateTime.now().minusDays(1));
        user.setIsHost(true);
        return user;
    }
}
