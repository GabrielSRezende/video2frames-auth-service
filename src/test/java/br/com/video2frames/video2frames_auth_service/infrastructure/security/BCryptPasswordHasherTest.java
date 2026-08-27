package br.com.video2frames.video2frames_auth_service.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordHasherTest {

    private final BCryptPasswordHasher hasher = new BCryptPasswordHasher();

    @Test
    void hash_produzValorDiferenteDaSenhaOriginal() {
        String hash = hasher.hash("senha123");

        assertThat(hash).isNotEqualTo("senha123");
        assertThat(hash).startsWith("$2a$");
    }

    @Test
    void matches_quandoSenhaCorreta_retornaTrue() {
        String hash = hasher.hash("senha123");

        assertThat(hasher.matches("senha123", hash)).isTrue();
    }

    @Test
    void matches_quandoSenhaIncorreta_retornaFalse() {
        String hash = hasher.hash("senha123");

        assertThat(hasher.matches("senha-errada", hash)).isFalse();
    }

    @Test
    void hash_duasChamadasParaMesmaSenhaGeramHashesDiferentes() {
        // BCrypt usa salt aleatório — isso é o que impede ataques de rainbow table.
        String hash1 = hasher.hash("senha123");
        String hash2 = hasher.hash("senha123");

        assertThat(hash1).isNotEqualTo(hash2);
    }
}
