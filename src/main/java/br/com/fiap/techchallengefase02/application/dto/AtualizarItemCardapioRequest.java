package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados para atualização de item de cardápio")
public record AtualizarItemCardapioRequest(

        @NotBlank(message = "Nome e obrigatório")
        @Size(min = 3, max = 255)
        @Schema(example = "Lasanha bolonhesa")
        String nome,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(min = 3, max = 1000)
        @Schema(example = "Massa fresca com molho bolonhesa e queijo gratinado")
        String descricao,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        @Schema(example = "49.90")
        BigDecimal preco,

        @Schema(example = "false")
        boolean disponivelApenasNoLocal,

        @Size(max = 500)
        @Schema(example = "/imagens/cardapio/lasanha.jpg")
        String caminhoFoto,

        @NotNull(message = "Restaurante é obrigatório")
        @Schema(example = "b3fa85e7-9c9b-4c2a-a9c3-123456789abc")
        UUID restauranteId
) {
}
