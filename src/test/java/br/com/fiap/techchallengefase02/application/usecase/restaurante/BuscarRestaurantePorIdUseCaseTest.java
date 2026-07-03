package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.application.dto.RestauranteResponse;
import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarRestaurantePorIdUseCaseTest {

    @InjectMocks
    private BuscarRestaurantePorIdUseCase useCase;
    @Mock
    private RestauranteRepository restauranteRepository;
    @Test
    void deveBuscarRestaurantePorId() {
        UUID id = UUID.randomUUID();
        Restaurante restaurante = Restaurante.builder().id(id).nome("Cantina da Fiap").build();
        when(restauranteRepository.buscarPorId(id)).thenReturn(Optional.of(restaurante));
        RestauranteResponse response = useCase.executar(id);
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.nome()).isEqualTo("Cantina da Fiap");
    }
    @Test
    void deveLancarExcecaoQuandoRestauranteNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(restauranteRepository.buscarPorId(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.executar(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Restaurante não encontrado");
    }
}
