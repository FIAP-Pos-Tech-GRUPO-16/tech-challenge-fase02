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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemCardapioControllerIntegrationTest {

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

    private String jsonRestaurante(String nome) {
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
                """.formatted(nome, DONO_EXISTENTE_ID);
    }

    private String jsonItem(String nome, String preco, UUID restauranteId) {
        return """
                {
                  "nome": "%s",
                  "descricao": "Massa fresca com molho bolonhesa",
                  "preco": %s,
                  "disponivelApenasNoLocal": false,
                  "caminhoFoto": "/fotos/lasanha.jpg",
                  "restauranteId": "%s"
                }
                """.formatted(nome, preco, restauranteId);
    }

    private String nomeUnico(String prefixo) {
        return prefixo + "-" + UUID.randomUUID();
    }

    private UUID criarRestaurante() throws Exception {
        String corpoResposta = mockMvc.perform(post("/v1/restaurantes")
                        .header("Authorization", tokenValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRestaurante(nomeUnico("Restaurante"))))
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(corpoResposta).get("data").get("id").asText());
    }

    private String criarItem(UUID restauranteId) throws Exception {
        String corpoResposta = mockMvc.perform(post("/v1/itens-cardapio")
                        .header("Authorization", tokenValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem(nomeUnico("Lasanha"), "49.90", restauranteId)))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpoResposta).get("data").get("id").asText();
    }

    @Nested
    class CriarItem {

        @Test
        void deveCriarItemComSucesso() throws Exception {
            UUID restauranteId = criarRestaurante();
            String nome = nomeUnico("Lasanha");

            mockMvc.perform(post("/v1/itens-cardapio")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem(nome, "49.90", restauranteId)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.nome").value(nome))
                    .andExpect(jsonPath("$.data.restauranteId").value(restauranteId.toString()))
                    .andExpect(jsonPath("$.data.id").exists());
        }

        @Test
        void deveRetornar404QuandoRestauranteNaoExistir() throws Exception {
            mockMvc.perform(post("/v1/itens-cardapio")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem(nomeUnico("Lasanha"), "49.90", UUID.randomUUID())))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deveRetornar400QuandoPrecoInvalido() throws Exception {
            UUID restauranteId = criarRestaurante();

            mockMvc.perform(post("/v1/itens-cardapio")
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem(nomeUnico("Lasanha"), "0", restauranteId)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void deveRetornar401QuandoSemToken() throws Exception {
            UUID restauranteId = criarRestaurante();

            mockMvc.perform(post("/v1/itens-cardapio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem(nomeUnico("Lasanha"), "49.90", restauranteId)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class ListarItens {

        @Test
        void deveListarItensPorRestaurante() throws Exception {
            UUID restauranteId = criarRestaurante();
            criarItem(restauranteId);

            mockMvc.perform(get("/v1/itens-cardapio")
                            .param("restauranteId", restauranteId.toString())
                            .param("page", "0")
                            .param("size", "10")
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.meta.totalElements").exists());
        }

        @Test
        void deveRetornar400QuandoRestauranteIdAusente() throws Exception {
            mockMvc.perform(get("/v1/itens-cardapio")
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class BuscarItem {

        @Test
        void deveBuscarItemPorId() throws Exception {
            UUID restauranteId = criarRestaurante();
            String id = criarItem(restauranteId);

            mockMvc.perform(get("/v1/itens-cardapio/{id}", id)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(id));
        }

        @Test
        void deveRetornar404QuandoItemNaoEncontrado() throws Exception {
            mockMvc.perform(get("/v1/itens-cardapio/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class AtualizarItem {

        @Test
        void deveAtualizarItemComSucesso() throws Exception {
            UUID restauranteId = criarRestaurante();
            String id = criarItem(restauranteId);
            String novoNome = nomeUnico("Ravioli");

            mockMvc.perform(put("/v1/itens-cardapio/{id}", id)
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem(novoNome, "59.90", restauranteId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.nome").value(novoNome));
        }

        @Test
        void deveRetornar404QuandoItemNaoEncontrado() throws Exception {
            UUID restauranteId = criarRestaurante();

            mockMvc.perform(put("/v1/itens-cardapio/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem(nomeUnico("Ravioli"), "59.90", restauranteId)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deveRetornar404QuandoRestauranteNaoEncontrado() throws Exception {
            UUID restauranteId = criarRestaurante();
            String id = criarItem(restauranteId);

            mockMvc.perform(put("/v1/itens-cardapio/{id}", id)
                            .header("Authorization", tokenValido())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem(nomeUnico("Ravioli"), "59.90", UUID.randomUUID())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class ExcluirItem {

        @Test
        void deveExcluirItemComSucesso() throws Exception {
            UUID restauranteId = criarRestaurante();
            String id = criarItem(restauranteId);

            mockMvc.perform(delete("/v1/itens-cardapio/{id}", id)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/v1/itens-cardapio/{id}", id)
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deveRetornar404QuandoItemNaoEncontrado() throws Exception {
            mockMvc.perform(delete("/v1/itens-cardapio/{id}", UUID.randomUUID())
                            .header("Authorization", tokenValido()))
                    .andExpect(status().isNotFound());
        }
    }
}
