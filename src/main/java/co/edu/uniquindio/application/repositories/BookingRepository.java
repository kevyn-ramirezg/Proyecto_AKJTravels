package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.dto.bookingDTO.SearchBookingDTO;
import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.BookingState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String>, JpaSpecificationExecutor<Booking> {

    List<Booking> findByPlaceId(String placeId);

    Optional<Booking> findByPlaceIdAndBookingState(String placeId, BookingState bookingState);

    // ¿Existen reservas futuras activas? (para impedir eliminar Place)
    boolean existsByPlace_IdAndCheckInAfterAndBookingStateIn(
            String placeId,
            LocalDateTime checkIn,
            List<BookingState> states
    );

    List<Booking> findByUser(User user);

    // Conteo de reservas por lugar en un rango (usamos checkIn/checkOut)
    long countByPlaceId(String placeId);

    long countByPlaceIdAndCheckInGreaterThanEqual(String placeId, LocalDateTime from);

    long countByPlaceIdAndCheckOutLessThanEqual(String placeId, LocalDateTime to);

    long countByPlaceIdAndCheckInGreaterThanEqualAndCheckOutLessThanEqual(
            String placeId,
            LocalDateTime from,
            LocalDateTime to
    );

    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.place.id = :placeId
          AND (:#{#filters.state} IS NULL OR b.bookingState = :#{#filters.state})
          AND (:#{#filters.checkIn} IS NULL OR b.checkIn >= :#{#filters.checkIn})
          AND (:#{#filters.checkOut} IS NULL OR b.checkOut <= :#{#filters.checkOut})
          AND (:#{#filters.guest_number} IS NULL OR b.guest_number = :#{#filters.guest_number})
        ORDER BY b.checkIn DESC
        """)
    Page<Booking> findBookingsByPlaceWithFilters(
            @Param("placeId") String placeId,
            @Param("filters") SearchBookingDTO filters,
            Pageable pageable
    );

    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.user.id = :userId
          AND (:#{#filters.state} IS NULL OR b.bookingState = :#{#filters.state})
          AND (:#{#filters.checkIn} IS NULL OR b.checkIn >= :#{#filters.checkIn})
          AND (:#{#filters.checkOut} IS NULL OR b.checkOut <= :#{#filters.checkOut})
          AND (:#{#filters.guest_number} IS NULL OR b.guest_number = :#{#filters.guest_number})
        ORDER BY b.checkIn DESC
        """)
    Page<Booking> findBookingsByUserWithFilters(
            @Param("userId") String userId,
            @Param("filters") SearchBookingDTO filters,
            Pageable pageable
    );

    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.place.id = :placeId
          AND (:startDate IS NULL OR b.createdAt >= :startDate)
          AND (:endDate   IS NULL OR b.createdAt <= :endDate)
        """)
    long countByPlaceIdAndBetween(
            @Param("placeId") String placeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
        SELECT COALESCE(
                   SUM(
                       function(
                           'date_part',
                           'day',
                           function('age', b.checkOut, b.checkIn)
                       )
                   ),
                   0
               )
        FROM Booking b
        WHERE b.place.id = :placeId
          AND b.bookingState = co.edu.uniquindio.application.model.enums.BookingState.COMPLETED
          AND (:startDate IS NULL OR b.checkIn  >= :startDate)
          AND (:endDate   IS NULL OR b.checkOut <= :endDate)
        """)
    Double findAverageOccupancyByPlaceId(
            @Param("placeId") String placeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.place.id = :placeId
          AND b.bookingState = co.edu.uniquindio.application.model.enums.BookingState.CANCELED
          AND (:startDate IS NULL OR b.checkIn  >= :startDate)
          AND (:endDate   IS NULL OR b.checkOut <= :endDate)
        """)
    int countCancellationsByPlaceId(
            @Param("placeId") String placeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
        SELECT COALESCE(SUM(a.price), 0)
        FROM Booking b
        JOIN b.place a
        WHERE a.id = :placeId
          AND b.bookingState = co.edu.uniquindio.application.model.enums.BookingState.COMPLETED
          AND (:startDate IS NULL OR b.checkIn  >= :startDate)
          AND (:endDate   IS NULL OR b.checkOut <= :endDate)
        """)
    Double findAverageRevenueByPlaceId(
            @Param("placeId") String placeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
        SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
        FROM Booking b
        WHERE b.place.id = :placeId
          AND b.checkIn  < :checkOut
          AND b.checkOut > :checkIn
          AND b.bookingState IN ('CONFIRMED', 'PENDING')
        """)
    boolean existsOverlappingBooking(
            @Param("placeId") String placeId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut
    );

    /**
     * Obtiene todas las bookings con estado CONFIRMED cuyo checkOut ya pasó.
     * Usado por el scheduler para actualizar automáticamente a COMPLETED.
     */
    List<Booking> findByBookingStateAndCheckOutBefore(
            BookingState bookingState,
            LocalDateTime checkOut
    );

}
