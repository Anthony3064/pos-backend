package com.afb.pos_backend.user.service;

import com.afb.pos_backend.common.dto.RoleUser;
import com.afb.pos_backend.user.dto.UserDTO;
import org.springframework.data.domain.Page;

public interface UserService {
    UserDTO createUser(UserDTO user);

    Page<UserDTO> getAllUsers(int page, int size);

    Page<UserDTO> getAllUsers(int page, int size, RoleUser role);

    Page<UserDTO> getAllUsersOfUser(int page, int size, String uid);

    UserDTO getUser(String uid);

    UserDTO getByUsername(String username);

    UserDTO updateUser(UserDTO user);

    String deleteUser(String uid);

}
