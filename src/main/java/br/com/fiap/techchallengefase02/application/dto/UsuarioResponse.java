package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados retornados do usuário")
public record UsuarioResponse(

        @Schema(example = "b3fa85e7-9c9b-4c2a-a9c3-123456789abc")
        UUID id,

        @Schema(example = "João Silva")
        String nome,

        @Schema(example = "joao@email.com")
        String email,

        @Schema(example = "joao123")
        String login,

        @Schema(example = "11111111-1111-1111-1111-111111111111")
        UUID tipoUsuarioId,

        EnderecoDTO endereco,

        @Schema(example = "2024-01-15T10:30:00")
        LocalDateTime dataUltimaAlteracao
) {
}
