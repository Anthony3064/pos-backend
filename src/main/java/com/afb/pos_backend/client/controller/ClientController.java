package com.afb.pos_backend.client.controller;

import com.afb.pos_backend.client.dto.ClientDTO;
import com.afb.pos_backend.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/clients")
@AllArgsConstructor
public class ClientController {

    private final ClientService service;

    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ClientDTO> createClient(@RequestBody @Valid ClientDTO client) {
        return ResponseEntity.status(HttpStatus.OK).body(service.createClient(client));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<ClientDTO>> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllClients(page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping(path = "/byUser/{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<ClientDTO>> getAllClientsOfUser(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @PathVariable String username
    ) {
        if (search != null) {
            return ResponseEntity.status(HttpStatus.OK).body(service.getAllClientsOfUser(page, size, username, search));
        }

        return ResponseEntity.status(HttpStatus.OK).body(service.getAllClientsOfUser(page, size, username));
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ClientDTO> getClient(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getClient(id));
    }

    @PutMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ClientDTO> updateClient(@RequestBody @Valid ClientDTO client) {
        return ResponseEntity.status(HttpStatus.OK).body(service.updateClient(client));
    }

    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"})
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", service.deleteClient(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
