package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados retornados do item de cardápio")
public record ItemCardapioResponse(

        @Schema(example = "b3fa85e7-9c9b-4c2a-a9c3-123456789abc")
        UUID id,

        @Schema(example = "Lasanha bolonhesa")
        String nome,

        @Schema(example = "Massa fresca com molho bolonhesa e queijo gratinado")
        String descricao,

        @Schema(example = "49.90")
        BigDecimal preco,

        @Schema(example = "false")
        boolean disponivelApenasNoLocal,

        @Schema(example = "/imagens/cardapio/lasanha.jpg")
        String caminhoFoto,

        @Schema(example = "b3fa85e7-9c9b-4c2a-a9c3-123456789abc")
        UUID restauranteId
) {
}
