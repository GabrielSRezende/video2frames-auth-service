package br.com.video2frames.video2frames_auth_service.application.usecase;

import br.com.video2frames.video2frames_auth_service.application.dto.AuthResult;
import br.com.video2frames.video2frames_auth_service.application.dto.LoginCommand;
import br.com.video2frames.video2frames_auth_service.application.port.PasswordHasher;
import br.com.video2frames.video2frames_auth_service.application.service.TokenIssuer;
import br.com.video2frames.video2frames_auth_service.domain.exception.InvalidCredentialsException;
import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

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
                .orElseThrow(() -> new InvalidCredentialsException("E-mail ou senha incorretos"));

        if (!passwordHasher.matches(command.rawPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("E-mail ou senha incorretos");
        }

        return tokenIssuer.issueFor(user);
    }
}
