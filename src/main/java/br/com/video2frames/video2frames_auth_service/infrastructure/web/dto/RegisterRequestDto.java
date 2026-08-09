package br.com.video2frames.video2frames_auth_service.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, message = "A senha precisa ter ao menos 6 caracteres") String password) {
}
