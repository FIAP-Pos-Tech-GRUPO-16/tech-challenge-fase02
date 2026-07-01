package br.com.fiap.techchallengefase02.domain.exception;

/**
 * Lançada quando uma operação tentaria criar um recurso que já existe
 * (ex: email ou login duplicado).
 */
public class RecursoJaExistenteException extends RegraDeNegocioException {

    public RecursoJaExistenteException(String message) {
        super(message);
    }
}