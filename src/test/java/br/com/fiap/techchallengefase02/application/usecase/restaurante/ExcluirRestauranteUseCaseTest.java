package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcluirRestauranteUseCaseTest {

    @InjectMocks
    private ExcluirRestauranteUseCase useCase;
    @Mock
    private RestauranteRepository restauranteRepository;
    @Test
    void deveExcluirRestauranteQuandoExistir() {
        UUID id = UUID.randomUUID();
        when(restauranteRepository.existePorId(id)).thenReturn(true);
        useCase.executar(id);
        verify(restauranteRepository).excluirPorId(id);
    }
    @Test
    void deveLancarExcecaoQuandoRestauranteNaoExistir() {
        UUID id = UUID.randomUUID();
        when(restauranteRepository.existePorId(id)).thenReturn(false);
        assertThatThrownBy(() -> useCase.executar(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Restaurante nao encontrado");
        verify(restauranteRepository, never()).excluirPorId(any());
    }
}
