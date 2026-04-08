package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.favoriteDTO.FavoritePlaceDTO;
import co.edu.uniquindio.application.exceptions.ResourceNotFoundException;
import co.edu.uniquindio.application.model.Favorite;
import co.edu.uniquindio.application.model.Location;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.repositories.FavoriteRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

  private final FavoriteRepository favoriteRepository;
  private final UserRepository userRepository;
  private final PlaceRepository placeRepository;

  @Override
  @Transactional
  public void addFavorite(String userId, String placeId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario"));

    Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el alojamiento"));

    if (favoriteRepository.existsByUserAndPlace(user, place)) {
      return;
    }

    Favorite favorite = new Favorite();
    favorite.setUser(user);
    favorite.setPlace(place);

    favoriteRepository.save(favorite);
  }

  @Override
  @Transactional
  public void removeFavorite(String userId, String placeId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario"));

    Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el alojamiento"));

    favoriteRepository.findByUserAndPlace(user, place)
            .ifPresent(favoriteRepository::delete);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isMyFavorite(String userId, String placeId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario"));

    Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el alojamiento"));

    return favoriteRepository.existsByUserAndPlace(user, place);
  }

  @Override
  @Transactional(readOnly = true)
  public long countFavoritesByPlace(String placeId) {

    Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el alojamiento"));

    return favoriteRepository.countByPlace(place);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<FavoritePlaceDTO> listMyFavorites(String userId, Pageable pageable) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario"));

    Page<Favorite> favPage = favoriteRepository.findByUser(user, pageable);

    return favPage.map(favorite -> mapToFavoritePlaceDTO(favorite.getPlace()));
  }

  private FavoritePlaceDTO mapToFavoritePlaceDTO(Place place) {
    return new FavoritePlaceDTO(
            place.getId(),
            place.getTitle(),
            place.getLocation() != null ? place.getLocation().getCity() : "",
            buildAddress(place.getLocation()),
            place.getPrice(),
            getMainPhoto(place),
            place.getAverageRatings(),
            place.getCapacity()
    );
  }
  private String buildAddress(Location location) {
    if (location == null) return "";

    StringBuilder sb = new StringBuilder();

    if (location.getStreet() != null && !location.getStreet().isBlank()) {
      sb.append(location.getStreet());
    }

    if (location.getNeighborhood() != null && !location.getNeighborhood().isBlank()) {
      if (!sb.isEmpty()) sb.append(", ");
      sb.append(location.getNeighborhood());
    }

    if (location.getCity() != null && !location.getCity().isBlank()) {
      if (!sb.isEmpty()) sb.append(", ");
      sb.append(location.getCity());
    }

    if (location.getDepartment() != null && !location.getDepartment().isBlank()) {
      if (!sb.isEmpty()) sb.append(", ");
      sb.append(location.getDepartment());
    }

    if (location.getCountry() != null && !location.getCountry().isBlank()) {
      if (!sb.isEmpty()) sb.append(", ");
      sb.append(location.getCountry());
    }

    return sb.toString();
  }

  private String getMainPhoto(Place place) {
    return (place.getPics_url() != null && !place.getPics_url().isEmpty())
            ? place.getPics_url().get(0)
            : null;
  }
}