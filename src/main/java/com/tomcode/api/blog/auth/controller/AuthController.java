package com.tomcode.api.blog.auth.controller;

import com.tomcode.api.blog.auth.entity.RefreshToken;
import com.tomcode.api.blog.auth.service.JWTService;
import com.tomcode.api.blog.auth.service.RefreshTokenService;
import com.tomcode.api.blog.user.dto.CreateUserDTO;
import com.tomcode.api.blog.user.entity.User;
import com.tomcode.api.blog.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CreateUserDTO dto) {
        
        UUID userId = userService.createUser(dto);
        return ResponseEntity
                .status(201)
                .body(Map.of(
                        "id", userId,
                        "email", dto.getEmail()
                ));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletResponse res) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        User user = userService.findByEmail(req.email()).orElseThrow();

        String accessToken = jwtService.generateAccessToken(user);

        String rawRefresh = refreshService.create(user.getEmail());
        addRefreshCookie(res, rawRefresh);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "role", user.getRole().toString()
        ));
    }

    // ------------------------
    // REFRESH TOKEN
    // ------------------------
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refresh_token", required = false) String raw,
            HttpServletResponse res
    ) {
        if (raw == null) {
            return ResponseEntity.status(401).build();
        }

        RefreshToken rt = refreshService.findActive(raw)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // ⬇️ TU dokładnie jest to miejsce
        User user = userService.findByEmail(rt.getUserEmail())
                .orElseThrow();

        String accessToken = jwtService.generateAccessToken(user);

        String newRaw = refreshService.rotate(rt);
        addRefreshCookie(res, newRaw);

        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }


    // ------------------------
    // ME (SPRAWDZENIE LOGINU)
    // ------------------------
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(Map.of(
                "email", auth.getName()
        ));
    }

    // ------------------------
    // LOGOUT
    // ------------------------
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse res,
                                    @CookieValue(value = "refresh_token", required = false) String raw) {

        if (raw != null) {
            refreshService.findActive(raw).ifPresent(rt -> {
                rt.setRevokedAt(java.time.Instant.now());
            });
        }

        // Wygaszenie cookie
        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        res.addCookie(cookie);

        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }


    // ------------------------
    // UTIL - ustawianie refresh cookie
    // ------------------------
    private void addRefreshCookie(HttpServletResponse res, String rawToken) {
        Cookie cookie = new Cookie("refresh_token", rawToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(14 * 24 * 60 * 60); // 14 dni
        // cookie.setSecure(true); // Włączasz gdy masz HTTPS
        res.addCookie(cookie);
    }

    // ------------------------
    // DTO
    // ------------------------
    public record LoginRequest(String email, String password) {}
}
