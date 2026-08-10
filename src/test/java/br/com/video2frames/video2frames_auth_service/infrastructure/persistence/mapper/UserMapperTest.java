package br.com.video2frames.video2frames_auth_service.infrastructure.persistence.mapper;

import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toEntity_convertObjetoDeDominioParaEntidadeJpa() {
        UUID id = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        User user = User.reconstruct(id, "gabriel@video2frames.com", "hash", createdAt);

        UserJpaEntity entity = mapper.toEntity(user);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo("gabriel@video2frames.com");
        assertThat(entity.getPasswordHash()).isEqualTo("hash");
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void toDomain_convertEntidadeJpaParaObjetoDeDominio() {
        UUID id = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        UserJpaEntity entity = new UserJpaEntity(id, "gabriel@video2frames.com", "hash", createdAt);

        User user = mapper.toDomain(entity);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo("gabriel@video2frames.com");
        assertThat(user.getPasswordHash()).isEqualTo("hash");
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
    }
}
