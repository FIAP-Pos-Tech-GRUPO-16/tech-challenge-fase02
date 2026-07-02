package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Dados para associação de um tipo de usuário a um usuário existente")
public record AssociarTipoUsuarioRequest(

        @NotNull(message = "Tipo de usuário é obrigatório")
        @Schema(example = "11111111-1111-1111-1111-111111111111")
        UUID tipoUsuarioId
) {
}
