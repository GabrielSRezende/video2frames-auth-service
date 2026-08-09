package br.com.video2frames.video2frames_auth_service.infrastructure.persistence.mapper;

import br.com.video2frames.video2frames_auth_service.domain.model.RefreshToken;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {

    public RefreshTokenJpaEntity toEntity(RefreshToken refreshToken) {
        return new RefreshTokenJpaEntity(
                refreshToken.getId(),
                refreshToken.getUserId(),
                refreshToken.getTokenHash(),
                refreshToken.getExpiresAt(),
                refreshToken.isRevoked(),
                refreshToken.getCreatedAt());
    }

    public RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return RefreshToken.reconstruct(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getCreatedAt());
    }
}
