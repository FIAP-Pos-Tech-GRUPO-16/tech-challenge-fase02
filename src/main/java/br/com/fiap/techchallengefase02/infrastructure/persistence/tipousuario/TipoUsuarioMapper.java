package br.com.fiap.techchallengefase02.infrastructure.persistence.tipousuario;

import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TipoUsuarioMapper {

    TipoUsuario toDomain(TipoUsuarioJpaEntity entity);

    TipoUsuarioJpaEntity toJpaEntity(TipoUsuario tipoUsuario);
}
