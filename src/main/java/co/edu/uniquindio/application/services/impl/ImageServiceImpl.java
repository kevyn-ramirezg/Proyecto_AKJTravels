package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.exceptions.BadRequestException;
import co.edu.uniquindio.application.services.ImageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {

    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final Cloudinary cloudinary;

    public ImageServiceImpl(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        if (cloudName == null || cloudName.isBlank()
                || apiKey == null || apiKey.isBlank()
                || apiSecret == null || apiSecret.isBlank()) {
            throw new IllegalStateException("Cloudinary no esta configurado correctamente");
        }

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @Override
    public Map upload(MultipartFile image) throws Exception {
        validateImageFile(image);

        File file = convert(image);
        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", "AKJTravels",
                    "resource_type", "image",
                    "use_filename", true,
                    "unique_filename", true,
                    "overwrite", false
            );
            return cloudinary.uploader().upload(file, options);
        } finally {
            if (file != null && file.exists() && !file.delete()) {
                file.deleteOnExit();
            }
        }
    }

    private void validateImageFile(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Debes adjuntar una imagen");
        }

        if (image.getSize() > MAX_IMAGE_SIZE) {
            throw new BadRequestException("La imagen no debe superar 5 MB");
        }

        String contentType = image.getContentType();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Formato de imagen no permitido. Usa JPG, PNG o WEBP.");
        }
    }

    @Override
    public Map delete(String id) throws Exception {
        return cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
    }

    private File convert(MultipartFile image) throws IOException {
        String originalName = image.getOriginalFilename();
        String prefix = originalName == null || originalName.isBlank() ? "akj-image" : originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (prefix.length() < 3) {
            prefix = "akj-image";
        }

        File file = File.createTempFile(prefix, null);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(image.getBytes());
        }
        return file;
    }
}
