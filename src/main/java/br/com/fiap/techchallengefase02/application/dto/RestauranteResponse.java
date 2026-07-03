package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados retornados do restaurante")
public record RestauranteResponse(

        @Schema(example = "b3fa85e7-9c9b-4c2a-a9c3-123456789abc")
        UUID id,

        @Schema(example = "Cantina da Fiap")
        String nome,

        EnderecoDTO endereco,

        @Schema(example = "Italiana")
        String tipoCozinha,

        @Schema(example = "Segunda a sexta, das 11h às 22h")
        String horarioFuncionamento,

        @Schema(example = "22222222-2222-2222-2222-222222222222")
        UUID donoId
) {
}
