package br.com.fiap.techchallengefase02.infrastructure.controller;

import br.com.fiap.techchallengefase02.application.dto.LoginRequest;
import br.com.fiap.techchallengefase02.application.dto.TokenResponse;
import br.com.fiap.techchallengefase02.application.usecase.autenticacao.AutenticarUsuarioUseCase;
import br.com.fiap.techchallengefase02.infrastructure.controller.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de autenticação. A troca de senha foi movida para
 * {@code PUT /v1/usuarios/{id}/senha} em {@link UsuarioController} — faz
 * mais sentido REST-wise tratar senha como um recurso do próprio usuário.
 *
 * Optou-se por {@code /v1/autenticacao} (em vez de {@code /v1/auth}) para
 * manter consistência total com a regra de nomenclatura em PT-BR do
 * CLAUDE.md, já que também é o nome escolhido para este controller.
 */
@RestController
@RequestMapping("/v1/autenticacao")
@Tag(name = "Autenticação", description = "Autenticação de usuários")
public class AutenticacaoController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    public AutenticacaoController(AutenticarUsuarioUseCase autenticarUsuarioUseCase) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login de usuário",
            description = "Valida login e senha e retorna um token JWT para uso nas demais requisições"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login válido — token JWT retornado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<TokenResponse>> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse token = autenticarUsuarioUseCase.executar(request);
        return ApiSuccessResponse.ok(token, "Usuário autenticado com sucesso");
    }
}
