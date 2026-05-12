package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.externalServiceDTO.SendEmailDTO;
import co.edu.uniquindio.application.dto.userDTO.RequestResetPasswordDTO;
import co.edu.uniquindio.application.dto.userDTO.ResetPasswordDTO;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.model.PasswordResetCode;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.repositories.PasswordResetCodeRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.impl.PasswordResetServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

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

    @Test
    @DisplayName("Debe crear codigo de 6 digitos, persistirlo y enviar correo")
    void shouldCreateResetCodeAndSendEmail() throws Exception {
        User user = user("user-1", "Ana", "ana@test.com");
        when(userService.findByEmail("ana@test.com")).thenReturn(user);

        passwordResetService.requestPasswordReset(new RequestResetPasswordDTO("ana@test.com"));

        ArgumentCaptor<PasswordResetCode> codeCaptor = ArgumentCaptor.forClass(PasswordResetCode.class);
        verify(passwordResetCodeRepository).save(codeCaptor.capture());
        PasswordResetCode savedCode = codeCaptor.getValue();

        assertEquals(user, savedCode.getUser());
        assertFalse(savedCode.isUsed());
        assertNotNull(savedCode.getCreatedAt());
        assertNotNull(savedCode.getExpiresAt());
        assertTrue(savedCode.getExpiresAt().isAfter(savedCode.getCreatedAt()));
        assertTrue(savedCode.getCode().matches("\\d{6}"));

        ArgumentCaptor<SendEmailDTO> emailCaptor = ArgumentCaptor.forClass(SendEmailDTO.class);
        verify(emailService).sendMail(emailCaptor.capture());
        assertEquals("ana@test.com", emailCaptor.getValue().recipient());
        assertTrue(emailCaptor.getValue().body().contains(savedCode.getCode()));
    }

    @Test
    @DisplayName("Debe actualizar la contrasena y marcar el codigo como usado")
    void shouldResetPassword() throws Exception {
        User user = user("user-1", "Ana", "ana@test.com");
        PasswordResetCode code = resetCode("123456", user, false, LocalDateTime.now().plusMinutes(10));

        when(userService.findByEmail("ana@test.com")).thenReturn(user);
        when(passwordResetCodeRepository.findByCodeAndUser("123456", user)).thenReturn(Optional.of(code));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        passwordResetService.resetPassword(new ResetPasswordDTO("123456", "ana@test.com", "new-password"));

        assertEquals("encoded-new-password", user.getPassword());
        assertTrue(code.isUsed());
        verify(userRepository).save(user);
        verify(passwordResetCodeRepository).save(code);
    }

    @Test
    @DisplayName("Debe rechazar codigo inexistente")
    void shouldRejectInvalidCode() {
        User user = user("user-1", "Ana", "ana@test.com");
        when(userService.findByEmail("ana@test.com")).thenReturn(user);
        when(passwordResetCodeRepository.findByCodeAndUser("000000", user)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class,
                () -> passwordResetService.resetPassword(new ResetPasswordDTO("000000", "ana@test.com", "new-password")));

        assertTrue(ex.getMessage().contains("Código inválido"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar codigo ya utilizado")
    void shouldRejectUsedCode() {
        User user = user("user-1", "Ana", "ana@test.com");
        PasswordResetCode code = resetCode("123456", user, true, LocalDateTime.now().plusMinutes(10));

        when(userService.findByEmail("ana@test.com")).thenReturn(user);
        when(passwordResetCodeRepository.findByCodeAndUser("123456", user)).thenReturn(Optional.of(code));

        Exception ex = assertThrows(Exception.class,
                () -> passwordResetService.resetPassword(new ResetPasswordDTO("123456", "ana@test.com", "new-password")));

        assertTrue(ex.getMessage().contains("ya fue utilizado"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar contrasena menor a 6 caracteres")
    void shouldRejectShortPassword() {
        User user = user("user-1", "Ana", "ana@test.com");
        PasswordResetCode code = resetCode("123456", user, false, LocalDateTime.now().plusMinutes(10));

        when(userService.findByEmail("ana@test.com")).thenReturn(user);
        when(passwordResetCodeRepository.findByCodeAndUser("123456", user)).thenReturn(Optional.of(code));

        assertThrows(ValueConflictException.class,
                () -> passwordResetService.resetPassword(new ResetPasswordDTO("123456", "ana@test.com", "123")));

        verify(userRepository, never()).save(any());
    }

    private User user(String id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword("old-password");
        return user;
    }

    private PasswordResetCode resetCode(String code, User user, boolean used, LocalDateTime expiresAt) {
        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setCode(code);
        resetCode.setUser(user);
        resetCode.setUsed(used);
        resetCode.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        resetCode.setExpiresAt(expiresAt);
        return resetCode;
    }
}
