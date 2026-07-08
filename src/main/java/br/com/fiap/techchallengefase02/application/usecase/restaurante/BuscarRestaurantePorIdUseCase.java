package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.application.dto.RestauranteResponse;
import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class BuscarRestaurantePorIdUseCase {

    private final RestauranteRepository restauranteRepository;

    public BuscarRestaurantePorIdUseCase(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    public RestauranteResponse executar(UUID id) {
        Restaurante restaurante = restauranteRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Restaurante não encontrado"));
        return RestauranteResponseFactory.criar(restaurante);
    }
}
