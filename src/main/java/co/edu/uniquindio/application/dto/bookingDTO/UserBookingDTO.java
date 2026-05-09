package co.edu.uniquindio.application.dto.bookingDTO;

import co.edu.uniquindio.application.model.enums.BookingState;
import java.time.LocalDate;

public record UserBookingDTO(
        String id,
        BookingState bookingState,
        LocalDate checkIn,
        LocalDate checkOut,
        int guest_number,
        String placeId,
        String placeTitle,
        String mainImage,
        int capacity,
        boolean hasBeenRated
) { }
