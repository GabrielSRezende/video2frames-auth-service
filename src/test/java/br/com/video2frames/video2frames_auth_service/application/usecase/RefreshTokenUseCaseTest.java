package br.com.video2frames.video2frames_auth_service.application.usecase;

import br.com.video2frames.video2frames_auth_service.application.dto.AuthResult;
import br.com.video2frames.video2frames_auth_service.application.port.TokenHasher;
import br.com.video2frames.video2frames_auth_service.application.service.TokenIssuer;
import br.com.video2frames.video2frames_auth_service.domain.exception.InvalidCredentialsException;
import br.com.video2frames.video2frames_auth_service.domain.model.RefreshToken;
import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.domain.repository.RefreshTokenRepository;
import br.com.video2frames.video2frames_auth_service.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenHasher tokenHasher;

    @Mock
    private TokenIssuer tokenIssuer;

    private RefreshTokenUseCase useCase;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new RefreshTokenUseCase(refreshTokenRepository, userRepository, tokenHasher, tokenIssuer);
    }

    @Test
    void execute_quandoTokenNaoEncontrado_lancaInvalidCredentialsException() {
        when(tokenHasher.hash("token-cru")).thenReturn("hash");
        when(refreshTokenRepository.findByTokenHash("hash")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("token-cru"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(tokenIssuer, never()).issueFor(any());
    }

    @Test
    void execute_quandoTokenExpirado_lancaInvalidCredentialsExceptionSemRotacionar() {
        RefreshToken expirado = RefreshToken.reconstruct(
                UUID.randomUUID(), userId, "hash", OffsetDateTime.now().minusMinutes(1), false, OffsetDateTime.now());

        when(tokenHasher.hash("token-cru")).thenReturn("hash");
        when(refreshTokenRepository.findByTokenHash("hash")).thenReturn(Optional.of(expirado));

        assertThatThrownBy(() -> useCase.execute("token-cru"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(refreshTokenRepository, never()).save(any());
        verify(tokenIssuer, never()).issueFor(any());
    }

    @Test
    void execute_quandoTokenJaRevogado_lancaInvalidCredentialsException() {
        RefreshToken revogado = RefreshToken.reconstruct(
                UUID.randomUUID(), userId, "hash", OffsetDateTime.now().plusDays(1), true, OffsetDateTime.now());

        when(tokenHasher.hash("token-cru")).thenReturn("hash");
        when(refreshTokenRepository.findByTokenHash("hash")).thenReturn(Optional.of(revogado));

        assertThatThrownBy(() -> useCase.execute("token-cru"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void execute_quandoUsuarioDoTokenNaoExisteMais_lancaInvalidCredentialsExceptionMasRevogaOTokenUsado() {
        RefreshToken valido = RefreshToken.reconstruct(
                UUID.randomUUID(), userId, "hash", OffsetDateTime.now().plusDays(1), false, OffsetDateTime.now());

        when(tokenHasher.hash("token-cru")).thenReturn("hash");
        when(refreshTokenRepository.findByTokenHash("hash")).thenReturn(Optional.of(valido));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("token-cru"))
                .isInstanceOf(InvalidCredentialsException.class);

        // Rotação já aconteceu antes da checagem do usuário — o token usado é
        // invalidado mesmo que o fluxo falhe depois.
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().isRevoked()).isTrue();
    }

    @Test
    void execute_quandoTudoValido_revogaOTokenUsadoEEmiteNovoParDeTokens() {
        RefreshToken valido = RefreshToken.reconstruct(
                UUID.randomUUID(), userId, "hash", OffsetDateTime.now().plusDays(1), false, OffsetDateTime.now());
        User user = User.reconstruct(userId, "gabriel@video2frames.com", "senha-hash", OffsetDateTime.now());
        AuthResult expected = new AuthResult("novo-access", "novo-refresh", 900L);

        when(tokenHasher.hash("token-cru")).thenReturn("hash");
        when(refreshTokenRepository.findByTokenHash("hash")).thenReturn(Optional.of(valido));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenIssuer.issueFor(user)).thenReturn(expected);

        AuthResult result = useCase.execute("token-cru");

        assertThat(result).isEqualTo(expected);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().isRevoked()).isTrue();
    }
}
