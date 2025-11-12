package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.bookingDTO.BookingDTO;
import co.edu.uniquindio.application.dto.bookingDTO.CreateBookingDTO;
import co.edu.uniquindio.application.dto.bookingDTO.SearchBookingDTO;
import co.edu.uniquindio.application.dto.commentDTO.CommentDTO;
import co.edu.uniquindio.application.dto.commentDTO.CreateCommentDTO;
import co.edu.uniquindio.application.dto.placeDTO.*;
import co.edu.uniquindio.application.exceptions.BadRequestException;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.enums.BookingState;
import co.edu.uniquindio.application.model.enums.Services;
import co.edu.uniquindio.application.services.BookingService;
import co.edu.uniquindio.application.services.CommentService;
import co.edu.uniquindio.application.services.ImageService;
import co.edu.uniquindio.application.services.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

  private final PlaceService placeService;
  private final CommentService commentService;
  private final BookingService bookingService;
  private final ImageService imageService;

  // --------------------------------------------------------------------
  // GET /api/places  (paginado por query)
  // --------------------------------------------------------------------
  @GetMapping
  public ResponseEntity<ResponseDTO<Map<String, Object>>> search(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String city,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut,
    @RequestParam(required = false, name = "guestNumber") Integer guestNumber,
    @RequestParam(required = false) Double minimum,
    @RequestParam(required = false) Double maximum,
    @RequestParam(required = false, name = "amenities") String amenitiesCsv
  ) throws Exception {

    ListPlaceDTO filters = new ListPlaceDTO();
    filters.setCity(city);
    filters.setCheckIn(checkIn);
    filters.setCheckOut(checkOut);
    filters.setGuest_number(guestNumber);
    filters.setMinimum(minimum);
    filters.setMaximum(maximum);

    // Parseo opcional de amenities separados por coma → enum Services
    if (amenitiesCsv != null && !amenitiesCsv.isBlank()) {
      List<Services> amenities = Arrays.stream(amenitiesCsv.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(String::toUpperCase)
        .map(Services::valueOf)
        .collect(Collectors.toList());
      filters.setList(amenities);
    }

    // Reusa tu servicio actual (lista simple por página "legacy")
    List<PlaceDTO> list = placeService.search(filters, page);

    // Envolver como "página" para calzar con la respuesta del spec #2
    Page<PlaceDTO> pageData = new PageImpl<>(list, PageRequest.of(page, size), list.size());

    Map<String, Object> payload = Map.of(
      "content", pageData.getContent(),
      "totalElements", pageData.getTotalElements(),
      "totalPages", pageData.getTotalPages(),
      "size", pageData.getSize(),
      "number", pageData.getNumber()
    );
    return ResponseEntity.ok(new ResponseDTO<>(false, payload));
  }

  // --------------------------------------------------------------------
  // POST /api/places
  // --------------------------------------------------------------------
  @PreAuthorize("hasRole('HOST')")
  @PostMapping
  public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreatePlaceDTO createPlaceDTO) throws Exception {
    String userId = getCurrentUserId();
    placeService.create(userId, createPlaceDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "alojamiento creado"));
  }

  // --------------------------------------------------------------------
  // PUT /api/places/{id}
  // --------------------------------------------------------------------
  @PutMapping("/{id}")
  public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditPlaceDTO editPlaceDTO) throws Exception {
    placeService.edit(id, editPlaceDTO);
    return ResponseEntity.ok(new ResponseDTO<>(false, "alojamiento actualizado"));
  }

  // --------------------------------------------------------------------
  // DELETE /api/places/{id}
  // --------------------------------------------------------------------
  @DeleteMapping("/{id}")
  public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
    placeService.delete(id);
    return ResponseEntity.ok(new ResponseDTO<>(false, "alojamiento eliminado"));
  }

  // --------------------------------------------------------------------
  // GET /api/places/{id}/amenities
  // --------------------------------------------------------------------
  @GetMapping("/{id}/amenities")
  public ResponseEntity<ResponseDTO<List<Services>>> listServices(@PathVariable String id) throws Exception {
    List<Services> list = placeService.listAllServices(id);
    return ResponseEntity.ok(new ResponseDTO<>(false, list));
  }

  // --------------------------------------------------------------------
  // GET /api/places/{id}/comments  (paginado por query)
  // --------------------------------------------------------------------
  @GetMapping("/{id}/comments")
  public ResponseEntity<ResponseDTO<Map<String, Object>>> listCommentsPage(
    @PathVariable String id,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) throws Exception {

    // Reuso de tu servicio actual (lista simple por página legacy)
    List<CommentDTO> list = commentService.listComments(id, page);

    Page<CommentDTO> pageData = new PageImpl<>(list, PageRequest.of(page, size), list.size());

    Map<String, Object> payload = Map.of(
      "content", pageData.getContent(),
      "totalElements", pageData.getTotalElements(),
      "totalPages", pageData.getTotalPages(),
      "size", pageData.getSize(),
      "number", pageData.getNumber()
    );
    return ResponseEntity.ok(new ResponseDTO<>(false, payload));
  }

  // --------------------------------------------------------------------
  // POST /api/places/{bookingId}/comments
  // --------------------------------------------------------------------
  @PostMapping("/{bookingId}/comments")
  public ResponseEntity<ResponseDTO<String>> createComment(@PathVariable String bookingId,
                                                           @Valid @RequestBody CreateCommentDTO dto) throws Exception {
    String userId = getCurrentUserId();
    commentService.createComment(bookingId, userId, dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "comentario creado exitosamente"));
  }

  // --------------------------------------------------------------------
  // GET /api/places/{id}/bookings  (paginado por query, con state)
  // --------------------------------------------------------------------
  @GetMapping("/{id}/bookings")
  public ResponseEntity<ResponseDTO<Map<String, Object>>> listBookingsPage(
    @PathVariable String id,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) BookingState state
  ) throws Exception {

    // Tu DTO requiere (state, from, to, guest_number)
    SearchBookingDTO filter = new SearchBookingDTO(state, null, null, null);

    List<BookingDTO> rows = bookingService.listBookings(id, page, filter);

    Page<BookingDTO> pageData = new PageImpl<>(rows, PageRequest.of(page, size), rows.size());

    Map<String, Object> payload = Map.of(
      "content", pageData.getContent(),
      "totalElements", pageData.getTotalElements(),
      "totalPages", pageData.getTotalPages(),
      "size", pageData.getSize(),
      "number", pageData.getNumber()
    );
    return ResponseEntity.ok(new ResponseDTO<>(false, payload));
  }

  // --------------------------------------------------------------------
  // GET /api/places/{placeId}/stats
  // --------------------------------------------------------------------
  @GetMapping("/{placeId}/stats")
  public ResponseEntity<ResponseDTO<PlaceStatsDTO>> stats(
    @PathVariable String placeId,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
  ) throws Exception {
    PlaceStatsDTO dto = placeService.stats(placeId, from, to);
    return ResponseEntity.ok(new ResponseDTO<>(false, dto));
  }

  // --------------------------------------------------------------------
  // GET /api/places/{id}/detail
  // --------------------------------------------------------------------
  @GetMapping("/{id}/detail")
  public ResponseEntity<ResponseDTO<PlaceDetailDTO>> get(@PathVariable String id) throws Exception {
    PlaceDetailDTO placeDetailDTO = placeService.get(id);
    return ResponseEntity.ok(new ResponseDTO<>(false, placeDetailDTO));
  }

  // --------------------------------------------------------------------
  // POST /api/places/{placeId}/images
  // --------------------------------------------------------------------
  @PostMapping("/{placeId}/images")
  public ResponseEntity<ResponseDTO<?>> uploadPlaceImages(
    @PathVariable String placeId,
    @RequestPart("files") List<MultipartFile> files,
    @RequestParam("mainIndex") Integer mainIndex) throws Exception {

    if (files == null || files.isEmpty() || files.size() > 10) {
      throw new BadRequestException("Debe subir entre 1 y 10 imágenes");
    }
    if (mainIndex == null || mainIndex < 0 || mainIndex >= files.size()) {
      throw new BadRequestException("mainIndex fuera de rango");
    }

    List<String> urls = new ArrayList<>();
    for (MultipartFile f : files) {
      Object secure = imageService.upload(f).get("secure_url");
      if (secure == null) throw new BadRequestException("No se obtuvo URL segura de la imagen");
      urls.add(secure.toString());
    }
    if (mainIndex != 0) Collections.swap(urls, 0, mainIndex);

    Place place = placeService.setImages(placeId, urls);
    return ResponseEntity.ok(new ResponseDTO<>(false, place));
  }

  // --------------------------------------------------------------------
  // POST /api/places/{placeId}/bookings
  // --------------------------------------------------------------------
  @PostMapping("/{placeId}/bookings")
  public ResponseEntity<ResponseDTO<String>> createBooking(@PathVariable String placeId,
                                                           @Valid @RequestBody CreateBookingDTO dto) throws Exception {
    String userId = getCurrentUserId();
    bookingService.create(placeId, userId, dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "reserva creada"));
  }


  private String getCurrentUserId() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) throw new RuntimeException("Usuario no autenticado");
    var principal = auth.getPrincipal();
    if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) return ud.getUsername();
    return principal.toString();
  }
}
