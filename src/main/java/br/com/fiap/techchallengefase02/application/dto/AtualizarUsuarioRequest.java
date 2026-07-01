package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização parcial do usuário")
public record AtualizarUsuarioRequest(

        @Size(min = 3, max = 255)
        @Schema(example = "João Silva")
        String nome,

        @Email(message = "Email inválido")
        @Schema(example = "joao@email.com")
        String email,

        @Size(min = 3, max = 255)
        @Schema(example = "joao123")
        String login,

        @Valid
        EnderecoDTO endereco
) {
}
