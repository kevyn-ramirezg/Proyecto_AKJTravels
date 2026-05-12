package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.authDTO.LoginDTO;
import co.edu.uniquindio.application.dto.authDTO.TokenDTO;
import co.edu.uniquindio.application.dto.hostDTO.HostDTO;
import co.edu.uniquindio.application.dto.userDTO.CreateUserDTO;
import co.edu.uniquindio.application.dto.userDTO.DeleteUserDTO;
import co.edu.uniquindio.application.dto.userDTO.EditPasswordDTO;
import co.edu.uniquindio.application.dto.userDTO.EditUserDTO;
import co.edu.uniquindio.application.dto.userDTO.UserDTO;
import co.edu.uniquindio.application.exceptions.BadRequestException;
import co.edu.uniquindio.application.exceptions.ForbiddenException;
import co.edu.uniquindio.application.exceptions.ResourceNotFoundException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.mappers.UserMapper;
import co.edu.uniquindio.application.model.HostProfile;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.Role;
import co.edu.uniquindio.application.model.enums.State;
import co.edu.uniquindio.application.repositories.HostRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.security.JWTUtils;
import co.edu.uniquindio.application.services.impl.CurrentUserServiceImpl;
import co.edu.uniquindio.application.services.impl.UserServiceImpl;
import co.edu.uniquindio.application.validators.ImageValidators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private CurrentUserServiceImpl currentUserService;
    @Mock private UserMapper userMapper;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JWTUtils jwtUtils;
    @Mock private HostRepository hostRepository;
    @Mock private ImageValidators imageValidators;
    @Mock private ImageService imageService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                currentUserService,
                userMapper,
                userRepository,
                passwordEncoder,
                jwtUtils,
                hostRepository,
                imageValidators,
                imageService
        );
    }

    @Test
    @DisplayName("Debe registrar un huésped cuando el email no existe")
    void createUserWhenEmailIsAvailable() throws Exception {
        CreateUserDTO dto = createUserDTO(Role.USER);
        User mappedUser = user("user-1", Role.USER);
        mappedUser.setPassword("plainPassword1");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(mappedUser);
        when(userRepository.save(mappedUser)).thenReturn(mappedUser);

        userService.create(dto);

        verify(userRepository).save(mappedUser);
        assertNotEquals("plainPassword1", mappedUser.getPassword());
        verify(hostRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe crear perfil de anfitrión al registrar usuario HOST")
    void createHostAlsoCreatesHostProfile() throws Exception {
        CreateUserDTO dto = createUserDTO(Role.HOST);
        User mappedUser = user("host-1", Role.HOST);

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(mappedUser);

        userService.create(dto);

        ArgumentCaptor<HostProfile> captor = ArgumentCaptor.forClass(HostProfile.class);
        verify(hostRepository).save(captor.capture());
        assertEquals("host-1", captor.getValue().getId());
        assertSame(mappedUser, captor.getValue().getUser());
    }

    @Test
    @DisplayName("Debe rechazar registro cuando el email ya existe")
    void createRejectsDuplicatedEmail() {
        CreateUserDTO dto = createUserDTO(Role.USER);
        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(ValueConflictException.class, () -> userService.create(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe iniciar sesión y generar token con claims del usuario")
    void loginGeneratesToken() throws Exception {
        LoginDTO dto = new LoginDTO("host@test.com", "Clave123");
        User host = user("host-1", Role.HOST);
        host.setPassword("encoded-password");

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(host));
        when(passwordEncoder.matches(dto.password(), host.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(eq("host-1"), anyMap())).thenReturn("jwt-token");

        TokenDTO result = userService.login(dto);

        assertEquals("jwt-token", result.token());
        ArgumentCaptor<Map<String, String>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(jwtUtils).generateToken(eq("host-1"), claimsCaptor.capture());
        assertEquals("host@test.com", claimsCaptor.getValue().get("email"));
        assertEquals("Host", claimsCaptor.getValue().get("name"));
        assertEquals("ROLE_HOST", claimsCaptor.getValue().get("role"));
    }

    @Test
    @DisplayName("Debe rechazar login cuando el usuario no existe")
    void loginRejectsUnknownEmail() {
        LoginDTO dto = new LoginDTO("missing@test.com", "Clave123");
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.login(dto));
        verify(jwtUtils, never()).generateToken(anyString(), anyMap());
    }

    @Test
    @DisplayName("Debe rechazar login cuando la contraseña no coincide")
    void loginRejectsInvalidPassword() {
        LoginDTO dto = new LoginDTO("user@test.com", "wrongPass");
        User user = user("user-1", Role.USER);
        user.setPassword("encoded-password");

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.password(), user.getPassword())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.login(dto));
        verify(jwtUtils, never()).generateToken(anyString(), anyMap());
    }

    @Test
    @DisplayName("Debe obtener usuario por id")
    void getUserById() throws Exception {
        User user = user("user-1", Role.USER);
        UserDTO dto = new UserDTO("User", "Demo", "user@test.com", "https://img.com/a.jpg", LocalDate.of(1995, 1, 1), Role.USER, user.getCreatedAt());

        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(dto);

        UserDTO result = userService.get("user-1");

        assertEquals("user@test.com", result.email());
        assertEquals(Role.USER, result.role());
    }

    @Test
    @DisplayName("Debe completar datos de anfitrión del usuario autenticado")
    void addHostData() throws Exception {
        HostDTO dto = new HostDTO("Anfitrión con experiencia", List.of("doc-1", "doc-2"));
        User hostUser = user("host-1", Role.HOST);
        HostProfile profile = new HostProfile();
        profile.setId("host-1");
        profile.setUser(hostUser);

        when(currentUserService.getCurrentUser()).thenReturn("host-1");
        when(userRepository.findById("host-1")).thenReturn(Optional.of(hostUser));
        when(hostRepository.findByUserId("host-1")).thenReturn(Optional.of(profile));

        userService.addDataHost("host-1", dto);

        assertTrue(hostUser.getIsHost());
        assertEquals("Anfitrión con experiencia", hostUser.getDescription());
        assertEquals(List.of("doc-1", "doc-2"), profile.getDocuments());
        verify(userRepository).save(hostUser);
        verify(hostRepository).save(profile);
    }

    @Test
    @DisplayName("Debe impedir modificar datos de otro usuario")
    void addHostDataRejectsDifferentCurrentUser() {
        when(currentUserService.getCurrentUser()).thenReturn("other-user");

        assertThrows(ForbiddenException.class, () -> userService.addDataHost("host-1", new HostDTO("Descripción válida", List.of("doc"))));
    }

    @Test
    @DisplayName("Debe actualizar datos básicos del perfil")
    void updateBasicData() throws Exception {
        User user = user("user-1", Role.USER);
        EditUserDTO dto = new EditUserDTO(" Nuevo ", " Apellido ", " 3001234567 ", "https://img.com/new.png", null);

        when(currentUserService.getCurrentUser()).thenReturn("user-1");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(imageValidators.isValid("https://img.com/new.png")).thenReturn(true);

        userService.updateBasicData("user-1", dto);

        assertEquals("Nuevo", user.getName());
        assertEquals("Apellido", user.getLastName());
        assertEquals("3001234567", user.getPhone());
        assertEquals("https://img.com/new.png", user.getPhotoUrl());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Debe rechazar URL de foto inválida en actualización básica")
    void updateBasicDataRejectsInvalidPhotoUrl() {
        User user = user("user-1", Role.USER);
        EditUserDTO dto = new EditUserDTO("User", null, null, "ftp://img.com/file.txt", null);

        when(currentUserService.getCurrentUser()).thenReturn("user-1");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(imageValidators.isValid(dto.photoUrl())).thenReturn(false);

        assertThrows(ValueConflictException.class, () -> userService.updateBasicData("user-1", dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe cambiar contraseña cuando la contraseña anterior coincide")
    void changePassword() throws Exception {
        User user = user("user-1", Role.USER);
        user.setPassword("old-encoded");
        EditPasswordDTO dto = new EditPasswordDTO("Old1234", "New1234");

        when(currentUserService.getCurrentUser()).thenReturn("user-1");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Old1234", "old-encoded")).thenReturn(true);
        when(passwordEncoder.encode("New1234")).thenReturn("new-encoded");

        userService.changePassword("user-1", dto);

        assertEquals("new-encoded", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Debe rechazar cambio de contraseña si la anterior no coincide")
    void changePasswordRejectsInvalidCurrentPassword() {
        User user = user("user-1", Role.USER);
        user.setPassword("old-encoded");
        EditPasswordDTO dto = new EditPasswordDTO("Wrong123", "New1234");

        when(currentUserService.getCurrentUser()).thenReturn("user-1");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Wrong123", "old-encoded")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userService.changePassword("user-1", dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe desactivar usuario cuando confirma su contraseña")
    void deleteUserDeactivatesAccount() throws Exception {
        User user = user("user-1", Role.USER);
        user.setPassword("encoded-password");

        when(currentUserService.getCurrentUser()).thenReturn("user-1");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Clave123", "encoded-password")).thenReturn(true);

        userService.delete("user-1", new DeleteUserDTO("Clave123"));

        assertEquals(State.INACTIVE, user.getState());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Debe actualizar foto de perfil y eliminar foto anterior")
    void updatePhotoUploadsNewImageAndDeletesPreviousOne() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        User user = user("user-1", Role.USER);
        user.setPhotoPublicId("old-public-id");

        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(1024L);
        when(currentUserService.getCurrentUser()).thenReturn("user-1");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(imageService.upload(file)).thenReturn(Map.of("secure_url", "https://cdn.com/new.png", "public_id", "new-public-id"));

        userService.updatePhoto("user-1", file);

        assertEquals("https://cdn.com/new.png", user.getPhotoUrl());
        assertEquals("new-public-id", user.getPhotoPublicId());
        verify(imageService).delete("old-public-id");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Debe rechazar foto de perfil con formato no permitido")
    void updatePhotoRejectsInvalidContentType() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");

        assertThrows(ValueConflictException.class, () -> userService.updatePhoto("user-1", file));
        verify(imageService, never()).upload(any());
    }

    private CreateUserDTO createUserDTO(Role role) {
        return new CreateUserDTO(
                role == Role.HOST ? "Host" : "User",
                "Demo",
                role == Role.HOST ? "host@test.com" : "user@test.com",
                "3001234567",
                LocalDate.of(1995, 1, 1),
                "Colombia",
                "https://img.com/a.jpg",
                "Clave123",
                role
        );
    }

    private User user(String id, Role role) {
        User user = new User();
        user.setId(id);
        user.setName(role == Role.HOST ? "Host" : "User");
        user.setLastName("Demo");
        user.setEmail(role == Role.HOST ? "host@test.com" : "user@test.com");
        user.setPhone("3001234567");
        user.setBirthDate(LocalDate.of(1995, 1, 1));
        user.setCountry("Colombia");
        user.setRole(role);
        user.setPassword("encoded-password");
        user.setPhotoUrl("https://img.com/a.jpg");
        user.setState(State.ACTIVE);
        user.setCreatedAt(LocalDateTime.now().minusDays(1));
        user.setIsHost(role == Role.HOST);
        return user;
    }
}
