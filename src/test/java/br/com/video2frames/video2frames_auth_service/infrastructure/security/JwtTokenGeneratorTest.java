package br.com.video2frames.video2frames_auth_service.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenGeneratorTest {

    private static final String SECRET = "mDMUd7hxqi87Jab3PEoexGDbKUqx548RyntWmtrZJqJ";

    private JwtTokenGenerator generator;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(SECRET, 15, 7);
        generator = new JwtTokenGenerator(properties);
    }

    @Test
    void generateAccessToken_retornaTokenComSubjectExtraivel() {
        String token = generator.generateAccessToken("gabriel@video2frames.com");

        Optional<String> subject = generator.extractSubject(token);

        assertThat(subject).contains("gabriel@video2frames.com");
    }

    @Test
    void accessTokenExpirationSeconds_convertMinutosParaSegundos() {
        assertThat(generator.accessTokenExpirationSeconds()).isEqualTo(15 * 60L);
    }

    @Test
    void refreshTokenExpiration_convertDiasParaDuration() {
        assertThat(generator.refreshTokenExpiration()).isEqualTo(Duration.ofDays(7));
    }

    @Test
    void generateOpaqueRefreshToken_geraValoresDiferentesACadaChamada() {
        String token1 = generator.generateOpaqueRefreshToken();
        String token2 = generator.generateOpaqueRefreshToken();

        assertThat(token1).isNotEqualTo(token2);
        assertThat(token1).isNotBlank();
    }

    @Test
    void extractSubject_quandoTokenInvalido_retornaEmpty() {
        assertThat(generator.extractSubject("token-completamente-invalido")).isEmpty();
    }

    @Test
    void extractSubject_quandoAssinadoComOutraChave_retornaEmpty() {
        SecretKey outraChave = Keys.hmacShaKeyFor("outra-chave-completamente-diferente-desta-aqui".getBytes(StandardCharsets.UTF_8));
        String tokenComOutraAssinatura = Jwts.builder()
                .subject("gabriel@video2frames.com")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(60)))
                .signWith(outraChave)
                .compact();

        assertThat(generator.extractSubject(tokenComOutraAssinatura)).isEmpty();
    }

    @Test
    void extractSubject_quandoTokenExpirado_retornaEmpty() {
        SecretKey signingKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String tokenExpirado = Jwts.builder()
                .subject("gabriel@video2frames.com")
                .issuedAt(Date.from(Instant.now().minusSeconds(120)))
                .expiration(Date.from(Instant.now().minusSeconds(60)))
                .signWith(signingKey)
                .compact();

        assertThat(generator.extractSubject(tokenExpirado)).isEmpty();
    }
}
