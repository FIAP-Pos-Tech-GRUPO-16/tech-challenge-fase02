package br.com.fiap.techchallengefase02.infrastructure.controller;

import br.com.fiap.techchallengefase02.application.dto.AlterarSenhaRequest;
import br.com.fiap.techchallengefase02.application.dto.AtualizarUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.CriarUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.application.usecase.autenticacao.AlterarSenhaUseCase;
import br.com.fiap.techchallengefase02.application.usecase.usuario.AtualizarUsuarioUseCase;
import br.com.fiap.techchallengefase02.application.usecase.usuario.BuscarUsuarioPorIdUseCase;
import br.com.fiap.techchallengefase02.application.usecase.usuario.BuscarUsuariosPorNomeUseCase;
import br.com.fiap.techchallengefase02.application.usecase.usuario.CriarUsuarioUseCase;
import br.com.fiap.techchallengefase02.application.usecase.usuario.ExcluirUsuarioUseCase;
import br.com.fiap.techchallengefase02.infrastructure.controller.response.ApiSuccessResponse;
import br.com.fiap.techchallengefase02.infrastructure.controller.response.Meta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private final BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;
    private final BuscarUsuariosPorNomeUseCase buscarUsuariosPorNomeUseCase;
    private final ExcluirUsuarioUseCase excluirUsuarioUseCase;
    private final AlterarSenhaUseCase alterarSenhaUseCase;

    public UsuarioController(CriarUsuarioUseCase criarUsuarioUseCase,
                              AtualizarUsuarioUseCase atualizarUsuarioUseCase,
                              BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase,
                              BuscarUsuariosPorNomeUseCase buscarUsuariosPorNomeUseCase,
                              ExcluirUsuarioUseCase excluirUsuarioUseCase,
                              AlterarSenhaUseCase alterarSenhaUseCase) {
        this.criarUsuarioUseCase = criarUsuarioUseCase;
        this.atualizarUsuarioUseCase = atualizarUsuarioUseCase;
        this.buscarUsuarioPorIdUseCase = buscarUsuarioPorIdUseCase;
        this.buscarUsuariosPorNomeUseCase = buscarUsuariosPorNomeUseCase;
        this.excluirUsuarioUseCase = excluirUsuarioUseCase;
        this.alterarSenhaUseCase = alterarSenhaUseCase;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário a partir do seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<UsuarioResponse>> buscarPorId(@PathVariable UUID id) {
        UsuarioResponse response = buscarUsuarioPorIdUseCase.executar(id);
        return ApiSuccessResponse.ok(response);
    }

    @GetMapping
    @Operation(summary = "Buscar usuários por nome", description = "Retorna uma lista paginada de usuários cujo nome contenha o termo informado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    public ResponseEntity<ApiSuccessResponse<List<UsuarioResponse>>> buscarPorNome(
            @Parameter(description = "Nome ou parte do nome do usuário", example = "João")
            @RequestParam String nome,
            @Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pagina<UsuarioResponse> resultado = buscarUsuariosPorNomeUseCase.executar(nome, page, size);
        return ApiSuccessResponse.ok(resultado.conteudo(), Meta.from(resultado));
    }

    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cadastra um novo usuário associado a um tipo de usuário existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "E-mail ou login já cadastrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<UsuarioResponse>> criar(@RequestBody @Valid CriarUsuarioRequest request) {
        UsuarioResponse response = criarUsuarioUseCase.executar(request);
        return ApiSuccessResponse.created(response, "Usuário criado com sucesso");
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar dados do usuário", description = "Atualiza parcialmente os dados do usuário, exceto senha")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "E-mail ou login já cadastrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<UsuarioResponse>> atualizar(@PathVariable UUID id, @RequestBody @Valid AtualizarUsuarioRequest request) {
        UsuarioResponse response = atualizarUsuarioUseCase.executar(id, request);
        return ApiSuccessResponse.ok(response);
    }

    @PutMapping("/{id}/senha")
    @Operation(summary = "Trocar senha do usuário", description = "Altera a senha do usuário. Requer o login e a senha atual para confirmação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Usuário ou senha inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<String>> trocarSenha(@PathVariable UUID id, @RequestBody @Valid AlterarSenhaRequest request) {
        alterarSenhaUseCase.executar(id, request);
        return ApiSuccessResponse.okMessage("Senha alterada com sucesso");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover usuário", description = "Exclui permanentemente um usuário pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirUsuarioUseCase.executar(id);
        return ApiSuccessResponse.noContent();
    }
}
