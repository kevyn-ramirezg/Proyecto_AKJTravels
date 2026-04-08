package co.edu.uniquindio.application.dto.favoriteDTO;

import lombok.Builder;

public record FavoritePlaceDTO(
        String id,
        String title,
        String city,
        String address,
        double price,
        String photoUrl,
        double averageRating,
        int capacity
) {}

