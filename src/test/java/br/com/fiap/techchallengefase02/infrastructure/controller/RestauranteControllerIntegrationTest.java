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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integracao ponta a ponta do {@link RestauranteController}, subindo
 * o contexto Spring completo com banco H2 e migrations Flyway reais.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RestauranteControllerIntegrationTest {

    private static final UUID TIPO_USUARIO_CLIENTE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID DONO_EXISTENTE_ID = UUID.fromString("c3e7f5a2-2d4e-5b3c-0a1e-3456789012cd");
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

    private String jsonRestaurante(String nome, UUID donoId) {
        return """
                {
                  "nome": "%s",
                  "endereco": {
                    "rua": "Rua Gourmet",
                    "numero": "45",
                    "cidade": "Sao Paulo",
                    "cep": "01310100"
                  },
                  "tipoCozinha": "Italiana",
                  "horarioFuncionamento": "Segunda a sexta, das 11h as 22h",
                  "donoId": "%s"
                }
                """.formatted(nome, donoId);
    }

    private String nomeUnico() {
        return "Restaurante-" + UUID.randomUUID();
    }
    private String criarRestaurante() throws Exception {
        String corpoResposta = mockMvc.perform(post("/v1/restaurantes")
                        .header("Authorization", tokenValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRestaurante(nomeUnico(), DONO_EXISTENTE_ID)))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpoResposta).get("data").get("id").asText();
    }
    @Nested
    class CriarRestaurante {

        @Test
        void deveCriarRestauranteComSucesso() throws Exception {
            String nome = nomeUnico();
            mockMvc.perform(post("/v1/restaurantes")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRestaurante(nome, DONO_EXISTENTE_ID)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.nome").value(nome))
                    .andExpect(jsonPath("$.data.donoId").value(DONO_EXISTENTE_ID.toString()))
                    .andExpect(jsonPath("$.data.id").exists());
        }
        @Test
        void deveRetornar404QuandoDonoNaoExistir() throws Exception {
            mockMvc.perform(post("/v1/restaurantes")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRestaurante(nomeUnico(), UUID.randomUUID())))
                    .andExpect(status().isNotFound());
        }
        @Test
        void deveRetornar400QuandoNomeInvalido() throws Exception {
            mockMvc.perform(post("/v1/restaurantes")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRestaurante("", DONO_EXISTENTE_ID)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        void deveRetornar401QuandoSemToken() throws Exception {
            mockMvc.perform(post("/v1/restaurantes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRestaurante(nomeUnico(), DONO_EXISTENTE_ID)))
                    .andExpect(status().isUnauthorized());
        }
    }
    @Nested
    class ListarRestaurantes {

        @Test
        void deveListarRestaurantesPaginado() throws Exception {
            mockMvc.perform(get("/v1/restaurantes")
                            .param("page", "0")
                            .param("size", "10")
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.meta.totalElements").exists());
        }
    }
    @Nested
    class BuscarRestaurante {

        @Test
        void deveBuscarRestaurantePorId() throws Exception {
            String id = criarRestaurante();
            mockMvc.perform(get("/v1/restaurantes/{id}", id)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(id));
        }
        @Test
        void deveRetornar404QuandoRestauranteNaoEncontrado() throws Exception {
            mockMvc.perform(get("/v1/restaurantes/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound());
        }
    }
    @Nested
    class AtualizarRestaurante {

        @Test
        void deveAtualizarRestauranteComSucesso() throws Exception {
            String id = criarRestaurante();
            String novoNome = nomeUnico();
            mockMvc.perform(put("/v1/restaurantes/{id}", id)
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRestaurante(novoNome, DONO_EXISTENTE_ID)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.nome").value(novoNome));
        }
        @Test
        void deveRetornar404QuandoRestauranteNaoEncontrado() throws Exception {
            mockMvc.perform(put("/v1/restaurantes/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRestaurante(nomeUnico(), DONO_EXISTENTE_ID)))
                    .andExpect(status().isNotFound());
        }
        @Test
        void deveRetornar404QuandoDonoNaoEncontrado() throws Exception {
            String id = criarRestaurante();
            mockMvc.perform(put("/v1/restaurantes/{id}", id)
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRestaurante(nomeUnico(), UUID.randomUUID())))
                    .andExpect(status().isNotFound());
        }
        @Test
        void deveRetornar400QuandoDadosInvalidos() throws Exception {
            String id = criarRestaurante();
            mockMvc.perform(put("/v1/restaurantes/{id}", id)
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRestaurante("", DONO_EXISTENTE_ID)))
                    .andExpect(status().isBadRequest());
        }
    }
    @Nested
    class ExcluirRestaurante {

        @Test
        void deveExcluirRestauranteComSucesso() throws Exception {
            String id = criarRestaurante();
            mockMvc.perform(delete("/v1/restaurantes/{id}", id)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNoContent());
            mockMvc.perform(get("/v1/restaurantes/{id}", id)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound());
        }
        @Test
        void deveRetornar404QuandoRestauranteNaoEncontrado() throws Exception {
            mockMvc.perform(delete("/v1/restaurantes/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound());
        }
    }
}
