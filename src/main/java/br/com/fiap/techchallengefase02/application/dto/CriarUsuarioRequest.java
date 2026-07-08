package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Dados para criação de usuário")
public record CriarUsuarioRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 255)
        @Schema(example = "João Silva")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Schema(example = "joao@email.com")
        String email,

        @NotBlank(message = "Login é obrigatório")
        @Size(min = 3, max = 255)
        @Schema(example = "joao123")
        String login,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 255)
        @Schema(type = "string", example = "123456")
        String senha,

        @Valid
        @NotNull(message = "Endereço é obrigatório")
        EnderecoDTO endereco,

        @NotNull(message = "Tipo de usuário é obrigatório")
        @Schema(example = "11111111-1111-1111-1111-111111111111")
        UUID tipoUsuarioId
) {
}
