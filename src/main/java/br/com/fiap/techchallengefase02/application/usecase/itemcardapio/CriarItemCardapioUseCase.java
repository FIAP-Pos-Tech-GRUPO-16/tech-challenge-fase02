package br.com.fiap.techchallengefase02.application.usecase.itemcardapio;

import br.com.fiap.techchallengefase02.application.dto.CriarItemCardapioRequest;
import br.com.fiap.techchallengefase02.application.dto.ItemCardapioResponse;
import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;
import br.com.fiap.techchallengefase02.domain.repository.ItemCardapioRepository;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Component
public class CriarItemCardapioUseCase {

    private final ItemCardapioRepository itemCardapioRepository;
    private final RestauranteRepository restauranteRepository;

    public CriarItemCardapioUseCase(ItemCardapioRepository itemCardapioRepository,
                                    RestauranteRepository restauranteRepository) {
        this.itemCardapioRepository = itemCardapioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    public ItemCardapioResponse executar(CriarItemCardapioRequest request) {
        if (!restauranteRepository.existePorId(request.restauranteId())) {
            throw new NoSuchElementException("Restaurante não encontrado");
        }
        if (request.preco() == null || request.preco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }

        ItemCardapio itemCardapio = ItemCardapio.builder()
                .nome(request.nome().trim())
                .descricao(request.descricao().trim())
                .preco(request.preco())
                .disponivelApenasNoLocal(request.disponivelApenasNoLocal())
                .caminhoFoto(normalizarTextoOpcional(request.caminhoFoto()))
                .restauranteId(request.restauranteId())
                .build();

        ItemCardapio itemSalvo = itemCardapioRepository.salvar(itemCardapio);
        return ItemCardapioResponseFactory.criar(itemSalvo);
    }

    private String normalizarTextoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
