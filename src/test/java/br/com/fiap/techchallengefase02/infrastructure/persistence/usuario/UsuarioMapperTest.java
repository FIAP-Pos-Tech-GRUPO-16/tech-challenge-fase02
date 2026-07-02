package br.com.fiap.techchallengefase02.infrastructure.persistence.usuario;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cobre o comportamento defensivo do mapper gerado pelo MapStruct diante de
 * entradas nulas — caminho não exercitado pelos testes de integração, que
 * sempre convertem objetos válidos.
 */
class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapperImpl();

    @Test
    void deveRetornarNuloQuandoEntidadeJpaDeUsuarioForNula() {
        assertThat(mapper.toDomain((UsuarioJpaEntity) null)).isNull();
    }

    @Test
    void deveRetornarNuloQuandoUsuarioDeDominioForNulo() {
        assertThat(mapper.toJpaEntity(null)).isNull();
    }

    @Test
    void deveRetornarNuloQuandoEnderecoEmbeddableForNulo() {
        assertThat(mapper.toDomain((EnderecoJpaEmbeddable) null)).isNull();
    }

    @Test
    void deveRetornarNuloQuandoEnderecoDeDominioForNulo() {
        assertThat(mapper.toJpaEmbeddable(null)).isNull();
    }
}
