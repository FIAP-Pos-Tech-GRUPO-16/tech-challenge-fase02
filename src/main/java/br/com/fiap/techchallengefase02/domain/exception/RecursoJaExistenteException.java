package br.com.fiap.techchallengefase02.domain.exception;

public class RecursoJaExistenteException extends RegraDeNegocioException {

    public RecursoJaExistenteException(String message) {
        super(message);
    }
}