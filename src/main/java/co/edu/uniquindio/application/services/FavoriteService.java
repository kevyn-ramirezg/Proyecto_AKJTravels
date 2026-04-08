package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.favoriteDTO.FavoritePlaceDTO;
import co.edu.uniquindio.application.model.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {

    void addFavorite(String userId, String placeId);
    void removeFavorite(String userId, String placeId);
    Page<FavoritePlaceDTO> listMyFavorites(String userId, Pageable pageable);
    boolean isMyFavorite(String userId, String placeId);
    long countFavoritesByPlace(String placeId);

}