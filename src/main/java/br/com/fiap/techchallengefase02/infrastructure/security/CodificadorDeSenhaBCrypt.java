package br.com.fiap.techchallengefase02.infrastructure.security;

import br.com.fiap.techchallengefase02.application.port.CodificadorDeSenha;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Implementação de {@link CodificadorDeSenha} usando BCrypt. Mantém a
 * dependência do Spring Security restrita à infraestrutura.
 */
@Component
public class CodificadorDeSenhaBCrypt implements CodificadorDeSenha {

    private final BCryptPasswordEncoder passwordEncoder;

    public CodificadorDeSenhaBCrypt(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String codificar(String senhaPura) {
        return passwordEncoder.encode(senhaPura);
    }

    @Override
    public boolean corresponde(String senhaPura, String senhaCodificada) {
        return passwordEncoder.matches(senhaPura, senhaCodificada);
    }
}
