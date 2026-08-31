package br.com.video2frames.video2frames_auth_service.infrastructure.web.controller;

import br.com.video2frames.video2frames_auth_service.application.dto.LoginCommand;
import br.com.video2frames.video2frames_auth_service.application.dto.RegisterCommand;
import br.com.video2frames.video2frames_auth_service.application.usecase.LoginUseCase;
import br.com.video2frames.video2frames_auth_service.application.usecase.RefreshTokenUseCase;
import br.com.video2frames.video2frames_auth_service.application.usecase.RegisterUserUseCase;
import br.com.video2frames.video2frames_auth_service.infrastructure.web.dto.AuthResponseDto;
import br.com.video2frames.video2frames_auth_service.infrastructure.web.dto.LoginRequestDto;
import br.com.video2frames.video2frames_auth_service.infrastructure.web.dto.RefreshRequestDto;
import br.com.video2frames.video2frames_auth_service.infrastructure.web.dto.RegisterRequestDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    public AuthController(
            RegisterUserUseCase registerUserUseCase,
            LoginUseCase loginUseCase,
            RefreshTokenUseCase refreshTokenUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto request) {
        log.info("Recebida requisição de registro para o e-mail: {}", request.email());
        registerUserUseCase.execute(new RegisterCommand(request.email(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        log.info("Recebida requisição de login para o e-mail: {}", request.email());
        var result = loginUseCase.execute(new LoginCommand(request.email(), request.password()));
        return ResponseEntity.ok(AuthResponseDto.from(result));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody RefreshRequestDto request) {
        log.info("Recebida requisição de refresh de token");
        var result = refreshTokenUseCase.execute(request.refreshToken());
        return ResponseEntity.ok(AuthResponseDto.from(result));
    }
}
