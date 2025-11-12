package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.bookingDTO.BookingListItemDTO;
import co.edu.uniquindio.application.dto.bookingDTO.CreateBookingDTO;
import co.edu.uniquindio.application.model.enums.BookingState;
import co.edu.uniquindio.application.services.BookingService;
import co.edu.uniquindio.application.services.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final CurrentUserService currentUserService;

  @Deprecated
  @PostMapping("/{id}") // LEGACY: id = placeId
  public ResponseEntity<ResponseDTO<String>> create(@PathVariable String id, @Valid @RequestBody CreateBookingDTO dto) throws Exception {
    String userId = currentUserService.getCurrentUser();
    bookingService.create(id, userId, dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "reserva creada"));
  }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
        bookingService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, "reserva eliminada"));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ResponseDTO<String>> confirm(@PathVariable String id) throws Exception {
        bookingService.confirm(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, "reserva confirmada"));
    }

    @GetMapping("/{placeId}/bookings")
    public ResponseEntity<ResponseDTO<List<BookingListItemDTO>>> listBookingsByPlace(
            @PathVariable String placeId,
            @RequestParam(required = false) BookingState state,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false, name = "guest_number") Integer guests
    ) throws Exception {
        var rows = bookingService.listByPlace(placeId, state, from, to, guests);
        return ResponseEntity.ok(new ResponseDTO<>(false, rows));
    }
}
