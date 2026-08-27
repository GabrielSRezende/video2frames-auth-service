package br.com.video2frames.video2frames_auth_service.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class RefreshToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final OffsetDateTime expiresAt;
    private final boolean revoked;
    private final OffsetDateTime createdAt;

    private RefreshToken(
            UUID id, UUID userId, String tokenHash, OffsetDateTime expiresAt,
            boolean revoked, OffsetDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public static RefreshToken issue(UUID userId, String tokenHash, OffsetDateTime expiresAt) {
        return new RefreshToken(null, userId, tokenHash, expiresAt, false, OffsetDateTime.now());
    }

    public static RefreshToken reconstruct(
            UUID id, UUID userId, String tokenHash, OffsetDateTime expiresAt,
            boolean revoked, OffsetDateTime createdAt) {
        return new RefreshToken(id, userId, tokenHash, expiresAt, revoked, createdAt);
    }

    /** Regra de negócio: um refresh token só é utilizável se não foi revogado e não expirou. */
    public boolean isValid() {
        return !revoked && expiresAt.isAfter(OffsetDateTime.now());
    }

    /** Retorna uma cópia revogada — usado na rotação (a cada refresh, o token usado é revogado). */
    public RefreshToken revoke() {
        return new RefreshToken(id, userId, tokenHash, expiresAt, true, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
