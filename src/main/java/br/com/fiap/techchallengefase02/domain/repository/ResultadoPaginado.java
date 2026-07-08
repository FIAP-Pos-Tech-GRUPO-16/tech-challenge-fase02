package br.com.fiap.techchallengefase02.domain.repository;

import java.util.List;

public record ResultadoPaginado<T>(
        List<T> conteudo,
        long totalElementos,
        int totalPaginas,
        int pagina,
        int tamanho
) {
}
