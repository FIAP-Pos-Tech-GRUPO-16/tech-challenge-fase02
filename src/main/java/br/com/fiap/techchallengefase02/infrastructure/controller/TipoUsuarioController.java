package br.com.fiap.techchallengefase02.infrastructure.controller;

import br.com.fiap.techchallengefase02.application.dto.AtualizarTipoUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.CriarTipoUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.application.dto.TipoUsuarioResponse;
import br.com.fiap.techchallengefase02.application.usecase.tipousuario.AtualizarTipoUsuarioUseCase;
import br.com.fiap.techchallengefase02.application.usecase.tipousuario.BuscarTipoUsuarioPorIdUseCase;
import br.com.fiap.techchallengefase02.application.usecase.tipousuario.CriarTipoUsuarioUseCase;
import br.com.fiap.techchallengefase02.application.usecase.tipousuario.ExcluirTipoUsuarioUseCase;
import br.com.fiap.techchallengefase02.application.usecase.tipousuario.ListarTiposUsuarioUseCase;
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
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/tipos-usuario", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Tipos de Usuário", description = "Gerenciamento de tipos de usuário")
@SecurityRequirement(name = "bearerAuth")
public class TipoUsuarioController {

    private final CriarTipoUsuarioUseCase criarTipoUsuarioUseCase;
    private final AtualizarTipoUsuarioUseCase atualizarTipoUsuarioUseCase;
    private final BuscarTipoUsuarioPorIdUseCase buscarTipoUsuarioPorIdUseCase;
    private final ListarTiposUsuarioUseCase listarTiposUsuarioUseCase;
    private final ExcluirTipoUsuarioUseCase excluirTipoUsuarioUseCase;

    public TipoUsuarioController(CriarTipoUsuarioUseCase criarTipoUsuarioUseCase,
                                  AtualizarTipoUsuarioUseCase atualizarTipoUsuarioUseCase,
                                  BuscarTipoUsuarioPorIdUseCase buscarTipoUsuarioPorIdUseCase,
                                  ListarTiposUsuarioUseCase listarTiposUsuarioUseCase,
                                  ExcluirTipoUsuarioUseCase excluirTipoUsuarioUseCase) {
        this.criarTipoUsuarioUseCase = criarTipoUsuarioUseCase;
        this.atualizarTipoUsuarioUseCase = atualizarTipoUsuarioUseCase;
        this.buscarTipoUsuarioPorIdUseCase = buscarTipoUsuarioPorIdUseCase;
        this.listarTiposUsuarioUseCase = listarTiposUsuarioUseCase;
        this.excluirTipoUsuarioUseCase = excluirTipoUsuarioUseCase;
    }

    @PostMapping
    @Operation(summary = "Criar tipo de usuário", description = "Cadastra um novo tipo de usuário (ex: Cliente, Dono de Restaurante)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo de usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Nome já cadastrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<TipoUsuarioResponse>> criar(@RequestBody @Valid CriarTipoUsuarioRequest request) {
        TipoUsuarioResponse response = criarTipoUsuarioUseCase.executar(request);
        return ApiSuccessResponse.created(response, "Tipo de usuário criado com sucesso");
    }

    @GetMapping
    @Operation(summary = "Listar tipos de usuário", description = "Retorna uma lista paginada de tipos de usuário cadastrados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    public ResponseEntity<ApiSuccessResponse<List<TipoUsuarioResponse>>> listar(
            @Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pagina<TipoUsuarioResponse> resultado = listarTiposUsuarioUseCase.executar(page, size);
        return ApiSuccessResponse.ok(resultado.conteudo(), Meta.from(resultado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tipo de usuário por ID", description = "Retorna os dados de um tipo de usuário a partir do seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<TipoUsuarioResponse>> buscarPorId(@PathVariable UUID id) {
        TipoUsuarioResponse response = buscarTipoUsuarioPorIdUseCase.executar(id);
        return ApiSuccessResponse.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tipo de usuário", description = "Atualiza o nome de um tipo de usuário existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Nome já cadastrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<TipoUsuarioResponse>> atualizar(@PathVariable UUID id, @RequestBody @Valid AtualizarTipoUsuarioRequest request) {
        TipoUsuarioResponse response = atualizarTipoUsuarioUseCase.executar(id, request);
        return ApiSuccessResponse.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover tipo de usuário", description = "Exclui permanentemente um tipo de usuário pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo de usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirTipoUsuarioUseCase.executar(id);
        return ApiSuccessResponse.noContent();
    }
}
