package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.Favorite;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndPlace(User user, Place place);

    Optional<Favorite> findByUserAndPlace(User user, Place place);

    @EntityGraph(attributePaths = {"place"})
    Page<Favorite> findByUser(User user, Pageable pageable);

    long countByPlace(Place place);

    long countByPlaceId(String placeId);

    long countByPlaceIdAndCreatedAtGreaterThanEqual(String placeId, LocalDateTime from);

    long countByPlaceIdAndCreatedAtLessThanEqual(String placeId, LocalDateTime to);

    long countByPlaceIdAndCreatedAtBetween(String placeId, LocalDateTime from, LocalDateTime to);
}
