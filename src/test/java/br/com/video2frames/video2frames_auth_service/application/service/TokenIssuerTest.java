package br.com.video2frames.video2frames_auth_service.application.service;

import br.com.video2frames.video2frames_auth_service.application.dto.AuthResult;
import br.com.video2frames.video2frames_auth_service.application.port.TokenGenerator;
import br.com.video2frames.video2frames_auth_service.application.port.TokenHasher;
import br.com.video2frames.video2frames_auth_service.domain.model.RefreshToken;
import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.domain.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenIssuerTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenHasher tokenHasher;

    @Mock
    private TokenGenerator tokenGenerator;

    private TokenIssuer tokenIssuer;

    @BeforeEach
    void setUp() {
        tokenIssuer = new TokenIssuer(refreshTokenRepository, tokenHasher, tokenGenerator);
    }

    @Test
    void issueFor_geraAccessTokenERefreshTokenESalvaOHashDoRefresh() {
        User user = User.reconstruct(UUID.randomUUID(), "gabriel@video2frames.com", "hash", OffsetDateTime.now());

        when(tokenGenerator.generateAccessToken(user.getEmail())).thenReturn("access-token");
        when(tokenGenerator.generateOpaqueRefreshToken()).thenReturn("refresh-cru");
        when(tokenGenerator.refreshTokenExpiration()).thenReturn(Duration.ofDays(7));
        when(tokenGenerator.accessTokenExpirationSeconds()).thenReturn(900L);
        when(tokenHasher.hash("refresh-cru")).thenReturn("refresh-hasheado");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResult result = tokenIssuer.issueFor(user);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-cru"); // o valor CRU vai pro cliente, não o hash
        assertThat(result.expiresInSeconds()).isEqualTo(900L);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getTokenHash()).isEqualTo("refresh-hasheado"); // no banco só o hash
        assertThat(captor.getValue().getUserId()).isEqualTo(user.getId());
        assertThat(captor.getValue().isRevoked()).isFalse();
    }
}
