package br.com.video2frames.video2frames_auth_service.domain.repository;

import br.com.video2frames.video2frames_auth_service.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    RefreshToken save(RefreshToken refreshToken);
}
