package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.exceptions.BadRequestException;
import co.edu.uniquindio.application.services.ImageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
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

    public ImageServiceImpl(){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dje3qr8tq");
        config.put("api_key", "693381465632364");
        config.put("api_secret", "5cICFV1EvZ-E8FPCAyNSh5WGUt0");
        cloudinary = new Cloudinary(config);
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
            if (file != null && file.exists()) {
                file.delete();
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

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BadRequestException("Formato de imagen no permitido. Usa JPG, PNG o WEBP.");
        }
    }

    @Override
    public Map delete(String id) throws Exception {
        return cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
    }

    private File convert(MultipartFile image) throws IOException {
        File file = File.createTempFile(image.getOriginalFilename(), null);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(image.getBytes());
        fos.close();
        return file;
    }
}