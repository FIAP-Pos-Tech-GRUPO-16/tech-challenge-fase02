package br.com.fiap.techchallengefase02.application.port;

/**
 * Porta (contrato) para codificação e verificação de senhas.
 *
 * A aplicação depende apenas desta abstração — quem implementa com
 * BCrypt (ou qualquer outro algoritmo) é a infraestrutura
 * ({@code infrastructure.security}), evitando que a camada de aplicação
 * importe classes do Spring Security diretamente.
 */
public interface CodificadorDeSenha {

    String codificar(String senhaPura);

    boolean corresponde(String senhaPura, String senhaCodificada);
}
