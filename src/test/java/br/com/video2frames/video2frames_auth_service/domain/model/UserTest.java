package br.com.video2frames.video2frames_auth_service.domain.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void register_criaUsuarioSemId() {
        User user = User.register("gabriel@video2frames.com", "hash");

        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo("gabriel@video2frames.com");
        assertThat(user.getPasswordHash()).isEqualTo("hash");
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void withId_retornaCopiaComIdMantendoOsDemaisCampos() {
        User user = User.register("gabriel@video2frames.com", "hash");
        UUID id = UUID.randomUUID();

        User withId = user.withId(id);

        assertThat(withId.getId()).isEqualTo(id);
        assertThat(withId.getEmail()).isEqualTo(user.getEmail());
        assertThat(withId.getPasswordHash()).isEqualTo(user.getPasswordHash());
        assertThat(user.getId()).isNull(); // imutabilidade
    }

    @Test
    void reconstruct_restauraUsuarioExistente() {
        UUID id = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusDays(3);

        User user = User.reconstruct(id, "gabriel@video2frames.com", "hash", createdAt);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
    }
}
