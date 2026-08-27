package br.com.video2frames.video2frames_auth_service.application.dto;

public record RegisterCommand(String email, String rawPassword) {
}
