package br.com.video2frames.video2frames_auth_service.infrastructure.security;

import br.com.video2frames.video2frames_auth_service.application.port.TokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtTokenGenerator implements TokenGenerator {

    private final JwtProperties properties;
    private final SecretKey signingKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public JwtTokenGenerator(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(String subjectEmail) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofMinutes(properties.accessTokenExpirationMinutes()));

        return Jwts.builder()
                .subject(subjectEmail)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public long accessTokenExpirationSeconds() {
        return Duration.ofMinutes(properties.accessTokenExpirationMinutes()).toSeconds();
    }

    @Override
    public Duration refreshTokenExpiration() {
        return Duration.ofDays(properties.refreshTokenExpirationDays());
    }

    @Override
    public String generateOpaqueRefreshToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return UUID.randomUUID() + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public Optional<String> extractSubject(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
