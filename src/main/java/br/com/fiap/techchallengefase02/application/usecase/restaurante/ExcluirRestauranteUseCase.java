package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Caso de uso: excluir permanentemente um restaurante pelo ID.
 */
@Component
public class ExcluirRestauranteUseCase {

    private final RestauranteRepository restauranteRepository;

    public ExcluirRestauranteUseCase(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    public void executar(UUID id) {
        if (!restauranteRepository.existePorId(id)) {
            throw new NoSuchElementException("Restaurante nao encontrado");
        }
        restauranteRepository.excluirPorId(id);
    }
}
