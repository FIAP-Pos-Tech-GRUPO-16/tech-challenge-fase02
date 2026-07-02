package br.com.fiap.techchallengefase02.domain.repository;

import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de persistencia de {@link Restaurante}. A implementacao concreta
 * (JPA) vive em {@code infrastructure.persistence.restaurante}.
 */
public interface RestauranteRepository {

    Restaurante salvar(Restaurante restaurante);
    Optional<Restaurante> buscarPorId(UUID id);
    ResultadoPaginado<Restaurante> listarPaginado(int pagina, int tamanho);
    boolean existePorId(UUID id);
    void excluirPorId(UUID id);
}
