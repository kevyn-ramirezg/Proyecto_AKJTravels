package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.*;
import co.edu.uniquindio.application.dto.bookingDTO.BookingDTO;
import co.edu.uniquindio.application.dto.bookingDTO.SearchBookingDTO;
import co.edu.uniquindio.application.dto.hostDTO.HostDTO;
import co.edu.uniquindio.application.dto.placeDTO.PlaceDTO;
import co.edu.uniquindio.application.dto.userDTO.*;
import co.edu.uniquindio.application.services.PlaceService;
import co.edu.uniquindio.application.services.BookingService;
import co.edu.uniquindio.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final PlaceService placeService;
    private final UserService userService;
    private final BookingService bookingService;
//

    @PutMapping(("/{id}"))
    public ResponseEntity<ResponseDTO<String>> edit(@PathVariable String id, @Valid @RequestBody EditUserDTO editUserDTO) throws Exception {
        userService.updateBasicData(id, editUserDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, "actualizacion exitosa :)"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> delete(@PathVariable String id, @Valid @RequestBody DeleteUserDTO deleteUserDTO) throws Exception {

        userService.delete(id, deleteUserDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, "eliminacion exitosa :)"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> get(@PathVariable String id) throws Exception{
        UserDTO userDTO = userService.get(id);
        return ResponseEntity.ok(new ResponseDTO<>(false, userDTO));
    }

    @PutMapping("/{id}/host")
    public ResponseEntity<ResponseDTO<String>> add_data_host(@PathVariable String id, @Valid @RequestBody HostDTO hostDTO ) throws Exception {
        userService.addDataHost(id, hostDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, "datos añadidos con exito "));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> update_password(@PathVariable String id, @Valid @RequestBody EditPasswordDTO editPasswordDTO) throws Exception {
        userService.changePassword(id, editPasswordDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, "contraseña actualizada :)"));
    }

    @GetMapping("/{id}/bookings/{page}")
    public ResponseEntity<ResponseDTO<List<BookingDTO>>> booking_list(@PathVariable String id, @PathVariable int page, @Valid @RequestBody SearchBookingDTO searchBookingDTO) throws Exception {
        List<BookingDTO> list = bookingService.listBookingsUser(id, page, searchBookingDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, list));
    }

    @GetMapping("/{id}/places/host/{page}")
    public ResponseEntity<ResponseDTO<List<PlaceDTO>>> listPlaceHost(@PathVariable String id, @PathVariable int page) throws Exception {
        List<PlaceDTO> list = placeService.listAllPlacesHost(id, page);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(false, list));
    }
    @PostMapping("/{id}/photo")
    public ResponseEntity<ResponseDTO<String>> updatePhoto(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) throws Exception {
        userService.updatePhoto(id, file);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Foto de perfil actualizada"));
    }


}