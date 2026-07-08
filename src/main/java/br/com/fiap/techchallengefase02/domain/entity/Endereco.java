package br.com.fiap.techchallengefase02.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Endereco {

    private final String rua;
    private final String numero;
    private final String cidade;
    private final String cep;
}