package co.edu.uniquindio.application.dto.userDTO;

import co.edu.uniquindio.application.model.enums.Role;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateUserDTO(@NotBlank(message = "Nombre requerido")
                            String name,
                            @NotBlank(message = "Apellido requerido")
                            String surname,
                            @Email(message = "Email inválido")
                            @NotBlank(message = "Email requerido")
                            String email,
                            @NotBlank(message = "Teléfono requerido")
                            String phone,
                            @NotNull(message = "Fecha de nacimiento requerida")
                            LocalDate birthDate,
                            String country,
                            String photoUrl,
                            @NotBlank(message = "Contraseña requerida")
                            @Size(min = 8, message = "Mínimo 8 caracteres")
                            @Pattern(regexp="^(?=.*[A-Z])(?=.*\\d).+$", message="Debe incluir mayúscula y dígito")
                            String password,
                            @NotNull Role role
    ) {
}