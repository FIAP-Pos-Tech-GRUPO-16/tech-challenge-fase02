package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação de tipo de usuário")
public record CriarTipoUsuarioRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100)
        @Schema(example = "Cliente")
        String nome
) {
}
