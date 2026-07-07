package br.com.fiap.techchallengefase02.application.usecase.itemcardapio;

import br.com.fiap.techchallengefase02.domain.repository.ItemCardapioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class ExcluirItemCardapioUseCase {

    private final ItemCardapioRepository itemCardapioRepository;

    public ExcluirItemCardapioUseCase(ItemCardapioRepository itemCardapioRepository) {
        this.itemCardapioRepository = itemCardapioRepository;
    }

    public void executar(UUID id) {
        if (!itemCardapioRepository.existePorId(id)) {
            throw new NoSuchElementException("Item de cardápio não encontrado");
        }
        itemCardapioRepository.excluirPorId(id);
    }
}
