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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TipoUsuarioControllerIntegrationTest {

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

    private String jsonCriarTipoUsuario(String nome) {
        return """
                {
                  "nome": "%s"
                }
                """.formatted(nome);
    }

    private String nomeUnico() {
        return "Tipo-" + UUID.randomUUID();
    }

    @Nested
    class CriarTipoUsuario {

        @Test
        void deveCriarTipoUsuarioComSucesso() throws Exception {
            String nome = nomeUnico();

            mockMvc.perform(post("/v1/tipos-usuario")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario(nome)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.nome").value(nome))
                    .andExpect(jsonPath("$.data.id").exists());
        }

        @Test
        void deveRetornar409QuandoNomeJaCadastrado() throws Exception {
            String nome = nomeUnico();

            mockMvc.perform(post("/v1/tipos-usuario")
                    .header("Authorization", tokenValido())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonCriarTipoUsuario(nome)));

            mockMvc.perform(post("/v1/tipos-usuario")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario(nome)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Recurso já existe"));
        }

        @Test
        void deveRetornar400QuandoNomeInvalido() throws Exception {
            mockMvc.perform(post("/v1/tipos-usuario")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario("")))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Erro de validação"));
        }

        @Test
        void deveRetornar401QuandoSemToken() throws Exception {
            mockMvc.perform(post("/v1/tipos-usuario")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario(nomeUnico())))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class ListarTiposUsuario {

        @Test
        void deveListarTiposUsuarioPaginado() throws Exception {
            mockMvc.perform(get("/v1/tipos-usuario")
                            .param("page", "0")
                            .param("size", "10")
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.meta.totalElements").exists());
        }
    }

    @Nested
    class BuscarTipoUsuario {

        @Test
        void deveBuscarTipoUsuarioPorId() throws Exception {
            mockMvc.perform(get("/v1/tipos-usuario/{id}", TIPO_USUARIO_CLIENTE_ID)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.nome").value("Cliente"));
        }

        @Test
        void deveRetornar404QuandoTipoUsuarioNaoEncontrado() throws Exception {
            mockMvc.perform(get("/v1/tipos-usuario/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
        }
    }

    @Nested
    class AtualizarTipoUsuario {

        @Test
        void deveAtualizarNomeComSucesso() throws Exception {
            String corpoResposta = mockMvc.perform(post("/v1/tipos-usuario")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario(nomeUnico())))
                    .andReturn().getResponse().getContentAsString();

            String id = objectMapper.readTree(corpoResposta).get("data").get("id").asText();
            String novoNome = nomeUnico();

            mockMvc.perform(put("/v1/tipos-usuario/{id}", id)
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario(novoNome)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.nome").value(novoNome));
        }

        @Test
        void deveRetornar404QuandoTipoUsuarioNaoEncontrado() throws Exception {
            mockMvc.perform(put("/v1/tipos-usuario/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario(nomeUnico())))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deveRetornar409QuandoNovoNomeJaCadastrado() throws Exception {
            String corpoResposta = mockMvc.perform(post("/v1/tipos-usuario")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario(nomeUnico())))
                    .andReturn().getResponse().getContentAsString();

            String id = objectMapper.readTree(corpoResposta).get("data").get("id").asText();

            mockMvc.perform(put("/v1/tipos-usuario/{id}", id)
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario("Cliente")))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Recurso já existe"));
        }

        @Test
        void deveRetornar400QuandoNomeInvalido() throws Exception {
            String corpoResposta = mockMvc.perform(post("/v1/tipos-usuario")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario(nomeUnico())))
                    .andReturn().getResponse().getContentAsString();

            String id = objectMapper.readTree(corpoResposta).get("data").get("id").asText();

            mockMvc.perform(put("/v1/tipos-usuario/{id}", id)
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario("")))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ExcluirTipoUsuario {

        @Test
        void deveExcluirTipoUsuarioComSucesso() throws Exception {
            String corpoResposta = mockMvc.perform(post("/v1/tipos-usuario")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonCriarTipoUsuario(nomeUnico())))
                    .andReturn().getResponse().getContentAsString();

            String id = objectMapper.readTree(corpoResposta).get("data").get("id").asText();

            mockMvc.perform(delete("/v1/tipos-usuario/{id}", id)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/v1/tipos-usuario/{id}", id)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deveRetornar404QuandoTipoUsuarioNaoEncontrado() throws Exception {
            mockMvc.perform(delete("/v1/tipos-usuario/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound());
        }
    }
}
