package br.com.fiap.techchallengefase02.application.usecase.itemcardapio;

import br.com.fiap.techchallengefase02.application.dto.ItemCardapioResponse;
import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;

public final class ItemCardapioResponseFactory {

    private ItemCardapioResponseFactory() {
    }

    public static ItemCardapioResponse criar(ItemCardapio itemCardapio) {
        return new ItemCardapioResponse(
                itemCardapio.getId(),
                itemCardapio.getNome(),
                itemCardapio.getDescricao(),
                itemCardapio.getPreco(),
                itemCardapio.isDisponivelApenasNoLocal(),
                itemCardapio.getCaminhoFoto(),
                itemCardapio.getRestauranteId()
        );
    }
}
