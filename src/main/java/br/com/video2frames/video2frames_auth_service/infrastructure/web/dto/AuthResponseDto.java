package br.com.video2frames.video2frames_auth_service.infrastructure.web.dto;

import br.com.video2frames.video2frames_auth_service.application.dto.AuthResult;

public record AuthResponseDto(String accessToken, String refreshToken, long expiresIn) {

    public static AuthResponseDto from(AuthResult result) {
        return new AuthResponseDto(result.accessToken(), result.refreshToken(), result.expiresInSeconds());
    }
}
