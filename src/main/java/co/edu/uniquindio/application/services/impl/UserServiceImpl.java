package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.authDTO.LoginDTO;
import co.edu.uniquindio.application.dto.authDTO.TokenDTO;
import co.edu.uniquindio.application.dto.hostDTO.HostDTO;
import co.edu.uniquindio.application.dto.userDTO.*;
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
import co.edu.uniquindio.application.services.CurrentUserService;
import co.edu.uniquindio.application.services.ImageService;
import co.edu.uniquindio.application.services.UserService;
import co.edu.uniquindio.application.validators.ImageValidators;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final CurrentUserServiceImpl currentUserService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final HostRepository hostRepository;
    private final ImageValidators imageValidators;
    private final ImageService imageService;

    @Override
    @Transactional
    public void create(CreateUserDTO createUserDTO) throws Exception {

        if (userRepository.existsByEmail(createUserDTO.email())) {
            throw new ValueConflictException("El email ya está registrado");
        }

        User user = userMapper.toEntity(createUserDTO);
        user.setPassword(encode(createUserDTO.password()));
        userRepository.save(user);

        if (createUserDTO.role() == Role.HOST) {
            HostProfile host = new HostProfile();
            host.setUser(user);
            host.setId(user.getId());
            hostRepository.save(host);
        }
    }

    @Override
    public UserDTO get(String id) throws Exception {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        return userMapper.toUserDTO(optionalUser.get());
    }


    @Override
    @Transactional
    public void addDataHost(String id, HostDTO hostDTO) throws Exception {
        validateCurrentUser(id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        HostProfile host = hostRepository.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de anfitrión no encontrado"));

        if (hostDTO.description() == null || hostDTO.description().isBlank()) {
            throw new ValueConflictException("Descripcion requerida para ser anfitrion");
        }
        user.setDescription(hostDTO.description().trim());
        host.setDocuments(hostDTO.legal_documents());
        user.setIsHost(true);
        userRepository.save(user);
        hostRepository.save(host);
    }

    @Override
    public void delete(String id, DeleteUserDTO deleteUserDTO) throws Exception {
        validateCurrentUser(id);
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        if (!passwordEncoder.matches(deleteUserDTO.password(), optionalUser.get().getPassword())) {
            throw new ValueConflictException("La contraseña es incorrecta");
        }

        User user = optionalUser.get();
        user.setState(State.INACTIVE);
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Override
    public TokenDTO login(LoginDTO loginDTO) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(loginDTO.email());

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("El usuario no existe");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new ResourceNotFoundException("El usuario no existe");
        }

        String token = jwtUtils.generateToken(user.getId(), createClaims(user));
        return new TokenDTO(token);
    }

    @Override
    public void changePassword(String id, EditPasswordDTO editPasswordDTO) throws Exception {

        validateCurrentUser(id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(editPasswordDTO.old_password(), user.getPassword())) {
            throw new BadRequestException("la contraseña no coincide");
        }

        user.setPassword(passwordEncoder.encode(editPasswordDTO.new_password()));
        userRepository.save(user);
    }

    // ========= MÉTODOS NUEVOS que exige la interfaz =========

    /** Perfil detallado para /api/auth/me (según tu UserDetailDTO: id, name, photoUrl, createdAt) */
    @Override
    public UserDetailDTO getUserDetailById(String id) throws Exception {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return new UserDetailDTO(u.getId(), u.getName(), u.getLastName(), u.getPhotoUrl(), u.getCreatedAt());
    }

    private void validateCurrentUser(String id) {

        String currentUserId = currentUserService.getCurrentUser();

        if (!Objects.equals(currentUserId, id)) {
            throw new ForbiddenException("No puedes modificar información de otro usuario");
        }
    }

    @Override
    public void updateBasicData(String id, EditUserDTO dto) throws Exception {
        validateCurrentUser(id);

        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (dto.name() != null)     u.setName(dto.name().trim());
        if (dto.lastName() != null) u.setLastName(dto.lastName().trim());
        if (dto.phone() != null)    u.setPhone(dto.phone().trim());
        if (dto.photoUrl() != null) {
            if (!imageValidators.isValid(dto.photoUrl())) throw new ValueConflictException("El formato de imagen no es valido");
            u.setPhotoUrl(dto.photoUrl().trim());
        }
        // if (dto.birthDate() != null) u.setBirthDate(dto.birthDate());
        userRepository.save(u);
    }

    // ========================================================

    private Map<String, String> createClaims(User user) {
        return Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "role", "ROLE_" + user.getRole().name()
        );
    }

    private String encode(String password) {
        var passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
    @Override
    public void updatePhoto(String id, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) throw new BadRequestException("Debes adjuntar una imagen");

        String ct = file.getContentType() != null ? file.getContentType() : "";
        if (!ct.startsWith("image/")) throw new ValueConflictException("El archivo debe ser una imagen");
        if (file.getSize() > 5L * 1024 * 1024) throw new ValueConflictException("La imagen no debe superar 5 MB");
        validateCurrentUser(id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Sube
        Map<?, ?> uploadRes = imageService.upload(file);
        String url = (String) (uploadRes.get("secure_url") != null ? uploadRes.get("secure_url") : uploadRes.get("url"));
        String publicId = (String) uploadRes.get("public_id");
        if (url == null || url.isBlank()) throw new ValueConflictException("La carga no devolvió una URL válida");

        // Borra la anterior si existe
        if (user.getPhotoPublicId() != null && !user.getPhotoPublicId().isBlank()) {
            try { imageService.delete(user.getPhotoPublicId()); } catch (Exception ignored) {}
        }

        user.setPhotoUrl(url.trim());
        user.setPhotoPublicId(publicId);
        userRepository.save(user);
    }



}
