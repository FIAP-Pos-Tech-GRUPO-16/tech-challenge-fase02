package br.com.fiap.techchallengefase02.domain.repository;

import java.util.List;

/**
 * Envelope de paginação usado nos contratos de repositório do domínio.
 *
 * Existe para que as interfaces de {@code domain.repository} não precisem
 * importar tipos do Spring Data ({@code Page}/{@code Pageable}), o que
 * violaria a regra de "zero dependência de framework" do domínio. A
 * implementação concreta (infraestrutura) é quem converte de/para os tipos
 * do Spring Data.
 */
public record ResultadoPaginado<T>(
        List<T> conteudo,
        long totalElementos,
        int totalPaginas,
        int pagina,
        int tamanho
) {
}
