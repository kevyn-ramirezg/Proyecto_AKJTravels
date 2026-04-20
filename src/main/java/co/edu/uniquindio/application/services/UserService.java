package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.authDTO.LoginDTO;
import co.edu.uniquindio.application.dto.authDTO.TokenDTO;
import co.edu.uniquindio.application.dto.hostDTO.HostDTO;
import co.edu.uniquindio.application.dto.userDTO.*;
import co.edu.uniquindio.application.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    void create(CreateUserDTO userDTO) throws Exception;

    // Ya existente: listado/versión resumida
    UserDTO get(String id) throws Exception;


    void addDataHost(String id, HostDTO hostDTO) throws Exception;

    void delete(String id, DeleteUserDTO deleteUserDTO) throws Exception;

    User findByEmail(String email);

    TokenDTO login(LoginDTO loginDTO) throws Exception;

    void changePassword(String id, EditPasswordDTO editPasswordDTO) throws Exception;

    // ⬇️ NUEVO: perfil completo para /api/auth/me
    UserDetailDTO getUserDetailById(String id) throws Exception;

    // ⬇️ (Opcional pero útil) Actualización básica sin tocar email
    void updateBasicData(String id, EditUserDTO editUserDTO) throws Exception;

    void updatePhoto(String id, MultipartFile file) throws Exception;
}
