package br.com.fiap.techchallengefase02.infrastructure.controller.response;

import br.com.fiap.techchallengefase02.application.dto.Pagina;

public record Meta(
        long totalElements,
        int totalPages,
        int page,
        int size,
        boolean hasNext,
        boolean hasPrevious
) {

    public static Meta from(Pagina<?> pagina) {
        boolean hasNext = pagina.pagina() < pagina.totalPaginas() - 1;
        boolean hasPrevious = pagina.pagina() > 0;

        return new Meta(
                pagina.totalElementos(),
                pagina.totalPaginas(),
                pagina.pagina(),
                pagina.tamanho(),
                hasNext,
                hasPrevious
        );
    }
}
