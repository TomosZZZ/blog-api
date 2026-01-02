package com.tomcode.api.blog.auth.service;

import com.tomcode.api.blog.auth.entity.RefreshToken;
import com.tomcode.api.blog.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    @Value("${app.jwt.refreshTtlDays}")
    private long refreshDays;

    private String generateRawToken() {
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(raw.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String create(String userEmail) {
        String raw = generateRawToken();

        RefreshToken rt = new RefreshToken();
        rt.setUserEmail(userEmail);
        rt.setTokenHash(hash(raw));
        rt.setCreatedAt(Instant.now());
        rt.setExpiresAt(Instant.now().plus(Duration.ofDays(refreshDays)));

        repo.save(rt);
        return raw;
    }


    public Optional<RefreshToken> findActive(String raw) {
        return repo.findByTokenHash(hash(raw))
                .filter(rt -> rt.getRevokedAt() == null)
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()));
    }


    public String rotate(RefreshToken old) {
        old.setRevokedAt(Instant.now());

        String newRaw = create(old.getUserEmail());

        repo.save(old);
        return newRaw;
    }
}
