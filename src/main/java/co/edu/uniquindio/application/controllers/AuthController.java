package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.dto.authDTO.LoginDTO;
import co.edu.uniquindio.application.dto.authDTO.TokenDTO;
import co.edu.uniquindio.application.dto.userDTO.CreateUserDTO;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @GetMapping("/me")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> me(Authentication auth) {
        // En tu filtro JWT, el "username" es el ID del usuario
        final String userId = auth.getName();

        // Toma el primer rol (o “GUEST” si no hay)
        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                .orElse("GUEST")
                .toUpperCase();

        Map<String, Object> payload = Map.of(
                "userId", userId,
                "role", role
        );
        return ResponseEntity.ok(new ResponseDTO<>(false, payload));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> create(@Valid @RequestBody CreateUserDTO createUserDTO) throws Exception {
        userService.create(createUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(false, "registro exitoso :)"));
    }

  @PostMapping("/login")
  public ResponseEntity<ResponseDTO<TokenDTO>> login(@Valid @RequestBody LoginDTO loginDTO) throws Exception{
    TokenDTO token = userService.login(loginDTO);
    return ResponseEntity.ok(new ResponseDTO<>(false, token)); // 200
  }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestReset(@Valid @RequestBody RequestResetPasswordDTO dto) throws Exception{
        passwordResetService.requestPasswordReset(dto);
        return ResponseEntity.ok("Se ha enviado un código de recuperación a tu email");
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) throws Exception{
        passwordResetService.resetPassword(dto);
        return ResponseEntity.ok("Contraseña cambiada exitosamente");
    }
}
