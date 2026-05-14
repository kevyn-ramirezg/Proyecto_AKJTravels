package co.edu.uniquindio.application.repositories.spec;

import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.enums.BookingState;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class BookingSpecifications {
    private BookingSpecifications(){}

    public static Specification<Booking> byPlaceId(String placeId) {
        return (root, q, cb) -> cb.equal(root.get("place").get("id"), placeId);
    }

    public static Specification<Booking> byUserId(String userId) {
        return (root, q, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Booking> withState(BookingState state) {
        return (root, q, cb) -> state == null ? cb.conjunction() : cb.equal(root.get("bookingState"), state);
    }

    public static Specification<Booking> fromDate(LocalDateTime from) {
        return (root, q, cb) -> from == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("checkIn"), from);
    }

    public static Specification<Booking> toDate(LocalDateTime to) {
        return (root, q, cb) -> to == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("checkOut"), to);
    }

    public static Specification<Booking> withGuests(Integer guests) {
        return (root, q, cb) -> guests == null ? cb.conjunction() : cb.equal(root.get("guest_number"), guests);
    }
}
