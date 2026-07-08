package br.com.fiap.techchallengefase02.application.port;

public interface CodificadorDeSenha {

    String codificar(String senhaPura);

    boolean corresponde(String senhaPura, String senhaCodificada);
}
