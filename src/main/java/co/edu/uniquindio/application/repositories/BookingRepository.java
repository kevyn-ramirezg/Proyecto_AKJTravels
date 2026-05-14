package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.BookingState;
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

    boolean existsByPlace_IdAndCheckInAfterAndBookingStateIn(
            String placeId,
            LocalDateTime checkIn,
            List<BookingState> states
    );

    List<Booking> findByUser(User user);

    long countByPlaceId(String placeId);

    long countByPlaceIdAndCheckInGreaterThanEqual(String placeId, LocalDateTime from);

    long countByPlaceIdAndCheckOutLessThanEqual(String placeId, LocalDateTime to);

    long countByPlaceIdAndCheckInGreaterThanEqualAndCheckOutLessThanEqual(
            String placeId,
            LocalDateTime from,
            LocalDateTime to
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

    List<Booking> findByBookingStateAndCheckOutBefore(
            BookingState bookingState,
            LocalDateTime checkOut
    );

    List<Booking> findByBookingStateAndCheckInLessThanEqual(
            BookingState bookingState,
            LocalDateTime checkIn
    );
}
