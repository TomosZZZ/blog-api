package com.tomcode.api.blog.security;

import com.tomcode.api.blog.exception.BadRequestException;
import com.tomcode.api.blog.exception.ForbiddenException;
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

    public Claims getClaims(String authHeader) {
        String token = extractToken(authHeader);
        return decodeJWT(token);
    }

    public void validateAdminPrivileges(String authHeader) {
        requireAuthHeader(authHeader);
        if (!isAdmin(authHeader)) {
            throw new ForbiddenException("Access denied: admin role required");
        }
    }

    public void validateEditorOrAdminPrivileges(String authHeader) {
        requireAuthHeader(authHeader);
        if (!isEditor(authHeader) && !isAdmin(authHeader)) {
            throw new ForbiddenException("Access denied: editor or admin role required");
        }
    }

    private void requireAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Bad or missing authorization header");
        }
    }

    private boolean isAdmin(String authHeader) {
        Claims claims = getClaims(authHeader);
        return "ADMIN".equals(claims.get("role", String.class));
    }

    private boolean isEditor(String authHeader) {
        Claims claims = getClaims(authHeader);
        return "EDITOR".equals(claims.get("role", String.class));
    }

    private String extractToken(String authHeader) {
        return authHeader.replace("Bearer ", "");
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
