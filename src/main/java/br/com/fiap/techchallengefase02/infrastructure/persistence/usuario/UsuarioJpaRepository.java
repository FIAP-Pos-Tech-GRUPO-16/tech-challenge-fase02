package br.com.fiap.techchallengefase02.infrastructure.persistence.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {

    Page<UsuarioJpaEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Optional<UsuarioJpaEntity> findByLogin(String login);

    Optional<UsuarioJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);
}
