package br.com.fiap.techchallengefase02.application.usecase.itemcardapio;

import br.com.fiap.techchallengefase02.application.dto.ItemCardapioResponse;
import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;
import br.com.fiap.techchallengefase02.domain.repository.ItemCardapioRepository;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ListarItensCardapioUseCase {

    private final ItemCardapioRepository itemCardapioRepository;

    public ListarItensCardapioUseCase(ItemCardapioRepository itemCardapioRepository) {
        this.itemCardapioRepository = itemCardapioRepository;
    }

    public Pagina<ItemCardapioResponse> executar(UUID restauranteId, int pagina, int tamanho) {
        if (restauranteId == null) {
            throw new IllegalArgumentException("restauranteId é obrigatório");
        }

        ResultadoPaginado<ItemCardapio> resultado = itemCardapioRepository.listarPorRestauranteId(restauranteId, pagina, tamanho);
        return new Pagina<>(
                resultado.conteudo().stream().map(ItemCardapioResponseFactory::criar).toList(),
                resultado.totalElementos(),
                resultado.totalPaginas(),
                resultado.pagina(),
                resultado.tamanho()
        );
    }
}
