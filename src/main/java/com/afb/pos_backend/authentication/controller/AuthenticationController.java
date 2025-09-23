package com.afb.pos_backend.authentication.controller;

import com.afb.pos_backend.authentication.dto.AuthenticationRequestDTO;
import com.afb.pos_backend.authentication.dto.AuthenticationResponseDTO;
import com.afb.pos_backend.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping(path = "/authenticate", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody @Valid AuthenticationRequestDTO authenticationRequest) {
        return ResponseEntity.ok(service.authenticate(authenticationRequest));
    }

    @PostMapping(path = "/refresh", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthenticationResponseDTO> refresh(@RequestParam("refreshToken") String refreshToken) {
        return ResponseEntity.ok(service.refreshToken(refreshToken));
    }

}
