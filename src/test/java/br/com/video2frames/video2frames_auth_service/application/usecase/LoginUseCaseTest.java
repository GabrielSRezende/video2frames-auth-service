package br.com.video2frames.video2frames_auth_service.application.usecase;

import br.com.video2frames.video2frames_auth_service.application.dto.AuthResult;
import br.com.video2frames.video2frames_auth_service.application.dto.LoginCommand;
import br.com.video2frames.video2frames_auth_service.application.port.PasswordHasher;
import br.com.video2frames.video2frames_auth_service.application.service.TokenIssuer;
import br.com.video2frames.video2frames_auth_service.domain.exception.InvalidCredentialsException;
import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenIssuer tokenIssuer;

    private LoginUseCase useCase;

    private final User user = User.reconstruct(
            java.util.UUID.randomUUID(), "gabriel@video2frames.com", "hash-armazenado",
            java.time.OffsetDateTime.now());

    @BeforeEach
    void setUp() {
        useCase = new LoginUseCase(userRepository, passwordHasher, tokenIssuer);
    }

    @Test
    void execute_quandoUsuarioNaoExiste_lancaInvalidCredentialsException() {
        when(userRepository.findByEmail("naoexiste@video2frames.com")).thenReturn(Optional.empty());

        LoginCommand command = new LoginCommand("naoexiste@video2frames.com", "qualquer");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(tokenIssuer, never()).issueFor(any(User.class));
    }

    @Test
    void execute_quandoSenhaIncorreta_lancaInvalidCredentialsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordHasher.matches("senha-errada", user.getPasswordHash())).thenReturn(false);

        LoginCommand command = new LoginCommand(user.getEmail(), "senha-errada");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(tokenIssuer, never()).issueFor(any(User.class));
    }

    @Test
    void execute_quandoCredenciaisCorretas_retornaAuthResultDoTokenIssuer() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordHasher.matches("senha-correta", user.getPasswordHash())).thenReturn(true);

        AuthResult expected = new AuthResult("access", "refresh", 900L);
        when(tokenIssuer.issueFor(user)).thenReturn(expected);

        LoginCommand command = new LoginCommand(user.getEmail(), "senha-correta");

        AuthResult result = useCase.execute(command);

        assertThat(result).isEqualTo(expected);
    }
}
