package br.com.video2frames.video2frames_auth_service.infrastructure.web.controller;

import br.com.video2frames.video2frames_auth_service.application.dto.AuthResult;
import br.com.video2frames.video2frames_auth_service.application.usecase.LoginUseCase;
import br.com.video2frames.video2frames_auth_service.application.usecase.RefreshTokenUseCase;
import br.com.video2frames.video2frames_auth_service.application.usecase.RegisterUserUseCase;
import br.com.video2frames.video2frames_auth_service.domain.exception.EmailAlreadyRegisteredException;
import br.com.video2frames.video2frames_auth_service.domain.exception.InvalidCredentialsException;
import br.com.video2frames.video2frames_auth_service.infrastructure.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @Mock
    private LoginUseCase loginUseCase;

    @Mock
    private RefreshTokenUseCase refreshTokenUseCase;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(registerUserUseCase, loginUseCase, refreshTokenUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void register_comDadosValidos_retorna201() throws Exception {
        String body = objectMapper.writeValueAsString(
                new br.com.video2frames.video2frames_auth_service.infrastructure.web.dto.RegisterRequestDto(
                        "gabriel@video2frames.com", "senha123"));

        mockMvc.perform(post("/api/auth/register").contentType("application/json").content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void register_comEmailInvalido_retorna400() throws Exception {
        String body = objectMapper.writeValueAsString(
                new br.com.video2frames.video2frames_auth_service.infrastructure.web.dto.RegisterRequestDto(
                        "nao-e-um-email", "senha123"));

        mockMvc.perform(post("/api/auth/register").contentType("application/json").content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_comEmailJaCadastrado_retorna409() throws Exception {
        org.mockito.Mockito.doThrow(new EmailAlreadyRegisteredException("gabriel@video2frames.com"))
                .when(registerUserUseCase).execute(any());

        String body = objectMapper.writeValueAsString(
                new br.com.video2frames.video2frames_auth_service.infrastructure.web.dto.RegisterRequestDto(
                        "gabriel@video2frames.com", "senha123"));

        mockMvc.perform(post("/api/auth/register").contentType("application/json").content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("E-mail já cadastrado: gabriel@video2frames.com"));
    }

    @Test
    void login_comCredenciaisValidas_retorna200ComOsTokens() throws Exception {
        when(loginUseCase.execute(any())).thenReturn(new AuthResult("access-token", "refresh-token", 900L));

        String body = objectMapper.writeValueAsString(
                new br.com.video2frames.video2frames_auth_service.infrastructure.web.dto.LoginRequestDto(
                        "gabriel@video2frames.com", "senha123"));

        mockMvc.perform(post("/api/auth/login").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    void login_comCredenciaisInvalidas_retorna401() throws Exception {
        when(loginUseCase.execute(any())).thenThrow(new InvalidCredentialsException("E-mail ou senha incorretos"));

        String body = objectMapper.writeValueAsString(
                new br.com.video2frames.video2frames_auth_service.infrastructure.web.dto.LoginRequestDto(
                        "gabriel@video2frames.com", "senha-errada"));

        mockMvc.perform(post("/api/auth/login").contentType("application/json").content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("E-mail ou senha incorretos"));
    }

    @Test
    void refresh_comTokenValido_retorna200ComOsNovosTokens() throws Exception {
        when(refreshTokenUseCase.execute("refresh-cru"))
                .thenReturn(new AuthResult("novo-access", "novo-refresh", 900L));

        String body = objectMapper.writeValueAsString(
                new br.com.video2frames.video2frames_auth_service.infrastructure.web.dto.RefreshRequestDto("refresh-cru"));

        mockMvc.perform(post("/api/auth/refresh").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("novo-access"));
    }

    @Test
    void refresh_comTokenInvalido_retorna401() throws Exception {
        when(refreshTokenUseCase.execute("token-invalido"))
                .thenThrow(new InvalidCredentialsException("Refresh token inválido"));

        String body = objectMapper.writeValueAsString(
                new br.com.video2frames.video2frames_auth_service.infrastructure.web.dto.RefreshRequestDto("token-invalido"));

        mockMvc.perform(post("/api/auth/refresh").contentType("application/json").content(body))
                .andExpect(status().isUnauthorized());
    }
}
