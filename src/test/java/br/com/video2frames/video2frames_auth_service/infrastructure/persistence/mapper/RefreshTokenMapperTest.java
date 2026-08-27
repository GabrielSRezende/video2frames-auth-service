package br.com.video2frames.video2frames_auth_service.infrastructure.persistence.mapper;

import br.com.video2frames.video2frames_auth_service.domain.model.RefreshToken;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenMapperTest {

    private final RefreshTokenMapper mapper = new RefreshTokenMapper();

    @Test
    void toEntity_convertObjetoDeDominioParaEntidadeJpa() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(7);
        OffsetDateTime createdAt = OffsetDateTime.now();
        RefreshToken token = RefreshToken.reconstruct(id, userId, "hash", expiresAt, true, createdAt);

        RefreshTokenJpaEntity entity = mapper.toEntity(token);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getTokenHash()).isEqualTo("hash");
        assertThat(entity.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(entity.isRevoked()).isTrue();
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void toDomain_convertEntidadeJpaParaObjetoDeDominio() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(7);
        OffsetDateTime createdAt = OffsetDateTime.now();
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity(id, userId, "hash", expiresAt, false, createdAt);

        RefreshToken token = mapper.toDomain(entity);

        assertThat(token.getId()).isEqualTo(id);
        assertThat(token.getUserId()).isEqualTo(userId);
        assertThat(token.getTokenHash()).isEqualTo("hash");
        assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(token.isRevoked()).isFalse();
        assertThat(token.getCreatedAt()).isEqualTo(createdAt);
    }
}
