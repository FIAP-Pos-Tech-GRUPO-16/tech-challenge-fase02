package br.com.fiap.techchallengefase02.application.usecase.itemcardapio;

import br.com.fiap.techchallengefase02.application.dto.ItemCardapioResponse;
import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;
import br.com.fiap.techchallengefase02.domain.repository.ItemCardapioRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarItemCardapioPorIdUseCaseTest {

    @InjectMocks
    private BuscarItemCardapioPorIdUseCase useCase;
    @Mock
    private ItemCardapioRepository itemCardapioRepository;

    @Test
    void deveBuscarItemPorId() {
        UUID id = UUID.randomUUID();
        ItemCardapio item = ItemCardapio.builder().id(id).nome("Lasanha").preco(BigDecimal.TEN).build();
        when(itemCardapioRepository.buscarPorId(id)).thenReturn(Optional.of(item));

        ItemCardapioResponse response = useCase.executar(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.nome()).isEqualTo("Lasanha");
    }

    @Test
    void deveLancarExcecaoQuandoItemNaoExistir() {
        UUID id = UUID.randomUUID();
        when(itemCardapioRepository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Item de cardápio não encontrado");
    }
}
