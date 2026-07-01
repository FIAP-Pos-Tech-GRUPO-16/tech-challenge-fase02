package br.com.fiap.techchallengefase02.infrastructure.controller;

import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.infrastructure.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integração ponta a ponta do {@link UsuarioController}, subindo
 * o contexto Spring completo com banco H2 e migrations Flyway reais.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerIntegrationTest {

    private static final UUID TIPO_USUARIO_CLIENTE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String tokenValido() {
        Usuario usuarioFicticio = Usuario.builder()
                .id(UUID.randomUUID())
                .login("usuario-de-teste")
                .tipoUsuarioId(TIPO_USUARIO_CLIENTE_ID)
                .build();
        return "Bearer " + jwtService.gerar(usuarioFicticio);
    }

    private String jsonCriarUsuario(String email, String login) {
        return """
                {
                  "nome": "Usuário Integração",
                  "email": "%s",
                  "login": "%s",
                  "senha": "123456",
                  "endereco": {
                    "rua": "Rua das Flores",
                    "numero": "123",
                    "cidade": "São Paulo",
                    "cep": "01310100"
                  },
                  "tipoUsuarioId": "%s"
                }
                """.formatted(email, login, TIPO_USUARIO_CLIENTE_ID);
    }

    private String emailUnico() {
        return "usuario-" + UUID.randomUUID() + "@email.com";
    }

    private String loginUnico() {
        return "login" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    @Nested
    class CriarUsuario {

        @Test
        void deveCriarUsuarioComSucesso() throws Exception {
            String email = emailUnico();
            String login = loginUnico();

            mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarUsuario(email, login)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.email").value(email))
                    .andExpect(jsonPath("$.data.tipoUsuarioId").value(TIPO_USUARIO_CLIENTE_ID.toString()))
                    .andExpect(jsonPath("$.data.id").exists());
        }

        @Test
        void deveRetornar409QuandoEmailJaCadastrado() throws Exception {
            String email = emailUnico();
            mockMvc.perform(post("/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonCriarUsuario(email, loginUnico())));

            mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarUsuario(email, loginUnico())))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Recurso já existe"));
        }

        @Test
        void deveRetornar409QuandoLoginJaCadastrado() throws Exception {
            String login = loginUnico();
            mockMvc.perform(post("/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonCriarUsuario(emailUnico(), login)));

            mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarUsuario(emailUnico(), login)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Recurso já existe"));
        }

        @Test
        void deveRetornar404QuandoTipoUsuarioNaoExiste() throws Exception {
            String corpo = """
                    {
                      "nome": "Usuário Sem Tipo",
                      "email": "%s",
                      "login": "%s",
                      "senha": "123456",
                      "endereco": {
                        "rua": "Rua das Flores",
                        "numero": "123",
                        "cidade": "São Paulo",
                        "cep": "01310100"
                      },
                      "tipoUsuarioId": "%s"
                    }
                    """.formatted(emailUnico(), loginUnico(), UUID.randomUUID());

            mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
        }

        @Test
        void deveRetornar400QuandoDadosInvalidos() throws Exception {
            String corpo = """
                    {
                      "nome": "",
                      "email": "invalido"
                    }
                    """;

            mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpo))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Erro de validação"));
        }
    }

    @Nested
    class BuscarUsuario {

        @Test
        void deveBuscarUsuarioPorIdComTokenValido() throws Exception {
            String email = emailUnico();
            String corpoResposta = mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarUsuario(email, loginUnico())))
                    .andReturn().getResponse().getContentAsString();

            String id = objectMapper.readTree(corpoResposta).get("data").get("id").asText();

            mockMvc.perform(get("/v1/usuarios/{id}", id)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.email").value(email));
        }

        @Test
        void deveRetornar401QuandoSemToken() throws Exception {
            mockMvc.perform(get("/v1/usuarios/{id}", UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void deveRetornar404QuandoUsuarioNaoEncontrado() throws Exception {
            mockMvc.perform(get("/v1/usuarios/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
        }

        @Test
        void deveBuscarUsuariosPorNomePaginado() throws Exception {
            mockMvc.perform(post("/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonCriarUsuario(emailUnico(), loginUnico())));

            mockMvc.perform(get("/v1/usuarios")
                            .param("nome", "Usuário Integração")
                            .param("page", "0")
                            .param("size", "10")
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.meta.totalElements").exists());
        }
    }

    @Nested
    class AtualizarUsuario {

        @Test
        void deveAtualizarNomeComSucesso() throws Exception {
            String corpoResposta = mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarUsuario(emailUnico(), loginUnico())))
                    .andReturn().getResponse().getContentAsString();

            String id = objectMapper.readTree(corpoResposta).get("data").get("id").asText();

            mockMvc.perform(patch("/v1/usuarios/{id}", id)
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nome\": \"Nome Atualizado\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.nome").value("Nome Atualizado"));
        }

        @Test
        void deveRetornar404QuandoUsuarioNaoEncontrado() throws Exception {
            mockMvc.perform(patch("/v1/usuarios/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nome\": \"Nome Atualizado\"}"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class TrocarSenha {

        @Test
        void deveTrocarSenhaComSucesso() throws Exception {
            String login = loginUnico();
            String corpoResposta = mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarUsuario(emailUnico(), login)))
                    .andReturn().getResponse().getContentAsString();

            String id = objectMapper.readTree(corpoResposta).get("data").get("id").asText();

            String corpoSenha = """
                    {
                      "login": "%s",
                      "senhaAtual": "123456",
                      "novaSenha": "novaSenha123"
                    }
                    """.formatted(login);

            mockMvc.perform(put("/v1/usuarios/{id}/senha", id)
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpoSenha))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Senha alterada com sucesso"));
        }

        @Test
        void deveRetornar401QuandoSenhaAtualIncorreta() throws Exception {
            String login = loginUnico();
            String corpoResposta = mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarUsuario(emailUnico(), login)))
                    .andReturn().getResponse().getContentAsString();

            String id = objectMapper.readTree(corpoResposta).get("data").get("id").asText();

            String corpoSenha = """
                    {
                      "login": "%s",
                      "senhaAtual": "senhaErrada",
                      "novaSenha": "novaSenha123"
                    }
                    """.formatted(login);

            mockMvc.perform(put("/v1/usuarios/{id}/senha", id)
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(corpoSenha))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class ExcluirUsuario {

        @Test
        void deveExcluirUsuarioComSucesso() throws Exception {
            String corpoResposta = mockMvc.perform(post("/v1/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarUsuario(emailUnico(), loginUnico())))
                    .andReturn().getResponse().getContentAsString();

            String id = objectMapper.readTree(corpoResposta).get("data").get("id").asText();

            mockMvc.perform(delete("/v1/usuarios/{id}", id)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/v1/usuarios/{id}", id)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deveRetornar404QuandoUsuarioNaoEncontrado() throws Exception {
            mockMvc.perform(delete("/v1/usuarios/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound());
        }
    }
}
