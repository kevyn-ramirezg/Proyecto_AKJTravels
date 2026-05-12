package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.exceptions.BadRequestException;
import co.edu.uniquindio.application.services.impl.ImageServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageServiceTest {

    private final ImageServiceImpl imageService = new ImageServiceImpl("cloud", "key", "secret");

    @Test
    @DisplayName("Debe rechazar subida cuando no se adjunta archivo")
    void shouldRejectNullImage() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> imageService.upload(null));
        assertTrue(ex.getMessage().contains("Debes adjuntar"));
    }

    @Test
    @DisplayName("Debe rechazar imagen vacia")
    void shouldRejectEmptyImage() {
        MockMultipartFile empty = new MockMultipartFile("image", "empty.png", "image/png", new byte[0]);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> imageService.upload(empty));

        assertTrue(ex.getMessage().contains("Debes adjuntar"));
    }

    @Test
    @DisplayName("Debe rechazar archivos que no sean JPG, PNG o WEBP")
    void shouldRejectInvalidContentType() {
        MockMultipartFile pdf = new MockMultipartFile("image", "document.pdf", "application/pdf", "data".getBytes());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> imageService.upload(pdf));

        assertTrue(ex.getMessage().contains("Formato de imagen no permitido"));
    }

    @Test
    @DisplayName("Debe rechazar imagenes mayores a 5 MB")
    void shouldRejectImagesBiggerThanFiveMb() {
        byte[] content = new byte[(5 * 1024 * 1024) + 1];
        MockMultipartFile bigImage = new MockMultipartFile("image", "large.jpg", "image/jpeg", content);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> imageService.upload(bigImage));

        assertTrue(ex.getMessage().contains("5 MB"));
    }
}
