package br.com.video2frames.video2frames_auth_service.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        int accessTokenExpirationMinutes,
        int refreshTokenExpirationDays
) {
}
