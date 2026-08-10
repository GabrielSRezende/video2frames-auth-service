package br.com.video2frames.video2frames_auth_service.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Sha256TokenHasherTest {

    private final Sha256TokenHasher hasher = new Sha256TokenHasher();

    @Test
    void hash_ehDeterministico_mesmaEntradaGeraMesmaSaida() {
        // Precisa ser determinístico (diferente do BCrypt) para dar pra buscar
        // o refresh token pelo hash na base.
        String hash1 = hasher.hash("token-cru");
        String hash2 = hasher.hash("token-cru");

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void hash_entradasDiferentesGeramSaidasDiferentes() {
        assertThat(hasher.hash("token-a")).isNotEqualTo(hasher.hash("token-b"));
    }

    @Test
    void hash_retornaHexadecimalDe64Caracteres() {
        String hash = hasher.hash("token-cru");

        assertThat(hash).hasSize(64); // SHA-256 = 32 bytes = 64 chars hex
        assertThat(hash).matches("[0-9a-f]+");
    }
}
