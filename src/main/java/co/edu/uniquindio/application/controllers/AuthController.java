package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.authDTO.LoginDTO;
import co.edu.uniquindio.application.dto.authDTO.TokenDTO;
import co.edu.uniquindio.application.dto.userDTO.CreateUserDTO;
import co.edu.uniquindio.application.dto.userDTO.UserDTO;
import co.edu.uniquindio.application.dto.userDTO.RequestResetPasswordDTO;
import co.edu.uniquindio.application.dto.userDTO.ResetPasswordDTO;
import co.edu.uniquindio.application.services.PasswordResetService;
import co.edu.uniquindio.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/auth"})
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    /**
     * Devuelve el perfil para precargar el front.
     * Une: id (Authentication), + datos de UserDTO (name, email, photoUrl, birthDate, role, createdAt).
     * Nota: phone NO existe en UserDTO (según tu código); quedará null/absent.
     */
    @GetMapping("/me")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> me(Authentication auth) throws Exception {
        final String userId = auth.getName();
        UserDTO u = userService.get(userId);
        var userEntity = userService.findByEmail(u.email());
        String phone = userEntity.getPhone();

        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                .orElse("GUEST")
                .toUpperCase();

        Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("id",        userId);
        payload.put("name",      u.name()     != null ? u.name()     : "");
        payload.put("lastName",  u.lastName() != null ? u.lastName() : "");
        payload.put("email",     u.email()    != null ? u.email()    : "");
        payload.put("photoUrl",  u.photoUrl() != null ? u.photoUrl() : "");
        payload.put("birthDate", u.birthDate());
        payload.put("role",      role);
        payload.put("createdAt", u.createdAt());
        payload.put("phone",     phone        != null ? phone        : "");

        return ResponseEntity.ok(new ResponseDTO<>(false, payload));
    }



    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@RequestBody @Valid CreateUserDTO createUserDTO) throws Exception {
        userService.create(createUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "registro exitoso :)"));
    }

    @PostMapping({"/login"})
    public ResponseEntity<ResponseDTO<TokenDTO>> login(@RequestBody @Valid LoginDTO loginDTO) throws Exception {
        TokenDTO token = userService.login(loginDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseDTO<>(false, token));
    }

    @PostMapping({"/forgot-password"})
    public ResponseEntity<ResponseDTO<String>> requestReset(@RequestBody @Valid RequestResetPasswordDTO dto) throws Exception {
        passwordResetService.requestPasswordReset(dto);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Se ha enviado un código de recuperación a tu email"));
    }

    @PatchMapping({"/reset-password"})
    public ResponseEntity<ResponseDTO<String>> resetPassword(@RequestBody @Valid ResetPasswordDTO dto) throws Exception {
        passwordResetService.resetPassword(dto);
        return ResponseEntity.ok(new ResponseDTO<>(false, "Contraseña cambiada exitosamente"));
    }
}
