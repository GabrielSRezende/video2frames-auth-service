package br.com.video2frames.video2frames_auth_service.application.usecase;

import br.com.video2frames.video2frames_auth_service.application.dto.AuthResult;
import br.com.video2frames.video2frames_auth_service.application.port.TokenHasher;
import br.com.video2frames.video2frames_auth_service.application.service.TokenIssuer;
import br.com.video2frames.video2frames_auth_service.domain.exception.InvalidCredentialsException;
import br.com.video2frames.video2frames_auth_service.domain.model.RefreshToken;
import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.domain.repository.RefreshTokenRepository;
import br.com.video2frames.video2frames_auth_service.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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
                .orElseThrow(() -> {
                    log.warn("Tentativa de refresh com token desconhecido");
                    return new InvalidCredentialsException("Refresh token inválido");
                });

        if (!stored.isValid()) {
            log.warn("Tentativa de refresh com token expirado ou revogado para o usuário: {}", stored.getUserId());
            throw new InvalidCredentialsException("Refresh token expirado ou revogado");
        }

        refreshTokenRepository.save(stored.revoke());

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> {
                    log.warn("Refresh token válido, mas usuário não existe mais: {}", stored.getUserId());
                    return new InvalidCredentialsException("Usuário do refresh token não existe mais");
                });

        log.info("Token renovado com sucesso para o usuário: {}", user.getEmail());
        return tokenIssuer.issueFor(user);
    }
}
