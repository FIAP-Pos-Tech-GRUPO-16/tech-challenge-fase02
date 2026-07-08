package br.com.fiap.techchallengefase02.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutenticacaoControllerIntegrationTest {

    private static final UUID TIPO_USUARIO_CLIENTE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String emailUnico() {
        return "login-integracao-" + UUID.randomUUID() + "@email.com";
    }

    private String loginUnico() {
        return "loginauth" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private void criarUsuario(String email, String login, String senha) throws Exception {
        String corpo = """
                {
                  "nome": "Usuário Autenticação",
                  "email": "%s",
                  "login": "%s",
                  "senha": "%s",
                  "endereco": {
                    "rua": "Rua das Flores",
                    "numero": "123",
                    "cidade": "São Paulo",
                    "cep": "01310100"
                  },
                  "tipoUsuarioId": "%s"
                }
                """.formatted(email, login, senha, TIPO_USUARIO_CLIENTE_ID);

        mockMvc.perform(post("/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo));
    }

    @Test
    void deveAutenticarComSucesso() throws Exception {
        String login = loginUnico();
        criarUsuario(emailUnico(), login, "123456");

        String corpoLogin = """
                {
                  "login": "%s",
                  "senha": "123456"
                }
                """.formatted(login);

        mockMvc.perform(post("/v1/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoLogin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.tipo").value("Bearer"));
    }

    @Test
    void deveRetornar401QuandoSenhaIncorreta() throws Exception {
        String login = loginUnico();
        criarUsuario(emailUnico(), login, "123456");

        String corpoLogin = """
                {
                  "login": "%s",
                  "senha": "senhaErrada"
                }
                """.formatted(login);

        mockMvc.perform(post("/v1/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoLogin))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoUsuarioNaoEncontrado() throws Exception {
        String corpoLogin = """
                {
                  "login": "usuario-inexistente",
                  "senha": "123456"
                }
                """;

        mockMvc.perform(post("/v1/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoLogin))
                .andExpect(status().isUnauthorized());
    }
}
