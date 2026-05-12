package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.Role;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Debe cargar UserDetails usando el id y mapear el rol con prefijo ROLE_")
    void shouldLoadUserDetailsById() {
        User user = new User();
        user.setId("user-1");
        user.setPassword("encoded-password");
        user.setRole(Role.HOST);

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));

        var details = userDetailsService.loadUserByUsername("user-1");

        assertEquals("user-1", details.getUsername());
        assertEquals("encoded-password", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_HOST")));
        verify(userRepository).findById("user-1");
    }

    @Test
    @DisplayName("Debe lanzar UsernameNotFoundException cuando el usuario no existe")
    void shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById("missing-user")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing-user"));

        verify(userRepository).findById("missing-user");
    }
}
