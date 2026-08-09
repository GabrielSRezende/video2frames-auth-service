package br.com.video2frames.video2frames_auth_service.domain.repository;

import br.com.video2frames.video2frames_auth_service.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Port de saída (driven port). O domínio declara o que precisa; a
 * infraestrutura (infrastructure.persistence.adapter) é quem implementa,
 * usando JPA por baixo — mas isso é um detalhe que o domínio desconhece.
 */
public interface UserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    boolean existsByEmail(String email);

    User save(User user);
}
