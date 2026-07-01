package br.com.fiap.techchallengefase02.infrastructure.controller;

import br.com.fiap.techchallengefase02.application.dto.LoginRequest;
import br.com.fiap.techchallengefase02.application.dto.TokenResponse;
import br.com.fiap.techchallengefase02.application.usecase.autenticacao.AutenticarUsuarioUseCase;
import br.com.fiap.techchallengefase02.infrastructure.controller.response.ApiSuccessResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacaoControllerTest {

    @InjectMocks
    private AutenticacaoController controller;
    @Mock
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @Nested
    class LoginTests {
        @Test
        void deveAutenticarComSucesso() {
            LoginRequest request = new LoginRequest("user", "senha");
            TokenResponse token = new TokenResponse("fake-jwt-token");

            when(autenticarUsuarioUseCase.executar(request)).thenReturn(token);

            ResponseEntity<ApiSuccessResponse<TokenResponse>> response = controller.login(request);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode().value()).isEqualTo(200);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().token()).isEqualTo("fake-jwt-token");
            assertThat(response.getBody().data().tipo()).isEqualTo("Bearer");
            assertThat(response.getBody().message()).isEqualTo("Usuário autenticado com sucesso");

            verify(autenticarUsuarioUseCase, times(1)).executar(request);
        }
    }
}
