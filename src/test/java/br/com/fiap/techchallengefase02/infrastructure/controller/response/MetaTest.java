package br.com.fiap.techchallengefase02.infrastructure.controller.response;

import br.com.fiap.techchallengefase02.application.dto.Pagina;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MetaTest {

    @Test
    void deveIndicarProximaPaginaSemAnteriorQuandoForPrimeiraPagina() {
        Pagina<String> pagina = new Pagina<>(List.of("a"), 15L, 3, 0, 5);

        Meta meta = Meta.from(pagina);

        assertThat(meta.totalElements()).isEqualTo(15L);
        assertThat(meta.totalPages()).isEqualTo(3);
        assertThat(meta.page()).isZero();
        assertThat(meta.size()).isEqualTo(5);
        assertThat(meta.hasNext()).isTrue();
        assertThat(meta.hasPrevious()).isFalse();
    }

    @Test
    void deveIndicarAnteriorSemProximaQuandoForUltimaPagina() {
        Pagina<String> pagina = new Pagina<>(List.of("a"), 15L, 3, 2, 5);

        Meta meta = Meta.from(pagina);

        assertThat(meta.hasNext()).isFalse();
        assertThat(meta.hasPrevious()).isTrue();
    }
}
