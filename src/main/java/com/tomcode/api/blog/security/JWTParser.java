package com.tomcode.api.blog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JWTParser {
    @Value("${jwt.secret}")
    private String secretKey;

    public boolean isAdmin(String token) {
        Claims claims = decodeJWT(token);
        String role = claims.get("role", String.class);
        return "ADMIN".equals(role);
    }
    private Claims decodeJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}