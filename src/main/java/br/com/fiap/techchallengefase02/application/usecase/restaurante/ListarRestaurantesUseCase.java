package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.application.dto.RestauranteResponse;
import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import org.springframework.stereotype.Component;

@Component
public class ListarRestaurantesUseCase {

    private final RestauranteRepository restauranteRepository;

    public ListarRestaurantesUseCase(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    public Pagina<RestauranteResponse> executar(int pagina, int tamanho) {
        ResultadoPaginado<Restaurante> resultado = restauranteRepository.listarPaginado(pagina, tamanho);
        return new Pagina<>(
                resultado.conteudo().stream().map(RestauranteResponseFactory::criar).toList(),
                resultado.totalElementos(),
                resultado.totalPaginas(),
                resultado.pagina(),
                resultado.tamanho()
        );
    }
}
