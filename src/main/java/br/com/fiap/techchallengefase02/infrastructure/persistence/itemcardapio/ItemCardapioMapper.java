package br.com.fiap.techchallengefase02.infrastructure.persistence.itemcardapio;

import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemCardapioMapper {

    ItemCardapio toDomain(ItemCardapioJpaEntity entity);
    ItemCardapioJpaEntity toJpaEntity(ItemCardapio itemCardapio);
}
