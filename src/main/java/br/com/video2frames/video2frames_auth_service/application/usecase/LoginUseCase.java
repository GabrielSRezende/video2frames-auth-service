package br.com.video2frames.video2frames_auth_service.application.usecase;

import br.com.video2frames.video2frames_auth_service.application.dto.AuthResult;
import br.com.video2frames.video2frames_auth_service.application.dto.LoginCommand;
import br.com.video2frames.video2frames_auth_service.application.port.PasswordHasher;
import br.com.video2frames.video2frames_auth_service.application.service.TokenIssuer;
import br.com.video2frames.video2frames_auth_service.domain.exception.InvalidCredentialsException;
import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;

    public LoginUseCase(UserRepository userRepository, PasswordHasher passwordHasher, TokenIssuer tokenIssuer) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
    }

    public AuthResult execute(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> {
                    log.warn("Tentativa de login com e-mail não cadastrado: {}", command.email());
                    return new InvalidCredentialsException("E-mail ou senha incorretos");
                });

        if (!passwordHasher.matches(command.rawPassword(), user.getPasswordHash())) {
            log.warn("Tentativa de login com senha incorreta para o e-mail: {}", command.email());
            throw new InvalidCredentialsException("E-mail ou senha incorretos");
        }

        log.info("Usuário autenticado com sucesso: {}", command.email());
        return tokenIssuer.issueFor(user);
    }
}
