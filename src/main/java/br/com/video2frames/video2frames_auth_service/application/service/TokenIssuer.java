package br.com.video2frames.video2frames_auth_service.application.service;

import br.com.video2frames.video2frames_auth_service.application.dto.AuthResult;
import br.com.video2frames.video2frames_auth_service.application.port.TokenGenerator;
import br.com.video2frames.video2frames_auth_service.application.port.TokenHasher;
import br.com.video2frames.video2frames_auth_service.domain.model.RefreshToken;
import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.domain.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
public class TokenIssuer {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasher tokenHasher;
    private final TokenGenerator tokenGenerator;

    public TokenIssuer(
            RefreshTokenRepository refreshTokenRepository,
            TokenHasher tokenHasher,
            TokenGenerator tokenGenerator) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenHasher = tokenHasher;
        this.tokenGenerator = tokenGenerator;
    }

    public AuthResult issueFor(User user) {
        String accessToken = tokenGenerator.generateAccessToken(user.getEmail());
        String rawRefreshToken = tokenGenerator.generateOpaqueRefreshToken();

        RefreshToken refreshToken = RefreshToken.issue(
                user.getId(),
                tokenHasher.hash(rawRefreshToken),
                OffsetDateTime.now().plus(tokenGenerator.refreshTokenExpiration()));
        refreshTokenRepository.save(refreshToken);

        log.info("Par de tokens emitido para o usuário: {}", user.getEmail());
        return new AuthResult(accessToken, rawRefreshToken, tokenGenerator.accessTokenExpirationSeconds());
    }
}
