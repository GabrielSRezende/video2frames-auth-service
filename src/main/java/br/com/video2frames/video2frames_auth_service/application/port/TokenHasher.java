package br.com.video2frames.video2frames_auth_service.application.port;

/**
 * Hash determinístico (diferente de PasswordHasher/BCrypt): precisamos
 * localizar o refresh token pelo hash na base, o que exige que o mesmo
 * valor de entrada sempre produza a mesma saída.
 */
public interface TokenHasher {

    String hash(String rawToken);
}
