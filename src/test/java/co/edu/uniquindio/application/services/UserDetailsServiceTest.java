package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.Role;
import co.edu.uniquindio.application.repositories.UserRepository;
//import co.edu.uniquindio.application.security.UserDetailsServiceImpl;
import co.edu.uniquindio.application.services.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

   /* @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("123");
        user.setPassword("encodedPass");
        user.setRole(Role.USER);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        User user = new User();
        user.setId("1");
        user.setPassword("password123");
        user.setRole(Role.USER); // o el enum que uses

        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("1");

        // Assert
        assertEquals("1", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
    }
    @Test
    void loadUserByUsername_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("999"));

        verify(userRepository, times(1)).findById("999");
    }*/
}

