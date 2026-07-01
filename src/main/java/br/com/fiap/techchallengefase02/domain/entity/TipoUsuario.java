package br.com.fiap.techchallengefase02.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidade de domínio que representa o papel de um usuário no sistema
 * (ex: Cliente, Dono de Restaurante).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuario {

    private UUID id;
    private String nome;
}