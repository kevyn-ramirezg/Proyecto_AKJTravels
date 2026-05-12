package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.favoriteDTO.FavoritePlaceDTO;
import co.edu.uniquindio.application.exceptions.ResourceNotFoundException;
import co.edu.uniquindio.application.model.Favorite;
import co.edu.uniquindio.application.model.Location;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.repositories.FavoriteRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.impl.FavoriteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Test
    @DisplayName("Debe agregar favorito cuando usuario y alojamiento existen y aun no existe")
    void shouldAddFavorite() {
        User user = user("user-1");
        Place place = place("place-1");

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(favoriteRepository.existsByUserAndPlace(user, place)).thenReturn(false);

        favoriteService.addFavorite("user-1", "place-1");

        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("No debe duplicar favorito si ya existe")
    void shouldNotDuplicateExistingFavorite() {
        User user = user("user-1");
        Place place = place("place-1");

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(favoriteRepository.existsByUserAndPlace(user, place)).thenReturn(true);

        favoriteService.addFavorite("user-1", "place-1");

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar favorito existente")
    void shouldRemoveFavorite() {
        User user = user("user-1");
        Place place = place("place-1");
        Favorite favorite = new Favorite();

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(favoriteRepository.findByUserAndPlace(user, place)).thenReturn(Optional.of(favorite));

        favoriteService.removeFavorite("user-1", "place-1");

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    @DisplayName("Debe indicar si un alojamiento es favorito del usuario")
    void shouldCheckIfPlaceIsFavorite() {
        User user = user("user-1");
        Place place = place("place-1");

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(favoriteRepository.existsByUserAndPlace(user, place)).thenReturn(true);

        assertTrue(favoriteService.isMyFavorite("user-1", "place-1"));
    }

    @Test
    @DisplayName("Debe contar favoritos por alojamiento")
    void shouldCountFavoritesByPlace() {
        Place place = place("place-1");
        when(placeRepository.findById("place-1")).thenReturn(Optional.of(place));
        when(favoriteRepository.countByPlace(place)).thenReturn(7L);

        assertEquals(7L, favoriteService.countFavoritesByPlace("place-1"));
    }

    @Test
    @DisplayName("Debe listar favoritos mapeando datos principales del alojamiento")
    void shouldListMyFavorites() {
        User user = user("user-1");
        Place place = place("place-1");
        place.setTitle("Casa campestre");
        place.setPrice(250000.0);
        place.setAverageRatings(4.8);
        place.setCapacity(6);
        place.setPics_url(List.of("https://cdn.example.com/place.jpg"));
        place.setLocation(location());

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setPlace(place);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUser(user, pageable)).thenReturn(new PageImpl<>(List.of(favorite)));

        Page<FavoritePlaceDTO> result = favoriteService.listMyFavorites("user-1", pageable);

        assertEquals(1, result.getTotalElements());
        FavoritePlaceDTO dto = result.getContent().get(0);
        assertEquals("place-1", dto.id());
        assertEquals("Casa campestre", dto.title());
        assertEquals("Armenia", dto.city());
        assertEquals("Calle 10, Centro, Armenia, Quindio, Colombia", dto.address());
        assertEquals("https://cdn.example.com/place.jpg", dto.photoUrl());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el usuario no existe")
    void shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById("missing-user")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.addFavorite("missing-user", "place-1"));

        verify(placeRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe retornar foto nula si el alojamiento favorito no tiene imagenes")
    void shouldReturnNullPhotoWhenPlaceHasNoImages() {
        User user = user("user-1");
        Place place = place("place-1");
        place.setTitle("Apartamento");
        place.setLocation(null);
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setPlace(place);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUser(user, pageable)).thenReturn(new PageImpl<>(List.of(favorite)));

        FavoritePlaceDTO dto = favoriteService.listMyFavorites("user-1", pageable).getContent().get(0);

        assertNull(dto.photoUrl());
        assertEquals("", dto.city());
        assertEquals("", dto.address());
    }

    private User user(String id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Place place(String id) {
        Place place = new Place();
        place.setId(id);
        return place;
    }

    private Location location() {
        Location location = new Location();
        location.setStreet("Calle 10");
        location.setNeighborhood("Centro");
        location.setCity("Armenia");
        location.setDepartment("Quindio");
        location.setCountry("Colombia");
        return location;
    }
}
