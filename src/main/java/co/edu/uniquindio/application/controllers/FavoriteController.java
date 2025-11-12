package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.services.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }


    /** Marca un lugar como favorito (idempotente). */

    @PostMapping("/{placeId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDTO<String>> add(Authentication auth, @PathVariable String placeId) {
      favoriteService.addFavorite(auth.getName(), placeId);
      return ResponseEntity.ok(new ResponseDTO<>(false, "favorito agregado"));
    }

    /** Quita un lugar de favoritos (idempotente). */
    @DeleteMapping("/{placeId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDTO<String>> remove(Authentication auth, @PathVariable String placeId) {
      favoriteService.removeFavorite(auth.getName(), placeId);
      return ResponseEntity.ok(new ResponseDTO<>(false, "favorito eliminado"));
    }

    /** Lista paginada de mis favoritos (devuelve Place directamente). */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDTO<Map<String,Object>>> listMyFavorites(
      Authentication auth,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
    ) {
      Pageable pageable = PageRequest.of(page, size);
      Page<Place> result = favoriteService.listMyFavorites(auth.getName(), pageable);

      // Adaptar al schema ResponsePlacePage (content + totalElements + totalPages + size + number)
      Map<String,Object> pagePayload = Map.of(
        "content", result.getContent(),  // si necesitas DTO, mapéalo aquí
        "totalElements", result.getTotalElements(),
        "totalPages", result.getTotalPages(),
        "size", result.getSize(),
        "number", result.getNumber()
      );
      return ResponseEntity.ok(new ResponseDTO<>(false, pagePayload));
    }
  @GetMapping("/me/{placeId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<ResponseDTO<Boolean>> isMyFavorite(Authentication auth, @PathVariable String placeId) {
    boolean isFav = favoriteService.isMyFavorite(auth.getName(), placeId);
    return ResponseEntity.ok(new ResponseDTO<>(false, isFav));
  }
    /** Conteo de favoritos por Place (puedes dejarlo público o restringirlo si quieres). */
    @GetMapping("/count/{placeId}")
    public ResponseEntity<ResponseDTO<Long>> count(@PathVariable String placeId) {
      long count = favoriteService.countFavoritesByPlace(placeId);
      return ResponseEntity.ok(new ResponseDTO<>(false, count));
    }
}
