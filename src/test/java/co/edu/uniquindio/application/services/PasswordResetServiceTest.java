package co.edu.uniquindio.application.services;
import co.edu.uniquindio.application.dto.userDTO.RequestResetPasswordDTO;
import co.edu.uniquindio.application.dto.userDTO.ResetPasswordDTO;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.model.PasswordResetCode;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.repositories.PasswordResetCodeRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.impl.PasswordResetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

    /*
    @Mock
    private PasswordResetCodeRepository passwordResetCodeRepository;
    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId("u1");
        user.setEmail("test@example.com");
        user.setPassword("oldPassword");
    }

    @Test
    void requestPasswordReset_ShouldSaveCodeAndSendEmail() throws Exception {
        RequestResetPasswordDTO dto = new RequestResetPasswordDTO("test@example.com");

        when(userService.findByEmail("test@example.com")).thenReturn(user);

        passwordResetService.requestPasswordReset(dto);

        verify(passwordResetCodeRepository).save(any(PasswordResetCode.class));
        verify(emailService).sendMail(argThat(email ->
                email.subject().equals("Cambio de contraseña")
                        && email.body().contains("Utiliza este codigo:")
                        && email.recipient().equals("test@example.com")
        ));
    }

    @Test
    void resetPassword_WhenCodeNotFound_ShouldThrowException() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO("123456", "test@example.com", "newPassword123");

        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(passwordResetCodeRepository.findByCodeAndUser("123456", user))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () ->
                passwordResetService.resetPassword(dto));


        assertTrue(ex.getMessage().contains("No se encuentra el code"));
    }

    @Test
    void resetPassword_WhenCodeUsed_ShouldThrowException() throws Exception {
        PasswordResetCode code = new PasswordResetCode();
        code.setUsed(true);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        ResetPasswordDTO dto = new ResetPasswordDTO("999999", "test@example.com", "newPassword123");

        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(passwordResetCodeRepository.findByCodeAndUser("999999", user))
                .thenReturn(Optional.of(code));

        Exception ex = assertThrows(Exception.class, () ->
                passwordResetService.resetPassword(dto));

        assertEquals("El code ya esta usado", ex.getMessage().trim());
    }

    @Test
    void resetPassword_WhenCodeExpired_ShouldThrowException() throws Exception {
        PasswordResetCode code = new PasswordResetCode();
        code.setUsed(false);
        code.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        ResetPasswordDTO dto = new ResetPasswordDTO("888888", "test@example.com", "newPassword");

        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(passwordResetCodeRepository.findByCodeAndUser("888888", user))
                .thenReturn(Optional.of(code));

        Exception ex = assertThrows(Exception.class, () ->
                passwordResetService.resetPassword(dto));

        assertTrue(ex.getMessage().contains("ha expirado"));
    }

    @Test
    void resetPassword_WhenPasswordTooShort_ShouldThrowValueConflict() throws Exception {
        PasswordResetCode code = new PasswordResetCode();
        code.setUsed(false);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        ResetPasswordDTO dto = new ResetPasswordDTO("111111", "test@example.com", "abc");

        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(passwordResetCodeRepository.findByCodeAndUser("111111", user))
                .thenReturn(Optional.of(code));

        assertThrows(ValueConflictException.class, () ->
                passwordResetService.resetPassword(dto));
    }

    @Test
    void resetPassword_WhenValid_ShouldUpdatePasswordAndMarkCodeUsed() throws Exception {
        PasswordResetCode code = new PasswordResetCode();
        code.setUsed(false);
        code.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        ResetPasswordDTO dto = new ResetPasswordDTO("111111", "test@example.com", "newPassword");

        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(passwordResetCodeRepository.findByCodeAndUser("111111", user))
                .thenReturn(Optional.of(code));
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedPass");

        passwordResetService.resetPassword(dto);

        assertEquals("hashedPass", user.getPassword());
        assertTrue(code.isUsed());
        verify(userRepository).save(user);
        verify(passwordResetCodeRepository).save(code);
    }*/

}
