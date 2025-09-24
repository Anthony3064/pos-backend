package com.afb.pos_backend.user.service.implementation;

import com.afb.pos_backend.common.constant.MessageConstant;
import com.afb.pos_backend.common.dto.RoleUser;
import com.afb.pos_backend.config.exception.model.BadRequestException;
import com.afb.pos_backend.config.exception.model.DuplicateResourceException;
import com.afb.pos_backend.config.exception.model.NotFoundException;
import com.afb.pos_backend.user.dto.UserDTO;
import com.afb.pos_backend.user.persistence.entity.User;
import com.afb.pos_backend.user.persistence.repository.UserRepository;
import com.afb.pos_backend.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Log4j2
@AllArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final ModelMapper mapper;

    @Override
    public UserDTO createUser(UserDTO user) {
        boolean existsUser = repository.existsByUsername(user.getUsername());
        if (existsUser) {
            throw new DuplicateResourceException(String.format(MessageConstant.DUPLICATE_RESOURCE_ERROR, user.getUsername()));
        }
        String passwordEncoded = encoder.encode(user.getPassword());
        user.setPassword(passwordEncoded);
        User userEntity = repository.save(mapper.map(user, User.class));
        return mapper.map(userEntity, UserDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersEntities = repository.findAll(pageable);
        return usersEntities.map(userEntity -> mapper.map(userEntity, UserDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(int page, int size, RoleUser role) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersEntities = repository.findByRole(pageable, role);
        return usersEntities.map(userEntity -> mapper.map(userEntity, UserDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsersOfUser(int page, int size, String uid) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersEntities = repository.findByCreatedBy(pageable, uid);
        return usersEntities.map(userEntity -> mapper.map(userEntity, UserDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUser(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "identificador del usuario"));
        }
        User userEntity = repository.findById(uid).orElseThrow(() -> new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "usuario")));
        return mapper.map(userEntity, UserDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "nombre de usuario"));
        }
        User userEntity = repository.findByUsername(username).orElseThrow(() -> new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "usuario")));
        return mapper.map(userEntity, UserDTO.class);
    }

    @Override
    public UserDTO updateUser(UserDTO user) {
        if (user.getId() == null || user.getId().isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "identificador del usuario"));
        }
        boolean userExists = repository.existsById(user.getId());
        if (!userExists) {
            throw new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "usuario"));
        }
        User userEntity = repository.save(mapper.map(user, User.class));
        return mapper.map(userEntity, UserDTO.class);
    }

    @Override
    public String deleteUser(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException(String.format(MessageConstant.BAD_REQUEST_ERROR, "identificador del usuario"));
        }
        boolean userExists = repository.existsById(uid);
        if (!userExists) {
            throw new NotFoundException(String.format(MessageConstant.NOT_FOUND_ERROR, "usuario"));
        }
        int rowsAffected = repository.changeActive(uid, false);
        log.info("Se ha actualizado {} registro.", rowsAffected);
        return String.format("El usuario con identificador: %s ha sido borrado", uid);
    }
}
