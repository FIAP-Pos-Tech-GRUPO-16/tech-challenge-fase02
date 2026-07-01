package br.com.fiap.techchallengefase02.exceptions;

public class ResourceAlreadyExistsException extends BusinessException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
