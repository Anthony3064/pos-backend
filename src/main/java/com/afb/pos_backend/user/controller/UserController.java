package com.afb.pos_backend.user.controller;

import com.afb.pos_backend.common.dto.RoleUser;
import com.afb.pos_backend.user.dto.UserDTO;
import com.afb.pos_backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserDTO user) {
        return ResponseEntity.ok(service.createUser(user));
    }

    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(value = "role", required = false) RoleUser role) {
        Page<UserDTO> users;
        if (role == null) {
            users = service.getAllUsers(page, size);
        } else {
            users = service.getAllUsers(page, size, role);
        }
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping(path = "/byUser/{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<UserDTO>> getAllUsersByCreatedBy(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllUsersOfUser(page, size, username));
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getUser(id));
    }

    @PutMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Valid UserDTO user) {
        return ResponseEntity.status(HttpStatus.OK).body(service.updateUser(user));
    }

    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.deleteUser(id));
    }

}
