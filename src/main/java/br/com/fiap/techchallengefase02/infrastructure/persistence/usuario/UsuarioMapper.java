package br.com.fiap.techchallengefase02.infrastructure.persistence.usuario;

import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toDomain(UsuarioJpaEntity entity);

    UsuarioJpaEntity toJpaEntity(Usuario usuario);

    Endereco toDomain(EnderecoJpaEmbeddable embeddable);

    EnderecoJpaEmbeddable toJpaEmbeddable(Endereco endereco);
}
