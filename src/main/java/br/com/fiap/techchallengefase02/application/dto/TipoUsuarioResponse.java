package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados retornados do tipo de usuário")
public record TipoUsuarioResponse(

        @Schema(example = "11111111-1111-1111-1111-111111111111")
        UUID id,

        @Schema(example = "Cliente")
        String nome
) {
}
