package br.com.fiap.techchallengefase02.infrastructure.persistence.tipousuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TipoUsuarioJpaRepository extends JpaRepository<TipoUsuarioJpaEntity, UUID> {

    boolean existsByNome(String nome);
}
