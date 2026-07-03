package br.com.fiap.techchallengefase02.infrastructure.persistence.restaurante;

import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import org.mapstruct.Mapper;

/**
 * Conversão entre a entidade de domínio {@link Restaurante} e a entidade
 * JPA {@link RestauranteJpaEntity}.
 */
@Mapper(componentModel = "spring")
public interface RestauranteMapper {

    Restaurante toDomain(RestauranteJpaEntity entity);
    RestauranteJpaEntity toJpaEntity(Restaurante restaurante);
}
