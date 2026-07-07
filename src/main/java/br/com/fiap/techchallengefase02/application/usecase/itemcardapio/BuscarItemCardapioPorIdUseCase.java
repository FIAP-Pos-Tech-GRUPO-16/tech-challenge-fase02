package br.com.fiap.techchallengefase02.application.usecase.itemcardapio;

import br.com.fiap.techchallengefase02.application.dto.ItemCardapioResponse;
import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;
import br.com.fiap.techchallengefase02.domain.repository.ItemCardapioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class BuscarItemCardapioPorIdUseCase {

    private final ItemCardapioRepository itemCardapioRepository;

    public BuscarItemCardapioPorIdUseCase(ItemCardapioRepository itemCardapioRepository) {
        this.itemCardapioRepository = itemCardapioRepository;
    }

    public ItemCardapioResponse executar(UUID id) {
        ItemCardapio itemCardapio = itemCardapioRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Item de cardápio não encontrado"));
        return ItemCardapioResponseFactory.criar(itemCardapio);
    }
}
