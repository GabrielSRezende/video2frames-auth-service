package br.com.video2frames.video2frames_auth_service.infrastructure.persistence.adapter;

import br.com.video2frames.video2frames_auth_service.domain.model.RefreshToken;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.mapper.RefreshTokenMapper;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.repository.RefreshTokenJpaRepository;
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
class RefreshTokenRepositoryAdapterTest {

    @Mock
    private RefreshTokenJpaRepository jpaRepository;

    @Mock
    private RefreshTokenMapper mapper;

    private RefreshTokenRepositoryAdapter adapter;

    private final UUID id = UUID.randomUUID();
    private final RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity(
            id, UUID.randomUUID(), "hash", OffsetDateTime.now().plusDays(7), false, OffsetDateTime.now());
    private final RefreshToken domainToken = RefreshToken.reconstruct(
            id, entity.getUserId(), "hash", entity.getExpiresAt(), false, entity.getCreatedAt());

    @BeforeEach
    void setUp() {
        adapter = new RefreshTokenRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    void findByTokenHash_quandoEncontrado_retornaTokenMapeado() {
        when(jpaRepository.findByTokenHash("hash")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domainToken);

        Optional<RefreshToken> result = adapter.findByTokenHash("hash");

        assertThat(result).contains(domainToken);
    }

    @Test
    void findByTokenHash_quandoNaoEncontrado_retornaOptionalVazio() {
        when(jpaRepository.findByTokenHash("hash-inexistente")).thenReturn(Optional.empty());

        assertThat(adapter.findByTokenHash("hash-inexistente")).isEmpty();
    }

    @Test
    void save_mapeiaParaEntidadeSalvaEDevolveDeVoltaParaOhDominio() {
        when(mapper.toEntity(domainToken)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domainToken);

        RefreshToken saved = adapter.save(domainToken);

        assertThat(saved).isEqualTo(domainToken);
    }
}
