package br.com.fiap.techchallengefase02.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Objeto de valor (value object) que representa um endereço no sistema,
 * usado por Usuário e Restaurante. Imutável: para alterar um endereço,
 * cria-se uma nova instância. Entidade de domínio pura, sem qualquer
 * dependência de framework.
 */
@Getter
@Builder
@AllArgsConstructor
public class Endereco {

    private final String rua;
    private final String numero;
    private final String cidade;
    private final String cep;
}