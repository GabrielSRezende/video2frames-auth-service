package br.com.video2frames.video2frames_auth_service.infrastructure.persistence.mapper;

import br.com.video2frames.video2frames_auth_service.domain.model.User;
import br.com.video2frames.video2frames_auth_service.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(user.getId(), user.getEmail(), user.getPasswordHash(), user.getCreatedAt());
    }

    public User toDomain(UserJpaEntity entity) {
        return User.reconstruct(entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getCreatedAt());
    }
}
