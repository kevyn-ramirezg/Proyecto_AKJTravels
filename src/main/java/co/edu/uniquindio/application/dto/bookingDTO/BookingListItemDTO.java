package co.edu.uniquindio.application.dto.bookingDTO;

import co.edu.uniquindio.application.dto.userDTO.UserDTO;
import co.edu.uniquindio.application.model.enums.BookingState;

import java.time.LocalDateTime;

public record BookingListItemDTO(
        String id,
        BookingState bookingState,
        UserDTO user,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        int guest_number,
        boolean hasBeenRated
) {}