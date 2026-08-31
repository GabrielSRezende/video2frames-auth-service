package br.com.video2frames.video2frames_auth_service.application.usecase;

import br.com.video2frames.video2frames_auth_service.application.dto.RegisterCommand;
import br.com.video2frames.video2frames_auth_service.application.port.PasswordHasher;
import br.com.video2frames.video2frames_auth_service.domain.exception.EmailAlreadyRegisteredException;
import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public void execute(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            log.warn("Tentativa de registro com e-mail já cadastrado: {}", command.email());
            throw new EmailAlreadyRegisteredException(command.email());
        }

        User user = User.register(command.email(), passwordHasher.hash(command.rawPassword()));
        userRepository.save(user);
        log.info("Novo usuário registrado: {}", command.email());
    }
}
