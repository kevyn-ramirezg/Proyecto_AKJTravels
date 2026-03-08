package co.edu.uniquindio.application.dto.placeDTO;

import co.edu.uniquindio.application.model.enums.PlaceType;
import co.edu.uniquindio.application.model.enums.Services;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import java.util.List;

public record CreatePlaceDTO(@NotBlank(message = "El título no puede estar vacío")
                             @Length(min = 5, max = 25, message = "El título debe tener entre 5 y 25 caracteres")
                             String title,
                             @NotBlank(message = "La descripción no puede estar vacía")
                             @NotBlank @Size(min = 20, max = 500, message = "La descripción debe tener entre 20 y 500 caracteres")
                             String description,
                             @NotNull @Positive double price,
                             @JsonProperty("pics_url")
                             @Size(max = 10, message = "Máximo 10 imágenes")
                             List<String> picsUrl,
                             @NotNull(message = "el tipo de alojamiento es obligatorio")
                             PlaceType placeType,
                             @Min(value = 1, message = "La capacidad debe ser mínimo 1")
                             @Max(value = 60, message = "La capacidad máximo 60")
                             int capacity,
                             @NotBlank @Length(max=30) String country,
                             @NotBlank @Length(max=30) String department,
                             @NotBlank @Length(max=30) String city,
                             @Length(max = 20) String neighborhood //puede ser opcional
                            , String street,
                             @NotBlank @Pattern(regexp = "^[0-9A-Za-z]{4,10}$", message = "El código postal no es válido")
                             String postalCode,
                             @NotEmpty(message = "debe tener al menos 1 amenidad") List<Services> amenities,
                             @NotNull float latitude,  @NotNull float longitude

) {
}
