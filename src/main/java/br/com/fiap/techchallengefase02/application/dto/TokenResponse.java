package br.com.fiap.techchallengefase02.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token JWT de autenticação")
public record TokenResponse(

        @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2FvMTIzIn0.abc")
        String token,

        @Schema(example = "Bearer")
        String tipo
) {
    public TokenResponse(String token) {
        this(token, "Bearer");
    }
}
