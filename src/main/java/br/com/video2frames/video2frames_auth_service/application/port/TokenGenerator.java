package br.com.video2frames.video2frames_auth_service.application.port;

import java.time.Duration;

public interface TokenGenerator {

    String generateAccessToken(String subjectEmail);

    long accessTokenExpirationSeconds();

    Duration refreshTokenExpiration();

    String generateOpaqueRefreshToken();
}
