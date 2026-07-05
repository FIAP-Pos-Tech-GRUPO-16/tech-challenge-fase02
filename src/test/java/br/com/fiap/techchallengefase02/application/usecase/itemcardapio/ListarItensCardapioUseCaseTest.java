package br.com.fiap.techchallengefase02.application.usecase.itemcardapio;

import br.com.fiap.techchallengefase02.application.dto.ItemCardapioResponse;
import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;
import br.com.fiap.techchallengefase02.domain.repository.ItemCardapioRepository;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarItensCardapioUseCaseTest {

    @InjectMocks
    private ListarItensCardapioUseCase useCase;
    @Mock
    private ItemCardapioRepository itemCardapioRepository;

    @Test
    void deveListarItensPorRestaurante() {
        UUID restauranteId = UUID.randomUUID();
        ItemCardapio item = ItemCardapio.builder().id(UUID.randomUUID()).nome("Lasanha").preco(BigDecimal.TEN).restauranteId(restauranteId).build();
        ResultadoPaginado<ItemCardapio> resultado = new ResultadoPaginado<>(List.of(item), 1, 1, 0, 10);
        when(itemCardapioRepository.listarPorRestauranteId(restauranteId, 0, 10)).thenReturn(resultado);

        Pagina<ItemCardapioResponse> pagina = useCase.executar(restauranteId, 0, 10);

        assertThat(pagina.conteudo()).hasSize(1);
        assertThat(pagina.totalElementos()).isEqualTo(1);
    }

    @Test
    void deveLancarExcecaoQuandoRestauranteIdAusente() {
        assertThatThrownBy(() -> useCase.executar(null, 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("restauranteId é obrigatório");
    }
}
