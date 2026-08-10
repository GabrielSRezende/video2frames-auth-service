package br.com.video2frames.video2frames_auth_service.application.usecase;

import br.com.video2frames.video2frames_auth_service.application.dto.RegisterCommand;
import br.com.video2frames.video2frames_auth_service.application.port.PasswordHasher;
import br.com.video2frames.video2frames_auth_service.domain.exception.EmailAlreadyRegisteredException;
import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private RegisterUserUseCase useCase;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCase(userRepository, passwordHasher);
    }

    @Test
    void execute_quandoEmailJaCadastrado_lancaEmailAlreadyRegisteredException() {
        when(userRepository.existsByEmail("gabriel@video2frames.com")).thenReturn(true);

        RegisterCommand command = new RegisterCommand("gabriel@video2frames.com", "senha123");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(EmailAlreadyRegisteredException.class)
                .hasMessageContaining("gabriel@video2frames.com");

        verify(userRepository, never()).save(any());
        verify(passwordHasher, never()).hash(anyString());
    }

    @Test
    void execute_quandoEmailDisponivel_hasheiaASenhaESalvaOUsuario() {
        when(userRepository.existsByEmail("novo@video2frames.com")).thenReturn(false);
        when(passwordHasher.hash("senha123")).thenReturn("senha-hasheada");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterCommand command = new RegisterCommand("novo@video2frames.com", "senha123");

        useCase.execute(command);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("novo@video2frames.com");
        assertThat(saved.getPasswordHash()).isEqualTo("senha-hasheada");
    }
}
