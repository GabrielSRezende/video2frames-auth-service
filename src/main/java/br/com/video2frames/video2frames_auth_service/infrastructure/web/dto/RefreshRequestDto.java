package br.com.video2frames.video2frames_auth_service.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDto(@NotBlank String refreshToken) {
}
