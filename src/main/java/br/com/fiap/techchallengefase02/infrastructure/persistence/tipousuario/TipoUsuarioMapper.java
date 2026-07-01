package br.com.fiap.techchallengefase02.infrastructure.persistence.tipousuario;

import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import org.mapstruct.Mapper;

/**
 * Conversão entre a entidade de domínio {@link TipoUsuario} e a entidade
 * JPA {@link TipoUsuarioJpaEntity}.
 */
@Mapper(componentModel = "spring")
public interface TipoUsuarioMapper {

    TipoUsuario toDomain(TipoUsuarioJpaEntity entity);

    TipoUsuarioJpaEntity toJpaEntity(TipoUsuario tipoUsuario);
}
