package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.Favorite;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndPlace(User user, Place place);

    Optional<Favorite> findByUserAndPlace(User user, Place place);

    @EntityGraph(attributePaths = {"place"})
    Page<Favorite> findByUser(User user, Pageable pageable);

    long countByPlace(Place place);

    @Query("""
      SELECT COUNT(f)
      FROM Favorite f
      WHERE f.place.id = :placeId
        AND (:from IS NULL OR f.createdAt >= :from)
        AND (:to   IS NULL OR f.createdAt <= :to)
    """)
    long countByPlaceIdBetween(@Param("placeId") String placeId,
                               @Param("from") LocalDateTime from,
                               @Param("to") LocalDateTime to);
}
