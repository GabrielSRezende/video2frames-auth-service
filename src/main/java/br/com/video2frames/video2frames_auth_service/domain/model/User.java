package br.com.video2frames.video2frames_auth_service.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class User {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final OffsetDateTime createdAt;

    private User(UUID id, String email, String passwordHash, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public static User register(String email, String passwordHash) {
        return new User(null, email, passwordHash, OffsetDateTime.now());
    }

    public static User reconstruct(UUID id, String email, String passwordHash, OffsetDateTime createdAt) {
        return new User(id, email, passwordHash, createdAt);
    }

    public User withId(UUID id) {
        return new User(id, this.email, this.passwordHash, this.createdAt);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
