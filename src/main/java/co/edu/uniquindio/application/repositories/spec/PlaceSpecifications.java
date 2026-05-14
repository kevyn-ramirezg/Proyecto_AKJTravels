package co.edu.uniquindio.application.repositories.spec;

import co.edu.uniquindio.application.dto.placeDTO.ListPlaceDTO;
import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.enums.BookingState;
import co.edu.uniquindio.application.model.enums.Services;
import co.edu.uniquindio.application.model.enums.State;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PlaceSpecifications {
    private PlaceSpecifications() {}

    public static Specification<Place> withFilters(ListPlaceDTO dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("state"), State.ACTIVE));

            if (dto.getCity() != null && !dto.getCity().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("location").get("city")), dto.getCity().toLowerCase()));
            }

            if (dto.getGuest_number() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), dto.getGuest_number()));
            }

            if (dto.getMinimum() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), dto.getMinimum()));
            }

            if (dto.getMaximum() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), dto.getMaximum()));
            }

            if (dto.getList() != null && !dto.getList().isEmpty()) {
                for (Services service : dto.getList()) {
                    predicates.add(cb.isMember(service, root.get("amenities")));
                }
            }

            if (dto.getCheckIn() != null && dto.getCheckOut() != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Booking> booking = subquery.from(Booking.class);

                subquery.select(cb.literal(1L));
                subquery.where(
                        cb.equal(booking.get("place"), root),
                        booking.get("bookingState").in(BookingState.PENDING, BookingState.CONFIRMED),
                        cb.lessThan(booking.get("checkIn"), dto.getCheckOut()),
                        cb.greaterThan(booking.get("checkOut"), dto.getCheckIn())
                );

                predicates.add(cb.not(cb.exists(subquery)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
