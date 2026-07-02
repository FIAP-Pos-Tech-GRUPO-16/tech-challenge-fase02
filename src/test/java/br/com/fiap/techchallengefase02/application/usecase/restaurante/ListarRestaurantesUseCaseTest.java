package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.application.dto.RestauranteResponse;
import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarRestaurantesUseCaseTest {

    @InjectMocks
    private ListarRestaurantesUseCase useCase;
    @Mock
    private RestauranteRepository restauranteRepository;
    @Test
    void deveListarRestaurantesPaginados() {
        Restaurante primeiro = Restaurante.builder().id(UUID.randomUUID()).nome("Cantina").build();
        Restaurante segundo = Restaurante.builder().id(UUID.randomUUID()).nome("Bistro").build();
        ResultadoPaginado<Restaurante> resultado = new ResultadoPaginado<>(List.of(primeiro, segundo), 2, 1, 0, 10);
        when(restauranteRepository.listarPaginado(0, 10)).thenReturn(resultado);
        Pagina<RestauranteResponse> pagina = useCase.executar(0, 10);
        assertThat(pagina.conteudo()).hasSize(2);
        assertThat(pagina.conteudo().get(0).nome()).isEqualTo("Cantina");
        assertThat(pagina.totalElementos()).isEqualTo(2);
        assertThat(pagina.totalPaginas()).isEqualTo(1);
    }
}
