package br.com.fiap.techchallengefase02.domain.repository;

import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;

import java.util.Optional;
import java.util.UUID;

public interface ItemCardapioRepository {

    ItemCardapio salvar(ItemCardapio itemCardapio);
    Optional<ItemCardapio> buscarPorId(UUID id);
    ResultadoPaginado<ItemCardapio> listarPorRestauranteId(UUID restauranteId, int pagina, int tamanho);
    boolean existePorId(UUID id);
    void excluirPorId(UUID id);
}
