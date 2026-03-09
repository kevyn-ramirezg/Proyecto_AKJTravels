package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    Page<Comment> findAllByPlaceId(String placeId, Pageable pageable);
    boolean existsByBookingId(String bookingId);

    @Query("""
    SELECT COALESCE(AVG(c.rating), 0.0)
    FROM Comment c
    WHERE c.place.id = :placeId
      AND (:startDate IS NULL OR c.createdAt >= :startDate)
      AND (:endDate IS NULL OR c.createdAt <= :endDate)
    """)
    Double findAverageRatingByPlaceId(@Param("placeId") String placeId,
                                              @Param("startDate")LocalDateTime startDate,
                                              @Param("endDate")LocalDateTime endDate);


    @Query("""
        SELECT COALESCE(AVG(CAST(c.rating AS double)), 0)
        FROM Comment c
        WHERE c.place.id = :placeId
    """)
    Double avgRatingByPlaceId(@Param("placeId") String placeId);

    @Query("""
        SELECT COALESCE(AVG(CAST(c.rating AS double)), 0)
        FROM Comment c
        WHERE c.place.id = :placeId
          AND c.createdAt >= :from
    """)
    Double avgRatingByPlaceIdFrom(@Param("placeId") String placeId,
                                  @Param("from") LocalDateTime from);

    @Query("""
        SELECT COALESCE(AVG(CAST(c.rating AS double)), 0)
        FROM Comment c
        WHERE c.place.id = :placeId
          AND c.createdAt <= :to
    """)
    Double avgRatingByPlaceIdTo(@Param("placeId") String placeId,
                                @Param("to") LocalDateTime to);

    @Query("""
        SELECT COALESCE(AVG(CAST(c.rating AS double)), 0)
        FROM Comment c
        WHERE c.place.id = :placeId
          AND c.createdAt >= :from
          AND c.createdAt <= :to
    """)
    Double avgRatingByPlaceIdBetween(@Param("placeId") String placeId,
                                     @Param("from") LocalDateTime from,
                                     @Param("to") LocalDateTime to);


    @Query("""
SELECt COUNT(c)
FROM Comment c
where c.place.id = :placeId
AND (:startDate IS NULL OR c.createdAt >= :startDate)
      AND (:endDate IS NULL OR c.createdAt <= :endDate)
""")
    long countByPlaceId(@Param("placeId")String placeId,
                                @Param("startDate")LocalDateTime startDate,
                                @Param("endDate")LocalDateTime endDate);

}