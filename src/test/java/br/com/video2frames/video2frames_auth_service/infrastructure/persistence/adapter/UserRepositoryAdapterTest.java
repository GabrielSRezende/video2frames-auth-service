package br.com.video2frames.video2frames_auth_service.infrastructure.persistence.adapter;

import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.entity.UserJpaEntity;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.mapper.UserMapper;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @Mock
    private UserMapper mapper;

    private UserRepositoryAdapter adapter;

    private final UUID id = UUID.randomUUID();
    private final UserJpaEntity entity = new UserJpaEntity(id, "gabriel@video2frames.com", "hash", OffsetDateTime.now());
    private final User domainUser = User.reconstruct(id, "gabriel@video2frames.com", "hash", OffsetDateTime.now());

    @BeforeEach
    void setUp() {
        adapter = new UserRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    void findByEmail_quandoEncontrado_retornaUsuarioMapeado() {
        when(jpaRepository.findByEmail("gabriel@video2frames.com")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domainUser);

        Optional<User> result = adapter.findByEmail("gabriel@video2frames.com");

        assertThat(result).contains(domainUser);
    }

    @Test
    void findByEmail_quandoNaoEncontrado_retornaOptionalVazio() {
        when(jpaRepository.findByEmail("naoexiste@video2frames.com")).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmail("naoexiste@video2frames.com");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_quandoEncontrado_retornaUsuarioMapeado() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domainUser);

        Optional<User> result = adapter.findById(id);

        assertThat(result).contains(domainUser);
    }

    @Test
    void existsByEmail_delegaParaOJpaRepository() {
        when(jpaRepository.existsByEmail("gabriel@video2frames.com")).thenReturn(true);

        assertThat(adapter.existsByEmail("gabriel@video2frames.com")).isTrue();
    }

    @Test
    void save_mapeiaParaEntidadeSalvaEDevolveDeVoltaParaOhDominio() {
        when(mapper.toEntity(domainUser)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domainUser);

        User saved = adapter.save(domainUser);

        assertThat(saved).isEqualTo(domainUser);
    }
}
