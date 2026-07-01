package br.com.fiap.techchallengefase02.domain.exception;

/**
 * Lançada quando credenciais de autenticação (login/senha) são inválidas.
 */
public class CredenciaisInvalidasException extends RegraDeNegocioException {

    public CredenciaisInvalidasException(String message) {
        super(message);
    }
}
