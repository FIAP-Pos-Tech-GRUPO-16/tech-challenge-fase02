package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Dados para atualização de restaurante")
public record AtualizarRestauranteRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 255)
        @Schema(example = "Cantina da Fiap")
        String nome,

        @Valid
        @NotNull(message = "Endereço é obrigatório")
        EnderecoDTO endereco,

        @NotBlank(message = "Tipo de cozinha é obrigatório")
        @Size(min = 3, max = 255)
        @Schema(example = "Italiana")
        String tipoCozinha,

        @NotBlank(message = "Horário de funcionamento é obrigatório")
        @Size(min = 3, max = 255)
        @Schema(example = "Segunda a sexta, das 11h às 22h")
        String horarioFuncionamento,

        @NotNull(message = "Dono do restaurante é obrigatório")
        @Schema(example = "22222222-2222-2222-2222-222222222222")
        UUID donoId
) {
}
