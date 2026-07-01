package br.com.fiap.techchallengefase02.infrastructure.controller;

import br.com.fiap.techchallengefase02.application.dto.AlterarSenhaRequest;
import br.com.fiap.techchallengefase02.application.dto.AtualizarUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.CriarUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.EnderecoDTO;
import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.application.usecase.autenticacao.AlterarSenhaUseCase;
import br.com.fiap.techchallengefase02.application.usecase.usuario.AtualizarUsuarioUseCase;
import br.com.fiap.techchallengefase02.application.usecase.usuario.BuscarUsuarioPorIdUseCase;
import br.com.fiap.techchallengefase02.application.usecase.usuario.BuscarUsuariosPorNomeUseCase;
import br.com.fiap.techchallengefase02.application.usecase.usuario.CriarUsuarioUseCase;
import br.com.fiap.techchallengefase02.application.usecase.usuario.ExcluirUsuarioUseCase;
import br.com.fiap.techchallengefase02.infrastructure.controller.response.ApiSuccessResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController controller;
    @Mock
    private CriarUsuarioUseCase criarUsuarioUseCase;
    @Mock
    private AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    @Mock
    private BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;
    @Mock
    private BuscarUsuariosPorNomeUseCase buscarUsuariosPorNomeUseCase;
    @Mock
    private ExcluirUsuarioUseCase excluirUsuarioUseCase;
    @Mock
    private AlterarSenhaUseCase alterarSenhaUseCase;

    private static final UUID TIPO_USUARIO_ID = UUID.randomUUID();

    private EnderecoDTO buildEndereco() {
        return new EnderecoDTO("Rua das Flores", "123", "São Paulo", "12345678");
    }

    private CriarUsuarioRequest buildCriarRequest() {
        return new CriarUsuarioRequest("João Silva", "joao@email.com", "joao123", "123456", buildEndereco(), TIPO_USUARIO_ID);
    }

    private UsuarioResponse buildResponse() {
        return new UsuarioResponse(UUID.randomUUID(), "João Silva", "joao@email.com", "joao123",
                TIPO_USUARIO_ID, buildEndereco(), LocalDateTime.now());
    }

    @Nested
    class BuscarPorIdTests {
        @Test
        void deveRetornarUsuarioPorId() {
            UUID id = UUID.randomUUID();
            UsuarioResponse response = buildResponse();
            when(buscarUsuarioPorIdUseCase.executar(id)).thenReturn(response);

            ResponseEntity<ApiSuccessResponse<UsuarioResponse>> result = controller.buscarPorId(id);

            assertThat(result.getStatusCode().value()).isEqualTo(200);
            assertThat(result.getBody().data()).isEqualTo(response);
            verify(buscarUsuarioPorIdUseCase).executar(id);
        }
    }

    @Nested
    class BuscarPorNomeTests {
        @Test
        void deveRetornarResultadosDaBusca() {
            UsuarioResponse response = buildResponse();
            Pagina<UsuarioResponse> pagina = new Pagina<>(List.of(response), 1, 1, 0, 10);
            when(buscarUsuariosPorNomeUseCase.executar("João", 0, 10)).thenReturn(pagina);

            ResponseEntity<ApiSuccessResponse<List<UsuarioResponse>>> result = controller.buscarPorNome("João", 0, 10);

            assertThat(result.getStatusCode().value()).isEqualTo(200);
            assertThat(result.getBody().data()).hasSize(1);
            assertThat(result.getBody().meta().totalElements()).isEqualTo(1);
        }

        @Test
        void deveRetornarListaVaziaQuandoSemCorrespondencia() {
            Pagina<UsuarioResponse> pagina = new Pagina<>(List.of(), 0, 0, 0, 10);
            when(buscarUsuariosPorNomeUseCase.executar("Desconhecido", 0, 10)).thenReturn(pagina);

            ResponseEntity<ApiSuccessResponse<List<UsuarioResponse>>> result = controller.buscarPorNome("Desconhecido", 0, 10);

            assertThat(result.getBody().data()).isEmpty();
            assertThat(result.getBody().meta().totalElements()).isZero();
        }
    }

    @Nested
    class CriarTests {
        @Test
        void deveCriarUsuarioComSucesso() {
            CriarUsuarioRequest request = buildCriarRequest();
            UsuarioResponse response = buildResponse();
            when(criarUsuarioUseCase.executar(request)).thenReturn(response);

            ResponseEntity<ApiSuccessResponse<UsuarioResponse>> result = controller.criar(request);

            assertThat(result.getStatusCode().value()).isEqualTo(201);
            assertThat(result.getBody().message()).isEqualTo("Usuário criado com sucesso");
            assertThat(result.getBody().data()).isEqualTo(response);
        }
    }

    @Nested
    class AtualizarTests {
        @Test
        void deveAtualizarUsuarioComSucesso() {
            UUID id = UUID.randomUUID();
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("Novo Nome", null, null, null);
            UsuarioResponse response = buildResponse();
            when(atualizarUsuarioUseCase.executar(id, request)).thenReturn(response);

            ResponseEntity<ApiSuccessResponse<UsuarioResponse>> result = controller.atualizar(id, request);

            assertThat(result.getStatusCode().value()).isEqualTo(200);
            assertThat(result.getBody().data()).isEqualTo(response);
        }
    }

    @Nested
    class TrocarSenhaTests {
        @Test
        void deveTrocarSenhaComSucesso() {
            UUID id = UUID.randomUUID();
            AlterarSenhaRequest request = new AlterarSenhaRequest("joao123", "senhaAntiga", "senhaNova");
            doNothing().when(alterarSenhaUseCase).executar(id, request);

            ResponseEntity<ApiSuccessResponse<String>> result = controller.trocarSenha(id, request);

            assertThat(result.getStatusCode().value()).isEqualTo(200);
            assertThat(result.getBody().message()).isEqualTo("Senha alterada com sucesso");
            verify(alterarSenhaUseCase).executar(id, request);
        }
    }

    @Nested
    class ExcluirTests {
        @Test
        void deveExcluirUsuarioComSucesso() {
            UUID id = UUID.randomUUID();
            doNothing().when(excluirUsuarioUseCase).executar(id);

            ResponseEntity<Void> result = controller.excluir(id);

            assertThat(result.getStatusCode().value()).isEqualTo(204);
            verify(excluirUsuarioUseCase).executar(id);
        }
    }
}
