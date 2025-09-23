package com.afb.pos_backend.authentication.service;

import com.afb.pos_backend.user.persistence.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${app.secret-key}")
    private String SECRET_KEY;

    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("completeName", user.getCompleteName());
        extraClaims.put("role", user.getRole().name());
        Date issueAt = new Date(System.currentTimeMillis());
        long EXPIRATION_TOKEN_IN_MINUTES = 15L;
        Date expiration = new Date((60 * 1000 * EXPIRATION_TOKEN_IN_MINUTES) + issueAt.getTime());
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(user.getUsername())
                .claims(extraClaims)
                .issuedAt(issueAt)
                .expiration(expiration)
                .signWith(generateKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("completeName", user.getCompleteName());
        extraClaims.put("role", user.getRole().name());
        Date issueAt = new Date(System.currentTimeMillis());
        long EXPIRATION_REFRESH_TOKEN_IN_DAYS = 7L;
        Date expiration = new Date((1000L * 60 * 60 * 24 * EXPIRATION_REFRESH_TOKEN_IN_DAYS) + issueAt.getTime());
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(user.getUsername())
                .claims(extraClaims)
                .issuedAt(issueAt)
                .expiration(expiration)
                .signWith(generateKey())
                .compact();
    }

    private SecretKey generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts
                .parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (username.equals(extractedUsername)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

}
