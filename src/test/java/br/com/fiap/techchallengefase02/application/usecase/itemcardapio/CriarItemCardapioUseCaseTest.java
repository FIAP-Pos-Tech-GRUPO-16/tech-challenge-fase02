package br.com.fiap.techchallengefase02.application.usecase.itemcardapio;

import br.com.fiap.techchallengefase02.application.dto.CriarItemCardapioRequest;
import br.com.fiap.techchallengefase02.application.dto.ItemCardapioResponse;
import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;
import br.com.fiap.techchallengefase02.domain.repository.ItemCardapioRepository;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarItemCardapioUseCaseTest {

    @InjectMocks
    private CriarItemCardapioUseCase useCase;
    @Mock
    private ItemCardapioRepository itemCardapioRepository;
    @Mock
    private RestauranteRepository restauranteRepository;

    @Nested
    class CriarComSucesso {

        @Test
        void deveCriarItemCardapioQuandoRestauranteExistir() {
            UUID restauranteId = UUID.randomUUID();
            UUID itemId = UUID.randomUUID();
            CriarItemCardapioRequest request = request(restauranteId, BigDecimal.valueOf(49.90));
            ItemCardapio itemSalvo = item(itemId, restauranteId);
            when(restauranteRepository.existePorId(restauranteId)).thenReturn(true);
            when(itemCardapioRepository.salvar(any(ItemCardapio.class))).thenReturn(itemSalvo);

            ItemCardapioResponse response = useCase.executar(request);

            assertThat(response.id()).isEqualTo(itemId);
            assertThat(response.nome()).isEqualTo("Lasanha");
            assertThat(response.restauranteId()).isEqualTo(restauranteId);
        }

        @Test
        void deveRemoverEspacosEmBrancoAntesDeSalvar() {
            UUID restauranteId = UUID.randomUUID();
            CriarItemCardapioRequest request = new CriarItemCardapioRequest(
                    "  Lasanha  ",
                    "  Massa fresca  ",
                    BigDecimal.valueOf(49.90),
                    false,
                    "  /fotos/lasanha.jpg  ",
                    restauranteId
            );
            ArgumentCaptor<ItemCardapio> captor = ArgumentCaptor.forClass(ItemCardapio.class);
            when(restauranteRepository.existePorId(restauranteId)).thenReturn(true);
            when(itemCardapioRepository.salvar(any(ItemCardapio.class))).thenReturn(item(UUID.randomUUID(), restauranteId));

            useCase.executar(request);

            verify(itemCardapioRepository).salvar(captor.capture());
            ItemCardapio item = captor.getValue();
            assertThat(item.getNome()).isEqualTo("Lasanha");
            assertThat(item.getDescricao()).isEqualTo("Massa fresca");
            assertThat(item.getCaminhoFoto()).isEqualTo("/fotos/lasanha.jpg");
        }
    }

    @Nested
    class ValidacoesDeErro {

        @Test
        void deveLancarExcecaoQuandoRestauranteNaoExistir() {
            UUID restauranteId = UUID.randomUUID();
            when(restauranteRepository.existePorId(restauranteId)).thenReturn(false);

            assertThatThrownBy(() -> useCase.executar(request(restauranteId, BigDecimal.valueOf(49.90))))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Restaurante não encontrado");
            verify(itemCardapioRepository, never()).salvar(any());
        }

        @Test
        void deveLancarExcecaoQuandoPrecoForInvalido() {
            UUID restauranteId = UUID.randomUUID();
            when(restauranteRepository.existePorId(restauranteId)).thenReturn(true);

            assertThatThrownBy(() -> useCase.executar(request(restauranteId, BigDecimal.ZERO)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Preço deve ser maior que zero");
            verify(itemCardapioRepository, never()).salvar(any());
        }
    }

    private CriarItemCardapioRequest request(UUID restauranteId, BigDecimal preco) {
        return new CriarItemCardapioRequest("Lasanha", "Massa fresca", preco, false, "/fotos/lasanha.jpg", restauranteId);
    }

    private ItemCardapio item(UUID id, UUID restauranteId) {
        return ItemCardapio.builder()
                .id(id)
                .nome("Lasanha")
                .descricao("Massa fresca")
                .preco(BigDecimal.valueOf(49.90))
                .disponivelApenasNoLocal(false)
                .caminhoFoto("/fotos/lasanha.jpg")
                .restauranteId(restauranteId)
                .build();
    }
}
