package br.com.fiap.techchallengefase02.application.port;

import br.com.fiap.techchallengefase02.domain.entity.Usuario;

public interface GeradorDeToken {

    String gerar(Usuario usuario);
}
