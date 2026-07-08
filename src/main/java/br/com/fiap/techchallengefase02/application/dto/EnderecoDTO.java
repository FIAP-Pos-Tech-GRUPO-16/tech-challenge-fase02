package br.com.fiap.techchallengefase02.application.dto;

import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados de endereço (compartilhado entre usuário e restaurante)")
public record EnderecoDTO(

        @NotBlank(message = "Rua é obrigatória")
        @Size(min = 3, max = 255)
        @Schema(example = "Rua das Flores")
        String rua,

        @NotBlank(message = "Número é obrigatório")
        @Pattern(regexp = "\\d{1,20}", message = "O número deve conter apenas dígitos (máx. 20)")
        @Schema(example = "123")
        String numero,

        @NotBlank(message = "Cidade é obrigatória")
        @Size(min = 3, max = 255)
        @Schema(example = "São Paulo")
        String cidade,

        @Pattern(regexp = "\\d{8}", message = "O CEP deve conter exatamente 8 números")
        @Schema(example = "01310100")
        String cep
) {

    public Endereco paraDominio() {
        return Endereco.builder()
                .rua(rua.trim())
                .numero(numero.trim())
                .cidade(cidade.trim())
                .cep(cep == null ? null : cep.trim())
                .build();
    }
}
