package br.com.fiap.techchallengefase02.application.port;

import br.com.fiap.techchallengefase02.domain.entity.Usuario;

/**
 * Porta (contrato) para geração de token de autenticação.
 *
 * Implementada na infraestrutura por {@code JwtService}, mantendo a
 * biblioteca JJWT fora da camada de aplicação.
 */
public interface GeradorDeToken {

    String gerar(Usuario usuario);
}
