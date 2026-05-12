package co.edu.uniquindio.application.validators;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ImageValidators {

    private static final List<String> VALID_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp");

    public boolean isValid(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return false;
        }

        String lower = imageUrl.toLowerCase();

        // Validar esquema (http o https)
        boolean hasValidScheme = lower.startsWith("http://") || lower.startsWith("https://");

        // Validar extensión permitida
        boolean hasValidExtension = VALID_EXTENSIONS.stream().anyMatch(lower::endsWith);

        return hasValidScheme && hasValidExtension;
    }
//hh
}