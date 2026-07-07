package br.com.fiap.techchallengefase02.application.usecase.itemcardapio;

import br.com.fiap.techchallengefase02.application.dto.AtualizarItemCardapioRequest;
import br.com.fiap.techchallengefase02.application.dto.ItemCardapioResponse;
import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;
import br.com.fiap.techchallengefase02.domain.repository.ItemCardapioRepository;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class AtualizarItemCardapioUseCase {

    private final ItemCardapioRepository itemCardapioRepository;
    private final RestauranteRepository restauranteRepository;

    public AtualizarItemCardapioUseCase(ItemCardapioRepository itemCardapioRepository,
                                        RestauranteRepository restauranteRepository) {
        this.itemCardapioRepository = itemCardapioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    public ItemCardapioResponse executar(UUID id, AtualizarItemCardapioRequest request) {
        ItemCardapio itemCardapio = itemCardapioRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Item de cardápio não encontrado"));

        if (!restauranteRepository.existePorId(request.restauranteId())) {
            throw new NoSuchElementException("Restaurante não encontrado");
        }
        if (request.preco() == null || request.preco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }

        itemCardapio.setNome(request.nome().trim());
        itemCardapio.setDescricao(request.descricao().trim());
        itemCardapio.setPreco(request.preco());
        itemCardapio.setDisponivelApenasNoLocal(request.disponivelApenasNoLocal());
        itemCardapio.setCaminhoFoto(normalizarTextoOpcional(request.caminhoFoto()));
        itemCardapio.setRestauranteId(request.restauranteId());

        ItemCardapio itemAtualizado = itemCardapioRepository.salvar(itemCardapio);
        return ItemCardapioResponseFactory.criar(itemAtualizado);
    }

    private String normalizarTextoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
