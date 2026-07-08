package br.com.fiap.techchallengefase02.application.dto;

import java.util.List;

public record Pagina<T>(
        List<T> conteudo,
        long totalElementos,
        int totalPaginas,
        int pagina,
        int tamanho
) {
}
