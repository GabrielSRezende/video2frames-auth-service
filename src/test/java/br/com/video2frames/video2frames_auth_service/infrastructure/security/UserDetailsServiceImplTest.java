package br.com.video2frames.video2frames_auth_service.infrastructure.security;

import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_quandoUsuarioExiste_retornaUserDetailsComEmailEHash() {
        User user = User.reconstruct(
                UUID.randomUUID(), "gabriel@video2frames.com", "senha-hash", OffsetDateTime.now());
        when(userRepository.findByEmail("gabriel@video2frames.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("gabriel@video2frames.com");

        assertThat(details.getUsername()).isEqualTo("gabriel@video2frames.com");
        assertThat(details.getPassword()).isEqualTo("senha-hash");
    }

    @Test
    void loadUserByUsername_quandoUsuarioNaoExiste_lancaUsernameNotFoundException() {
        when(userRepository.findByEmail("naoexiste@video2frames.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("naoexiste@video2frames.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
