package br.com.fiap.techchallengefase02.infrastructure.persistence.itemcardapio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItemCardapioJpaRepository extends JpaRepository<ItemCardapioJpaEntity, UUID> {

    Page<ItemCardapioJpaEntity> findByRestauranteId(UUID restauranteId, Pageable pageable);
}
