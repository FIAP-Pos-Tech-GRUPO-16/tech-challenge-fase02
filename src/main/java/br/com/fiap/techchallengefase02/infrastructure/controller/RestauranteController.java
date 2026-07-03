package br.com.fiap.techchallengefase02.infrastructure.controller;

import br.com.fiap.techchallengefase02.application.dto.AtualizarRestauranteRequest;
import br.com.fiap.techchallengefase02.application.dto.CriarRestauranteRequest;
import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.application.dto.RestauranteResponse;
import br.com.fiap.techchallengefase02.application.usecase.restaurante.AtualizarRestauranteUseCase;
import br.com.fiap.techchallengefase02.application.usecase.restaurante.BuscarRestaurantePorIdUseCase;
import br.com.fiap.techchallengefase02.application.usecase.restaurante.CriarRestauranteUseCase;
import br.com.fiap.techchallengefase02.application.usecase.restaurante.ExcluirRestauranteUseCase;
import br.com.fiap.techchallengefase02.application.usecase.restaurante.ListarRestaurantesUseCase;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/restaurantes")
@Tag(name = "Restaurantes", description = "Gerenciamento de restaurantes")
@SecurityRequirement(name = "bearerAuth")
public class RestauranteController {

    private final CriarRestauranteUseCase criarRestauranteUseCase;
    private final AtualizarRestauranteUseCase atualizarRestauranteUseCase;
    private final BuscarRestaurantePorIdUseCase buscarRestaurantePorIdUseCase;
    private final ListarRestaurantesUseCase listarRestaurantesUseCase;
    private final ExcluirRestauranteUseCase excluirRestauranteUseCase;

    public RestauranteController(CriarRestauranteUseCase criarRestauranteUseCase,
                                 AtualizarRestauranteUseCase atualizarRestauranteUseCase,
                                 BuscarRestaurantePorIdUseCase buscarRestaurantePorIdUseCase,
                                 ListarRestaurantesUseCase listarRestaurantesUseCase,
                                 ExcluirRestauranteUseCase excluirRestauranteUseCase) {
        this.criarRestauranteUseCase = criarRestauranteUseCase;
        this.atualizarRestauranteUseCase = atualizarRestauranteUseCase;
        this.buscarRestaurantePorIdUseCase = buscarRestaurantePorIdUseCase;
        this.listarRestaurantesUseCase = listarRestaurantesUseCase;
        this.excluirRestauranteUseCase = excluirRestauranteUseCase;
    }

    @PostMapping
    @Operation(summary = "Criar restaurante", description = "Cadastra um novo restaurante associado a um usuário dono existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<RestauranteResponse>> criar(@RequestBody @Valid CriarRestauranteRequest request) {
        RestauranteResponse response = criarRestauranteUseCase.executar(request);
        return ApiSuccessResponse.created(response, "Restaurante criado com sucesso");
    }

    @GetMapping
    @Operation(summary = "Listar restaurantes", description = "Retorna uma lista paginada de restaurantes cadastrados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    public ResponseEntity<ApiSuccessResponse<List<RestauranteResponse>>> listar(
            @Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pagina<RestauranteResponse> resultado = listarRestaurantesUseCase.executar(page, size);
        return ApiSuccessResponse.ok(resultado.conteudo(), Meta.from(resultado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar restaurante por ID", description = "Retorna os dados de um restaurante a partir do seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<RestauranteResponse>> buscarPorId(@PathVariable UUID id) {
        RestauranteResponse response = buscarRestaurantePorIdUseCase.executar(id);
        return ApiSuccessResponse.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar restaurante", description = "Atualiza os dados de um restaurante existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Restaurante ou dono não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<RestauranteResponse>> atualizar(@PathVariable UUID id,
                                                                             @RequestBody @Valid AtualizarRestauranteRequest request) {
        RestauranteResponse response = atualizarRestauranteUseCase.executar(id, request);
        return ApiSuccessResponse.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover restaurante", description = "Exclui permanentemente um restaurante pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Restaurante removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirRestauranteUseCase.executar(id);
        return ApiSuccessResponse.noContent();
    }
}
