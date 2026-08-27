package br.com.video2frames.video2frames_auth_service.application.port;

import java.time.Duration;

public interface TokenGenerator {

    String generateAccessToken(String subjectEmail);

    long accessTokenExpirationSeconds();

    Duration refreshTokenExpiration();

    /** Gera o valor cru (não-hasheado) do refresh token, de alta entropia. */
    String generateOpaqueRefreshToken();
}
