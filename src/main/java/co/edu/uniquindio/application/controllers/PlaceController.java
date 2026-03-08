package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.bookingDTO.SearchBookingDTO;
import co.edu.uniquindio.application.dto.commentDTO.CommentDTO;
import co.edu.uniquindio.application.dto.commentDTO.CreateCommentDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.placeDTO.*;
import co.edu.uniquindio.application.dto.bookingDTO.BookingDTO;
import co.edu.uniquindio.application.exceptions.BadRequestException;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.enums.Services;
import co.edu.uniquindio.application.services.BookingService;
import co.edu.uniquindio.application.services.CommentService;
import co.edu.uniquindio.application.services.ImageService;
import co.edu.uniquindio.application.services.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final CommentService commentService;
    private final BookingService bookingService;
    private final ImageService imageService;


    @GetMapping("/{page}")
    public ResponseEntity<ResponseDTO<List<PlaceDTO>>> read(@PathVariable int page, @ModelAttribute ListPlaceDTO listPlaceDTO) throws Exception {
        List<PlaceDTO> list = placeService.search(listPlaceDTO, page);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, list));
    }

    @PreAuthorize("hasRole('HOST')")
    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreatePlaceDTO dto) throws Exception {
        String hostId = getCurrentUserId();
        String placeId = placeService.create(hostId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, placeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditPlaceDTO editPlaceDTO) throws Exception {
        placeService.edit(id, editPlaceDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, "alojamiento actualizado "));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id) throws Exception {
        placeService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, "alojamiento eliminado "));
    }

    @GetMapping("/{id}/amenities")
    public ResponseEntity<ResponseDTO<List<Services>>> listServices(@PathVariable String id) throws Exception {
        List<Services> list = placeService.listAllServices(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, list));
    }

    @GetMapping("/{id}/comments/{page}")
    public ResponseEntity<ResponseDTO<List<CommentDTO>>> listComments(@PathVariable String id, @PathVariable int page) throws Exception {
        List<CommentDTO> list = commentService.listComments(id, page);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, list));
    }

    @PostMapping("/{bookingId}/comments")
    public ResponseEntity<ResponseDTO<String>> createComment(@PathVariable String bookingId, @Valid @RequestBody CreateCommentDTO createCommentDTO) throws Exception {
        String userId = getCurrentUserId();
        commentService.createComment(bookingId, userId, createCommentDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, "comentario creado exitosamente"));
    }

    @GetMapping("/{id}/bookings/{page}")
    public ResponseEntity<ResponseDTO<List<BookingDTO>>> listBookings(@PathVariable String id, @PathVariable int page, @Valid @RequestBody SearchBookingDTO searchBookingDTO) throws Exception {
        List<BookingDTO> list = bookingService.listBookings(id, page, searchBookingDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, list));
    }


    @GetMapping("/{placeId}/stats")
    public ResponseEntity<ResponseDTO<PlaceStatsDTO>> stats(
            @PathVariable String placeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) throws Exception {
        PlaceStatsDTO dto = placeService.stats(placeId, from, to);
        return ResponseEntity.ok(new ResponseDTO<>(false, dto));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ResponseDTO<PlaceDetailDTO>> get(@PathVariable String id) throws Exception {
        PlaceDetailDTO placeDetailDTO = placeService.get(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, placeDetailDTO));
    }

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

        // Subir a Cloudinary (o tu provider) usando ImageService
        List<String> urls = new ArrayList<>();
        for (MultipartFile f : files) {
            Object secure = imageService.upload(f).get("secure_url"); // tu ImageServiceImpl ya retorna ese mapa
            if (secure == null) throw new BadRequestException("No se obtuvo URL segura de la imagen");
            urls.add(secure.toString());
        }

        // Poner la imagen principal al inicio (índice 0)
        if (mainIndex != 0) {
            Collections.swap(urls, 0, mainIndex);
        }

        // Guardar en el Place (pics_url) con la principal en index 0
        Place place = placeService.setImages(placeId, urls);

        // Si tienes PlaceMapper, responde el DTO:
        // return ResponseEntity.ok(new ResponseDTO<>(false, placeMapper.toDTO(place)));

        // Si no tienes PlaceMapper, responde la entidad directamente:
        return ResponseEntity.ok(new ResponseDTO<>(false, place));
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Object principal = authentication.getPrincipal();

        // Si tu UserDetails personalizado devuelve el ID del usuario:
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername(); // o getId() si lo implementaste así
        }

        // Si el token guarda el ID directamente como String
        return principal.toString();
    }
    @PreAuthorize("hasRole('HOST')")
    @GetMapping("/me/{page}")
    public ResponseEntity<ResponseDTO<List<PlaceDTO>>> listMine(@PathVariable int page) throws Exception {
        String id = getCurrentUserId();
        List<PlaceDTO> result = placeService.listAllPlacesHost(id, page);
        return ResponseEntity.ok(new ResponseDTO<>(false, result));
    }
}