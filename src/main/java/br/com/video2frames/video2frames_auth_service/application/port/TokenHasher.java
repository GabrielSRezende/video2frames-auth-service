package br.com.video2frames.video2frames_auth_service.application.port;

public interface TokenHasher {

    String hash(String rawToken);
}
