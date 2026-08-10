package br.com.video2frames.video2frames_auth_service.domain.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

    @Test
    void isValid_quandoNaoRevogadoENaoExpirado_retornaTrue() {
        RefreshToken token = RefreshToken.issue(
                UUID.randomUUID(), "hash", OffsetDateTime.now().plusDays(1));

        assertThat(token.isValid()).isTrue();
    }

    @Test
    void isValid_quandoExpirado_retornaFalse() {
        RefreshToken token = RefreshToken.reconstruct(
                UUID.randomUUID(), UUID.randomUUID(), "hash",
                OffsetDateTime.now().minusMinutes(1), false, OffsetDateTime.now().minusDays(1));

        assertThat(token.isValid()).isFalse();
    }

    @Test
    void isValid_quandoRevogado_retornaFalseMesmoSemExpirar() {
        RefreshToken token = RefreshToken.reconstruct(
                UUID.randomUUID(), UUID.randomUUID(), "hash",
                OffsetDateTime.now().plusDays(1), true, OffsetDateTime.now());

        assertThat(token.isValid()).isFalse();
    }

    @Test
    void revoke_retornaCopiaRevogadaMantendoOsDemaisCampos() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(1);
        OffsetDateTime createdAt = OffsetDateTime.now();

        RefreshToken original = RefreshToken.reconstruct(id, userId, "hash", expiresAt, false, createdAt);
        RefreshToken revoked = original.revoke();

        assertThat(revoked.isRevoked()).isTrue();
        assertThat(revoked.getId()).isEqualTo(id);
        assertThat(revoked.getUserId()).isEqualTo(userId);
        assertThat(revoked.getTokenHash()).isEqualTo("hash");
        assertThat(revoked.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(revoked.getCreatedAt()).isEqualTo(createdAt);
        assertThat(original.isRevoked()).isFalse(); // imutabilidade: original não muda
    }

    @Test
    void issue_criaTokenNaoRevogado() {
        RefreshToken token = RefreshToken.issue(UUID.randomUUID(), "hash", OffsetDateTime.now().plusDays(7));

        assertThat(token.isRevoked()).isFalse();
        assertThat(token.getId()).isNull();
    }
}
