package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Dados para criacao de restaurante")
public record CriarRestauranteRequest(

        @NotBlank(message = "Nome é obrigatorio")
        @Size(min = 3, max = 255)
        @Schema(example = "Cantina da Fiap")
        String nome,

        @Valid
        @NotNull(message = "Endereco é obrigatorio")
        EnderecoDTO endereco,

        @NotBlank(message = "Tipo de cozinha é obrigatorio")
        @Size(min = 3, max = 255)
        @Schema(example = "Italiana")
        String tipoCozinha,

        @NotBlank(message = "Horario de funcionamento é obrigatorio")
        @Size(min = 3, max = 255)
        @Schema(example = "Segunda a sexta, das 11h as 22h")
        String horarioFuncionamento,

        @NotNull(message = "Dono do restaurante é obrigatorio")
        @Schema(example = "22222222-2222-2222-2222-222222222222")
        UUID donoId
) {
}
