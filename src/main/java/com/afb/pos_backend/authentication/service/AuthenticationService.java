package com.afb.pos_backend.authentication.service;

import com.afb.pos_backend.authentication.dto.AuthenticationRequestDTO;
import com.afb.pos_backend.authentication.dto.AuthenticationResponseDTO;
import com.afb.pos_backend.user.persistence.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserDetailsService userService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authRequest) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authRequest.username(),
                authRequest.password()
        );
        authenticationManager.authenticate(authentication);
        UserDetails user = userService.loadUserByUsername(authRequest.username());
        String accessToken = jwtService.generateToken((User) user);
        String refreshToken = jwtService.generateRefreshToken((User) user);
        return new AuthenticationResponseDTO(accessToken, refreshToken);
    }

    public AuthenticationResponseDTO refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        UserDetails user = userService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(refreshToken, username)) {
            throw new AuthenticationException("El refresh token es inválido o está expirado.") {
            };
        }
        String accessToken = jwtService.generateToken((User) user);
        String newRefreshToken = jwtService.generateRefreshToken((User) user);
        return new AuthenticationResponseDTO(accessToken, newRefreshToken);
    }

}
