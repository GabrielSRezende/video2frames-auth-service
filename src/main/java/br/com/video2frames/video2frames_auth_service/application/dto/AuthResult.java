package br.com.video2frames.video2frames_auth_service.application.dto;

public record AuthResult(String accessToken, String refreshToken, long expiresInSeconds) {
}
