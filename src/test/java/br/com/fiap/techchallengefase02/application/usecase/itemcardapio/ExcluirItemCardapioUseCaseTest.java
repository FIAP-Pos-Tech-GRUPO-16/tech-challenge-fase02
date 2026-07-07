package br.com.fiap.techchallengefase02.application.usecase.itemcardapio;

import br.com.fiap.techchallengefase02.domain.repository.ItemCardapioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcluirItemCardapioUseCaseTest {

    @InjectMocks
    private ExcluirItemCardapioUseCase useCase;
    @Mock
    private ItemCardapioRepository itemCardapioRepository;

    @Test
    void deveExcluirItemQuandoExistir() {
        UUID id = UUID.randomUUID();
        when(itemCardapioRepository.existePorId(id)).thenReturn(true);

        useCase.executar(id);

        verify(itemCardapioRepository).excluirPorId(id);
    }

    @Test
    void deveLancarExcecaoQuandoItemNaoExistir() {
        UUID id = UUID.randomUUID();
        when(itemCardapioRepository.existePorId(id)).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Item de cardápio não encontrado");
        verify(itemCardapioRepository, never()).excluirPorId(id);
    }
}
