package br.com.fiap.techchallengefase02.infrastructure.persistence.tipousuario;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cobre o comportamento defensivo do mapper gerado pelo MapStruct diante de
 * entradas nulas — caminho não exercitado pelos testes de integração, que
 * sempre convertem objetos válidos.
 */
class TipoUsuarioMapperTest {

    private final TipoUsuarioMapper mapper = new TipoUsuarioMapperImpl();

    @Test
    void deveRetornarNuloQuandoEntidadeJpaForNula() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void deveRetornarNuloQuandoTipoUsuarioDeDominioForNulo() {
        assertThat(mapper.toJpaEntity(null)).isNull();
    }
}
