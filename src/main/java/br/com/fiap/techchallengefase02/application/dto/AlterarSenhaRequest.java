package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para troca de senha do usuário")
public record AlterarSenhaRequest(

        @NotBlank(message = "Login é obrigatório")
        @Size(min = 3, max = 255)
        @Schema(example = "joao123")
        String login,

        @NotBlank(message = "Senha atual é obrigatória")
        @Size(min = 6, max = 255)
        @Schema(type = "string", example = "123456")
        String senhaAtual,

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 6, max = 255)
        @Schema(type = "string", example = "123456")
        String novaSenha
) {
}
