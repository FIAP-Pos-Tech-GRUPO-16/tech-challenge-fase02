package br.com.fiap.techchallengefase02.infrastructure.persistence.tipousuario;

import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import org.mapstruct.Mapper;

/**
 * Conversão entre a entidade de domínio {@link TipoUsuario} e a entidade
 * JPA {@link TipoUsuarioJpaEntity}.
 *
 * O sentido domínio → JPA ({@code toJpaEntity}) ainda não é usado nesta
 * etapa (não há criação/atualização de Tipo de Usuário aqui), mas já fica
 * pronto para o Membro 2 completar o CRUD.
 */
@Mapper(componentModel = "spring")
public interface TipoUsuarioMapper {

    TipoUsuario toDomain(TipoUsuarioJpaEntity entity);

    TipoUsuarioJpaEntity toJpaEntity(TipoUsuario tipoUsuario);
}
