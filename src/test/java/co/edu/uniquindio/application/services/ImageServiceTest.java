package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.services.impl.ImageServiceImpl;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ImageServiceTest {

    /*
    @Mock
    private Cloudinary cloudinaryMock;

    @Mock
    private Uploader uploaderMock;

    private ImageServiceImpl imageService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageServiceImpl();
        // Sustituimos el objeto Cloudinary real por el mock
        var field = ImageServiceImpl.class.getDeclaredField("cloudinary");
        field.setAccessible(true);
        field.set(imageService, cloudinaryMock);

        when(cloudinaryMock.uploader()).thenReturn(uploaderMock);
    }
    @Test
    public void upload_ShouldReturnCloudinaryResponse() throws Exception {
        MultipartFile multipartFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "fake-image-content".getBytes()
        );

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("url", "https://cloudinary.com/test.jpg");
        expectedResponse.put("public_id", "test123");

        when(uploaderMock.upload(any(File.class), anyMap())).thenReturn(expectedResponse);

        Map result = imageService.upload(multipartFile);

        assertEquals(expectedResponse, result);
        verify(uploaderMock, times(1))
                .upload(any(File.class), eq(ObjectUtils.asMap("folder", "AdvanzedProyect")));
    }
    @Test
    public void delete_ShouldReturnCloudinaryResponse() throws Exception {
        String imageId = "test123";
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("result", "ok");

        when(uploaderMock.destroy(eq(imageId), anyMap())).thenReturn(expectedResponse);

        Map result = imageService.delete(imageId);

        assertEquals(expectedResponse, result);
        verify(uploaderMock, times(1))
                .destroy(eq(imageId), eq(ObjectUtils.emptyMap()));
    }
    @Test
    public void convert_ShouldCreateTempFile() throws Exception {
        MultipartFile multipartFile = new MockMultipartFile(
                "file", "sample.txt", "text/plain", "Hello world".getBytes()
        );

        var method = ImageServiceImpl.class.getDeclaredMethod("convert", MultipartFile.class);
        method.setAccessible(true);

        File file = (File) method.invoke(imageService, multipartFile);

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(file.getName().contains("sample.txt"));

        file.delete(); // limpieza
    }
    @Test
    public void upload_WhenCloudinaryThrowsException_ShouldPropagate() throws Exception {
        MultipartFile multipartFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "fake-image-content".getBytes()
        );

        when(uploaderMock.upload(any(File.class), anyMap()))
                .thenThrow(new IOException("Error de conexión con Cloudinary"));

        Exception ex = assertThrows(Exception.class, () -> {
            imageService.upload(multipartFile);
        });

        assertTrue(ex.getMessage().contains("Error de conexión"));
    }
    @Test
    public void delete_WhenCloudinaryThrowsException_ShouldPropagate() throws Exception {
        String imageId = "fail123";

        when(uploaderMock.destroy(eq(imageId), anyMap()))
                .thenThrow(new IOException("No se pudo eliminar la imagen"));

        Exception ex = assertThrows(Exception.class, () -> {
            imageService.delete(imageId);
        });

        assertTrue(ex.getMessage().contains("No se pudo eliminar"));
    }*/

}
