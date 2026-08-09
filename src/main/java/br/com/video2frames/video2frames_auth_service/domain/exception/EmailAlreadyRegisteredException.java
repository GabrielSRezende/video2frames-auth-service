package br.com.video2frames.video2frames_auth_service.domain.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super("E-mail já cadastrado: " + email);
    }
}
