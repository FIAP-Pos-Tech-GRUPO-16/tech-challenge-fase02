package br.com.fiap.techchallengefase02.infrastructure.persistence.restaurante;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RestauranteJpaRepository extends JpaRepository<RestauranteJpaEntity, UUID> {

}
