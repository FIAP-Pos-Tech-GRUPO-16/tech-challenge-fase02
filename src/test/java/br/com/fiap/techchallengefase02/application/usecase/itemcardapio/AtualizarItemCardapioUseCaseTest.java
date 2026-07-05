package br.com.fiap.techchallengefase02.application.usecase.itemcardapio;

import br.com.fiap.techchallengefase02.application.dto.AtualizarItemCardapioRequest;
import br.com.fiap.techchallengefase02.application.dto.ItemCardapioResponse;
import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;
import br.com.fiap.techchallengefase02.domain.repository.ItemCardapioRepository;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarItemCardapioUseCaseTest {

    @InjectMocks
    private AtualizarItemCardapioUseCase useCase;
    @Mock
    private ItemCardapioRepository itemCardapioRepository;
    @Mock
    private RestauranteRepository restauranteRepository;

    @Nested
    class AtualizarComSucesso {

        @Test
        void deveAtualizarItemQuandoItemERestauranteExistirem() {
            UUID id = UUID.randomUUID();
            UUID restauranteId = UUID.randomUUID();
            ItemCardapio existente = item(id, restauranteId, "Antigo");
            ItemCardapio atualizado = item(id, restauranteId, "Novo");
            when(itemCardapioRepository.buscarPorId(id)).thenReturn(Optional.of(existente));
            when(restauranteRepository.existePorId(restauranteId)).thenReturn(true);
            when(itemCardapioRepository.salvar(any(ItemCardapio.class))).thenReturn(atualizado);

            ItemCardapioResponse response = useCase.executar(id, request(restauranteId, BigDecimal.valueOf(59.90)));

            assertThat(response.nome()).isEqualTo("Novo");
            assertThat(response.restauranteId()).isEqualTo(restauranteId);
        }
    }

    @Nested
    class ValidacoesDeErro {

        @Test
        void deveLancarExcecaoQuandoItemNaoExistir() {
            UUID id = UUID.randomUUID();
            UUID restauranteId = UUID.randomUUID();
            when(itemCardapioRepository.buscarPorId(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.executar(id, request(restauranteId, BigDecimal.valueOf(59.90))))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Item de cardápio não encontrado");
            verify(itemCardapioRepository, never()).salvar(any());
        }

        @Test
        void deveLancarExcecaoQuandoRestauranteNaoExistir() {
            UUID id = UUID.randomUUID();
            UUID restauranteId = UUID.randomUUID();
            when(itemCardapioRepository.buscarPorId(id)).thenReturn(Optional.of(item(id, UUID.randomUUID(), "Antigo")));
            when(restauranteRepository.existePorId(restauranteId)).thenReturn(false);

            assertThatThrownBy(() -> useCase.executar(id, request(restauranteId, BigDecimal.valueOf(59.90))))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Restaurante não encontrado");
            verify(itemCardapioRepository, never()).salvar(any());
        }

        @Test
        void deveLancarExcecaoQuandoPrecoForInvalido() {
            UUID id = UUID.randomUUID();
            UUID restauranteId = UUID.randomUUID();
            when(itemCardapioRepository.buscarPorId(id)).thenReturn(Optional.of(item(id, restauranteId, "Antigo")));
            when(restauranteRepository.existePorId(restauranteId)).thenReturn(true);

            assertThatThrownBy(() -> useCase.executar(id, request(restauranteId, BigDecimal.ZERO)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Preço deve ser maior que zero");
            verify(itemCardapioRepository, never()).salvar(any());
        }
    }

    private AtualizarItemCardapioRequest request(UUID restauranteId, BigDecimal preco) {
        return new AtualizarItemCardapioRequest("Novo", "Descrição nova", preco, true, "/fotos/novo.jpg", restauranteId);
    }

    private ItemCardapio item(UUID id, UUID restauranteId, String nome) {
        return ItemCardapio.builder()
                .id(id)
                .nome(nome)
                .descricao("Descrição")
                .preco(BigDecimal.valueOf(49.90))
                .disponivelApenasNoLocal(false)
                .caminhoFoto("/fotos/item.jpg")
                .restauranteId(restauranteId)
                .build();
    }
}
