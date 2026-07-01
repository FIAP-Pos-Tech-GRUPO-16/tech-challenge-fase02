package br.com.fiap.techchallengefase02.domain.exception;

/**
 * Exceção base para violações de regra de negócio.
 * O mapeamento para status HTTP é responsabilidade exclusiva do handler
 * de exceções na camada de infraestrutura — o domínio não conhece HTTP.
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String message) {
        super(message);
    }
}