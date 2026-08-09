package br.com.video2frames.video2frames_auth_service.infrastructure.persistence.adapter;

import br.com.video2frames.video2frames_auth_service.domain.model.RefreshToken;
import br.com.video2frames.video2frames_auth_service.domain.repository.RefreshTokenRepository;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.mapper.RefreshTokenMapper;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;
    private final RefreshTokenMapper mapper;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepository, RefreshTokenMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(mapper::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        var saved = jpaRepository.save(mapper.toEntity(refreshToken));
        return mapper.toDomain(saved);
    }
}
