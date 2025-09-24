package com.afb.pos_backend.user.service;

import com.afb.pos_backend.common.dto.RoleUser;
import com.afb.pos_backend.config.exception.model.BadRequestException;
import com.afb.pos_backend.config.exception.model.DuplicateResourceException;
import com.afb.pos_backend.config.exception.model.NotFoundException;
import com.afb.pos_backend.user.dto.UserDTO;
import com.afb.pos_backend.user.persistence.entity.User;
import com.afb.pos_backend.user.persistence.repository.UserRepository;
import com.afb.pos_backend.user.service.implementation.UserServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private ModelMapper mapper;


    @InjectMocks
    private UserServiceImplementation userServiceImpl;
    private UserService userService;

    // For create user and find one user
    private UserDTO userDTO;
    private User userEntity;
    private User savedUserEntity;

    // For lists test
    private List<User> userEntities;
    private List<UserDTO> userDTOs;
    private Page<User> userPage;

    @BeforeEach
    void setup() {
        this.userService = userServiceImpl;

        // For create and single searching
        userDTO = new UserDTO();
        userDTO.setUsername("test.user");
        userDTO.setPassword("plainPassword");
        userDTO.setCompleteName("Test User");

        userEntity = new User();
        userEntity.setUsername("test.user");
        userEntity.setPassword("plainPassword");
        userEntity.setCompleteName("Test User");
        userEntity.setRole(RoleUser.USER);

        savedUserEntity = new User();
        savedUserEntity.setId("TEST-USER-001");
        savedUserEntity.setUsername("test.user");
        savedUserEntity.setPassword("encodedPassword");
        savedUserEntity.setCompleteName("Test User");
        savedUserEntity.setRole(RoleUser.USER);

        // For list of users
        User userEntityTest = new User();
        userEntityTest.setId("TEST-USER-001");
        userEntityTest.setUsername("test.user1");
        userEntityTest.setPassword("encodedPassword");
        userEntityTest.setCompleteName("Test User 1");
        userEntityTest.setRole(RoleUser.ADMIN);

        User userEntityTest2 = new User();
        userEntityTest2.setId("TEST-USER-002");
        userEntityTest2.setUsername("test.user2");
        userEntityTest2.setPassword("encodedPassword");
        userEntityTest2.setCompleteName("Test User 2");
        userEntityTest2.setRole(RoleUser.USER);

        User userEntityTest3 = new User();
        userEntityTest3.setId("TEST-USER-003");
        userEntityTest3.setUsername("test.user3");
        userEntityTest3.setPassword("encodedPassword");
        userEntityTest3.setCompleteName("Test User 3");
        userEntityTest3.setRole(RoleUser.USER);

        userEntities = Arrays.asList(userEntityTest, userEntityTest2, userEntityTest3);

        UserDTO userTest = new UserDTO();
        userTest.setId("TEST-USER-001");
        userTest.setUsername("test.user1");
        userTest.setPassword("encodedPassword");
        userTest.setCompleteName("Test User 1");
        userTest.setRole(RoleUser.ADMIN);

        UserDTO userTest2 = new UserDTO();
        userTest2.setId("TEST-USER-002");
        userTest2.setUsername("test.user2");
        userTest2.setPassword("encodedPassword");
        userTest2.setCompleteName("Test User 2");
        userTest2.setRole(RoleUser.USER);

        UserDTO userTest3 = new UserDTO();
        userTest3.setId("TEST-USER-003");
        userTest3.setUsername("test.user3");
        userTest3.setPassword("encodedPassword");
        userTest3.setCompleteName("Test User 3");
        userTest3.setRole(RoleUser.USER);

        userDTOs = Arrays.asList(userTest, userTest2, userTest3);

        userPage = new PageImpl<>(userEntities, PageRequest.of(0, 10), 25L);
    }

    @Test
    @DisplayName("Se debería crear el usuario correctamente si el campo username no existe.")
    void testCreateUserSuccess() {
        String encodedPassword = "encodedPassword";

        UserDTO expectedResult = new UserDTO();
        expectedResult.setId("TEST-USER-001");
        expectedResult.setUsername("test.user");
        expectedResult.setPassword(encodedPassword);
        expectedResult.setRole(RoleUser.USER);
        expectedResult.setCompleteName("Test User");

        when(userRepository.existsByUsername("test.user")).thenReturn(false);
        when(encoder.encode("plainPassword")).thenReturn(encodedPassword);
        when(mapper.map(userDTO, User.class)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(savedUserEntity);
        when(mapper.map(savedUserEntity, UserDTO.class)).thenReturn(expectedResult);

        UserDTO result = userService.createUser(userDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test.user");
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.getCompleteName()).isEqualTo("Test User");
        assertThat(result.getRole()).isEqualTo(RoleUser.USER);

        verify(userRepository).existsByUsername("test.user");
        verify(encoder).encode("plainPassword");
        verify(mapper).map(userDTO, User.class);
        verify(userRepository).save(userEntity);
        verify(mapper).map(savedUserEntity, UserDTO.class);

        assertThat(userDTO.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("Se debería lanzar un DuplicateResourceException si el username ya existe.")
    void testCreateUserWithErrorDuplicateResourceException() {
        when(userRepository.existsByUsername("test.user")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> userService.createUser(userDTO));

        verify(userRepository).existsByUsername("test.user");
        verify(encoder, never()).encode("plainPassword");
        verify(mapper, never()).map(userDTO, User.class);
        verify(userRepository, never()).save(userEntity);
        verify(mapper, never()).map(savedUserEntity, UserDTO.class);
    }

    @Test
    @DisplayName("Debería gestionar correctamente el encoding de password.")
    void testCreateUserEncodePasswordCorrectly() {

        String originalPassword = "mySecretPassword";
        String encodedPassword = "encoded_mySecretPassword";

        userDTO.setPassword(originalPassword);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(encoder.encode(originalPassword)).thenReturn(encodedPassword);
        when(mapper.map(userDTO, User.class)).thenReturn(userEntity);
        when(userRepository.save(any(User.class))).thenReturn(savedUserEntity);
        when(mapper.map(any(User.class), eq(UserDTO.class))).thenReturn(new UserDTO());

        userService.createUser(userDTO);


        verify(encoder).encode(originalPassword);
        assertThat(userDTO.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("Debería de usar el ModelMapper para las conversiones correctamente.")
    void testCreateUserModelMapperConversion() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(mapper.map(userDTO, User.class)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(savedUserEntity);
        when(mapper.map(savedUserEntity, UserDTO.class)).thenReturn(new UserDTO());

        userService.createUser(userDTO);

        verify(mapper).map(userDTO, User.class);
        verify(mapper).map(savedUserEntity, UserDTO.class);
    }


    @Test
    @DisplayName("Debería obtener el listado de usuarios de manera paginada.")
    void testFindAllUsers() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(mapper.map(userEntities.get(0), UserDTO.class)).thenReturn(userDTOs.get(0));
        when(mapper.map(userEntities.get(1), UserDTO.class)).thenReturn(userDTOs.get(1));
        when(mapper.map(userEntities.get(2), UserDTO.class)).thenReturn(userDTOs.get(2));

        Page<UserDTO> result = userService.getAllUsers(page, size);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("test.user1");
        assertThat(result.getContent().get(1).getUsername()).isEqualTo("test.user2");
        assertThat(result.getContent().get(2).getUsername()).isEqualTo("test.user3");

        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);

        verify(userRepository).findAll(pageable);
        verify(mapper).map(userEntities.get(0), UserDTO.class);
        verify(mapper).map(userEntities.get(1), UserDTO.class);
        verify(mapper).map(userEntities.get(2), UserDTO.class);
    }

    @Test
    @DisplayName("Debería obtener el listado vacío de usuarios de manera paginada.")
    void testFindAllUsersEmpty() {
        int page = 1;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0L);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<UserDTO> result = userService.getAllUsers(page, size);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(10);

        verify(userRepository).findAll(pageable);
        verify(mapper, never()).map(any(User.class), eq(UserDTO.class));
    }

    @Test
    @DisplayName("Debería obtener el listado de usuarios filtrado por role de manera paginada.")
    void testFindAllUsersByRole() {
        RoleUser role = RoleUser.USER;

        List<User> userEntities = Arrays.asList(this.userEntities.get(1), this.userEntities.get(2));
        List<UserDTO> userDTOs = Arrays.asList(this.userDTOs.get(1), this.userDTOs.get(2));

        Page<User> userPageFilteringByRole = new PageImpl<>(userEntities, PageRequest.of(0, 10), 25L);

        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        when(userRepository.findByRole(pageable, role)).thenReturn(userPageFilteringByRole);
        when(mapper.map(userEntities.get(0), UserDTO.class)).thenReturn(userDTOs.get(0));
        when(mapper.map(userEntities.get(1), UserDTO.class)).thenReturn(userDTOs.get(1));

        Page<UserDTO> result = userService.getAllUsers(page, size, role);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("test.user2");
        assertThat(result.getContent().get(1).getUsername()).isEqualTo("test.user3");

        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);

        verify(userRepository).findByRole(pageable, role);
        verify(mapper).map(userEntities.get(0), UserDTO.class);
        verify(mapper).map(userEntities.get(1), UserDTO.class);
    }

    @Test
    @DisplayName("Debería obtener el listado de usuarios filtrado por usuario de creación de manera paginada.")
    void testFindAllUsersByCreatedBy() {
        String uidUser = "USER-CREATE-0000001";

        List<User> userEntities = Arrays.asList(this.userEntities.get(0), this.userEntities.get(1), this.userEntities.get(2));
        List<UserDTO> userDTOs = Arrays.asList(this.userDTOs.get(0), this.userDTOs.get(1), this.userDTOs.get(2));

        Page<User> userPageFilteringByCreatedBy = new PageImpl<>(userEntities, PageRequest.of(0, 10), 25L);

        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        when(userRepository.findByCreatedBy(pageable, uidUser)).thenReturn(userPageFilteringByCreatedBy);
        when(mapper.map(userEntities.get(0), UserDTO.class)).thenReturn(userDTOs.get(0));
        when(mapper.map(userEntities.get(1), UserDTO.class)).thenReturn(userDTOs.get(1));
        when(mapper.map(userEntities.get(2), UserDTO.class)).thenReturn(userDTOs.get(2));

        Page<UserDTO> result = userService.getAllUsersOfUser(page, size, uidUser);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("test.user1");
        assertThat(result.getContent().get(1).getUsername()).isEqualTo("test.user2");
        assertThat(result.getContent().get(2).getUsername()).isEqualTo("test.user3");

        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);

        verify(userRepository).findByCreatedBy(pageable, uidUser);
        verify(mapper).map(userEntities.get(0), UserDTO.class);
        verify(mapper).map(userEntities.get(1), UserDTO.class);
        verify(mapper).map(userEntities.get(2), UserDTO.class);
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException si no se envía un id de usuario.")
    void testFindUserByIdBadRequestException() {
        assertThrows(BadRequestException.class, () -> userService.getUser(null));
        verify(userRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException si el id de usuario no existe en la base de datos.")
    void testFindUserByIdNotFoundExceptionException() {
        String uid = "NON-EXISTENT-ID";

        when(userRepository.findById(uid)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(uid));

        verify(userRepository).findById(uid);
        verify(mapper, never()).map(any(User.class), eq(UserDTO.class));
    }

    @Test
    @DisplayName("Debería obtener el usuario filtrado por id.")
    void testFindUserByIdSuccess() {
        String uid = "TEST-USER-001";

        UserDTO expectedResult = new UserDTO();
        expectedResult.setId(uid);
        expectedResult.setUsername("test.user");
        expectedResult.setPassword("encodedPasswordTest");
        expectedResult.setRole(RoleUser.USER);
        expectedResult.setCompleteName("Test User");

        when(userRepository.findById(uid)).thenReturn(Optional.of(savedUserEntity));
        when(mapper.map(savedUserEntity, UserDTO.class)).thenReturn(expectedResult);

        UserDTO result = userService.getUser(uid);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo("TEST-USER-001");
        assertThat(result.getUsername()).isEqualTo("test.user");

        verify(userRepository).findById(uid);
        verify(mapper).map(savedUserEntity, UserDTO.class);
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException si no se envía un username.")
    void testFindUserByUsernameBadRequestException() {
        assertThrows(BadRequestException.class, () -> userService.getByUsername(null));
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException si el username no existe en la base de datos.")
    void testFindUserByUsernameNotFoundExceptionException() {
        String username = "NON-EXISTENT-USERNAME";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getByUsername(username));

        verify(userRepository).findByUsername(username);
        verify(mapper, never()).map(any(User.class), eq(UserDTO.class));
    }

    @Test
    @DisplayName("Debería obtener el usuario por el campo username.")
    void testFindUserByUsernameSuccess() {
        String username = "test.user";

        UserDTO expectedResult = new UserDTO();
        expectedResult.setId("TEST-USER-001");
        expectedResult.setUsername("test.user");
        expectedResult.setPassword("encodedPasswordTest");
        expectedResult.setRole(RoleUser.USER);
        expectedResult.setCompleteName("Test User");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(savedUserEntity));
        when(mapper.map(savedUserEntity, UserDTO.class)).thenReturn(expectedResult);

        UserDTO result = userService.getByUsername(username);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test.user");

        verify(userRepository).findByUsername(username);
        verify(mapper).map(savedUserEntity, UserDTO.class);
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException si el usuario no tiene un id.")
    void testUpdateUserBadRequestException() {
        UserDTO userForUpdate = new UserDTO();

        assertThrows(BadRequestException.class, () -> userService.updateUser(userForUpdate));

        verify(userRepository, never()).existsById(anyString());
        verify(mapper, never()).map(any(UserDTO.class), eq(User.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException si el id de usuario no existe en la base de datos.")
    void testUpdateUserNotFoundException() {
        UserDTO userForUpdate = new UserDTO();
        userForUpdate.setId("NON_EXISTENT_ID");

        when(userRepository.existsById(userForUpdate.getId())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.updateUser(userForUpdate));

        verify(userRepository).existsById(userForUpdate.getId());
        verify(mapper, never()).map(any(UserDTO.class), eq(User.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debería actualizar el valor del completeName del usuario indicado.")
    void testUpdateUserSuccess() {
        String oldCompleteNameOfUser = savedUserEntity.getCompleteName();

        UserDTO userForUpdate = new UserDTO();
        userForUpdate.setId("TEST-USER-001");
        userForUpdate.setUsername("test.user");
        userForUpdate.setPassword("plainPassword");
        userForUpdate.setCompleteName("Test User Updated");
        userForUpdate.setRole(RoleUser.USER);

        User userUpdatedEntity = new User();
        userUpdatedEntity.setId("TEST-USER-001");
        userUpdatedEntity.setUsername("test.user");
        userUpdatedEntity.setPassword("plainPassword");
        userUpdatedEntity.setCompleteName("Test User Updated");
        userUpdatedEntity.setRole(RoleUser.USER);

        when(userRepository.existsById(userForUpdate.getId())).thenReturn(true);
        when(mapper.map(userForUpdate, User.class)).thenReturn(userUpdatedEntity);
        when(userRepository.save(userUpdatedEntity)).thenReturn(userUpdatedEntity);
        when(mapper.map(userUpdatedEntity, UserDTO.class)).thenReturn(userForUpdate);

        UserDTO userUpdated = userService.updateUser(userForUpdate);

        assertThat(userUpdated.getCompleteName()).isEqualTo(userForUpdate.getCompleteName());
        assertThat(userUpdated.getCompleteName()).isNotEqualTo(oldCompleteNameOfUser);

        verify(userRepository).existsById(userForUpdate.getId());
        verify(mapper).map(userForUpdate, User.class);
        verify(userRepository).save(userUpdatedEntity);
        verify(mapper).map(userUpdatedEntity, UserDTO.class);
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException si el id de usuario no envía para ser borrado.")
    void testDeleteUserBadRequestException() {
        assertThrows(BadRequestException.class, () -> userService.deleteUser(null));

        verify(userRepository, never()).existsById(anyString());
        verify(userRepository, never()).changeActive(anyString(), anyBoolean());
    }

    @Test
    @DisplayName("Debería lanzar NotFoundException si el id de usuario no existe en la base de datos para eliminarlo.")
    void testDeleteUserNotFoundException() {
        String uidUser = "NON_EXISTENT_ID";
        when(userRepository.existsById(uidUser)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(uidUser));

        verify(userRepository).existsById(uidUser);
        verify(userRepository, never()).changeActive(anyString(), anyBoolean());
    }

    @Test
    @DisplayName("Debería inhabilitar el usuario ya que hace un borrado lógico.")
    void testDeleteUserSuccess() {
        String uidUser = "TEST-USER-001";

        when(userRepository.existsById(uidUser)).thenReturn(true);
        when(userRepository.changeActive(uidUser, false)).thenReturn(1);

        String message = userService.deleteUser(uidUser);

        assertThat(message).containsAnyOf("ha sido borrado", uidUser);

        verify(userRepository).existsById(uidUser);
        verify(userRepository).changeActive(uidUser, false);
    }


}
