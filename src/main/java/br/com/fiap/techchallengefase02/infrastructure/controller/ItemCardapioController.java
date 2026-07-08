package br.com.fiap.techchallengefase02.infrastructure.controller;

import br.com.fiap.techchallengefase02.application.dto.AtualizarItemCardapioRequest;
import br.com.fiap.techchallengefase02.application.dto.CriarItemCardapioRequest;
import br.com.fiap.techchallengefase02.application.dto.ItemCardapioResponse;
import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.application.usecase.itemcardapio.AtualizarItemCardapioUseCase;
import br.com.fiap.techchallengefase02.application.usecase.itemcardapio.BuscarItemCardapioPorIdUseCase;
import br.com.fiap.techchallengefase02.application.usecase.itemcardapio.CriarItemCardapioUseCase;
import br.com.fiap.techchallengefase02.application.usecase.itemcardapio.ExcluirItemCardapioUseCase;
import br.com.fiap.techchallengefase02.application.usecase.itemcardapio.ListarItensCardapioUseCase;
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
@RequestMapping(value = "/v1/itens-cardapio", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Itens de Cardápio", description = "Gerenciamento de itens vendidos no cardápio")
@SecurityRequirement(name = "bearerAuth")
public class ItemCardapioController {

    private final CriarItemCardapioUseCase criarItemCardapioUseCase;
    private final AtualizarItemCardapioUseCase atualizarItemCardapioUseCase;
    private final BuscarItemCardapioPorIdUseCase buscarItemCardapioPorIdUseCase;
    private final ListarItensCardapioUseCase listarItensCardapioUseCase;
    private final ExcluirItemCardapioUseCase excluirItemCardapioUseCase;

    public ItemCardapioController(CriarItemCardapioUseCase criarItemCardapioUseCase,
                                  AtualizarItemCardapioUseCase atualizarItemCardapioUseCase,
                                  BuscarItemCardapioPorIdUseCase buscarItemCardapioPorIdUseCase,
                                  ListarItensCardapioUseCase listarItensCardapioUseCase,
                                  ExcluirItemCardapioUseCase excluirItemCardapioUseCase) {
        this.criarItemCardapioUseCase = criarItemCardapioUseCase;
        this.atualizarItemCardapioUseCase = atualizarItemCardapioUseCase;
        this.buscarItemCardapioPorIdUseCase = buscarItemCardapioPorIdUseCase;
        this.listarItensCardapioUseCase = listarItensCardapioUseCase;
        this.excluirItemCardapioUseCase = excluirItemCardapioUseCase;
    }

    @PostMapping
    @Operation(summary = "Criar item de cardápio", description = "Cadastra um novo item de cardápio associado a um restaurante existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item de cardápio criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<ItemCardapioResponse>> criar(@RequestBody @Valid CriarItemCardapioRequest request) {
        ItemCardapioResponse response = criarItemCardapioUseCase.executar(request);
        return ApiSuccessResponse.created(response, "Item de cardápio criado com sucesso");
    }

    @GetMapping
    @Operation(summary = "Listar itens de cardápio", description = "Retorna itens de cardápio filtrados por restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "restauranteId ausente",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<List<ItemCardapioResponse>>> listar(
            @Parameter(description = "ID do restaurante")
            @RequestParam(required = false) UUID restauranteId,
            @Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pagina<ItemCardapioResponse> resultado = listarItensCardapioUseCase.executar(restauranteId, page, size);
        return ApiSuccessResponse.ok(resultado.conteudo(), Meta.from(resultado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item de cardápio por ID", description = "Retorna os dados de um item de cardápio a partir do seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item de cardápio encontrado"),
            @ApiResponse(responseCode = "404", description = "Item de cardápio não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<ItemCardapioResponse>> buscarPorId(@PathVariable UUID id) {
        ItemCardapioResponse response = buscarItemCardapioPorIdUseCase.executar(id);
        return ApiSuccessResponse.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item de cardápio", description = "Atualiza os dados de um item existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item de cardápio atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Item de cardápio ou restaurante não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ApiSuccessResponse<ItemCardapioResponse>> atualizar(@PathVariable UUID id,
                                                                              @RequestBody @Valid AtualizarItemCardapioRequest request) {
        ItemCardapioResponse response = atualizarItemCardapioUseCase.executar(id, request);
        return ApiSuccessResponse.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover item de cardápio", description = "Exclui permanentemente um item de cardápio pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item de cardápio removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item de cardápio nao encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirItemCardapioUseCase.executar(id);
        return ApiSuccessResponse.noContent();
    }
}
