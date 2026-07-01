package br.com.fiap.techchallengefase02.application.dto;

import java.util.List;

/**
 * Envelope de paginação para respostas dos casos de uso.
 * Equivalente, na camada de aplicação, ao {@code ResultadoPaginado} do domínio.
 */
public record Pagina<T>(
        List<T> conteudo,
        long totalElementos,
        int totalPaginas,
        int pagina,
        int tamanho
) {
}
