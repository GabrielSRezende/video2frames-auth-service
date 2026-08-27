package br.com.video2frames.video2frames_auth_service.application.usecase;

import br.com.video2frames.video2frames_auth_service.application.dto.AuthResult;
import br.com.video2frames.video2frames_auth_service.application.port.TokenHasher;
import br.com.video2frames.video2frames_auth_service.application.service.TokenIssuer;
import br.com.video2frames.video2frames_auth_service.domain.exception.InvalidCredentialsException;
import br.com.video2frames.video2frames_auth_service.domain.model.RefreshToken;
import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.domain.repository.RefreshTokenRepository;
import br.com.video2frames.video2frames_auth_service.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenHasher tokenHasher;
    private final TokenIssuer tokenIssuer;

    public RefreshTokenUseCase(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            TokenHasher tokenHasher,
            TokenIssuer tokenIssuer) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tokenHasher = tokenHasher;
        this.tokenIssuer = tokenIssuer;
    }

    public AuthResult execute(String rawRefreshToken) {
        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHasher.hash(rawRefreshToken))
                .orElseThrow(() -> new InvalidCredentialsException("Refresh token inválido"));

        if (!stored.isValid()) {
            throw new InvalidCredentialsException("Refresh token expirado ou revogado");
        }

        // Rotação: o token usado é revogado, mesmo que a emissão do novo falhe depois.
        refreshTokenRepository.save(stored.revoke());

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new InvalidCredentialsException("Usuário do refresh token não existe mais"));

        return tokenIssuer.issueFor(user);
    }
}
