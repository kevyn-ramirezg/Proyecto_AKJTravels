package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.bookingDTO.*;
import co.edu.uniquindio.application.model.enums.BookingState;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    void create(String id, String userId, CreateBookingDTO createBookingDTO) throws Exception;
    void delete(String id) throws Exception;
    void rejectByHost(String bookingId) throws Exception;
    List<BookingDTO> listBookings(String id, int page, SearchBookingDTO searchBookingDTO) throws Exception;
    List<BookingDTO> listBookingsUser(String id, int page, SearchBookingDTO searchBookingDTO) throws Exception;
    // Nuevo, requerido por el enunciado (no rompe tus llamadas existentes)
    void confirm(String bookingId) throws Exception;

    List<BookingListItemDTO> listByPlace(String placeId,
                                         BookingState state,
                                         LocalDateTime from,
                                         LocalDateTime to,
                                         Integer guests) throws Exception;

    List<UserBookingDTO> listUserBookings(String userId) throws Exception;
}
