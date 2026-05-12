package co.edu.uniquindio.application;

import co.edu.uniquindio.application.validators.ImageValidators;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageValidatorTest {

    private final ImageValidators imageValidator = new ImageValidators();

    @Test
    @DisplayName("Debe aceptar URLs http/https con extensiones de imagen permitidas")
    void shouldAcceptValidImageUrls() {
        assertTrue(imageValidator.isValid("https://cdn.app.com/user123/photo.jpg"));
        assertTrue(imageValidator.isValid("http://localhost:8080/images/test.png"));
        assertTrue(imageValidator.isValid("https://cdn.app.com/image.JPEG"));
        assertTrue(imageValidator.isValid("https://cdn.app.com/image.webp"));
    }

    @Test
    @DisplayName("Debe rechazar URLs vacias, esquemas invalidos y extensiones no permitidas")
    void shouldRejectInvalidImageUrls() {
        assertFalse(imageValidator.isValid(null));
        assertFalse(imageValidator.isValid(""));
        assertFalse(imageValidator.isValid("   "));
        assertFalse(imageValidator.isValid("ftp://server/photo.jpg"));
        assertFalse(imageValidator.isValid("https://cdn.app.com/user123/photo.txt"));
        assertFalse(imageValidator.isValid("photo.jpg"));
    }
}
