package co.edu.uniquindio.application;

import co.edu.uniquindio.application.validators.ImageValidators;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ImageValidatorTest {

    /*
    @Autowired
    private ImageValidators imageValidator;

    @Test
    void testValidImages() {
        assertTrue(imageValidator.isValid("https://cdn.app.com/user123/photo.jpg"));
        assertTrue(imageValidator.isValid("http://localhost:8080/images/test.png"));
    }

    @Test
    void testInvalidImages() {
        assertFalse(imageValidator.isValid("ftp://server/photo.jpg"));
        assertFalse(imageValidator.isValid("https://cdn.app.com/user123/photo.txt"));
        assertFalse(imageValidator.isValid("photo.jpg"));
    }*/
}