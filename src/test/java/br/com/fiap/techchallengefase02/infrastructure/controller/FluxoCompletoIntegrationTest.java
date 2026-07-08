package br.com.fiap.techchallengefase02.infrastructure.controller;

import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.infrastructure.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Fluxo completo ponta a ponta entre todos os domínios")
class FluxoCompletoIntegrationTest {

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

    private String sufixoUnico() {
        return UUID.randomUUID().toString();
    }

    private String extrairId(String corpoResposta) throws Exception {
        return objectMapper.readTree(corpoResposta).get("data").get("id").asText();
    }

    private String jsonTipoUsuario(String nome) {
        return """
                {
                  "nome": "%s"
                }
                """.formatted(nome);
    }

    private String jsonUsuario(String nome, String email, String login, UUID tipoUsuarioId) {
        return """
                {
                  "nome": "%s",
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
                """.formatted(nome, email, login, tipoUsuarioId);
    }

    private String jsonRestaurante(String nome, String donoId) {
        return """
                {
                  "nome": "%s",
                  "endereco": {
                    "rua": "Rua Gourmet",
                    "numero": "45",
                    "cidade": "São Paulo",
                    "cep": "01310100"
                  },
                  "tipoCozinha": "Italiana",
                  "horarioFuncionamento": "Segunda a sexta, das 11h às 22h",
                  "donoId": "%s"
                }
                """.formatted(nome, donoId);
    }

    private String jsonItem(String nome, String preco, boolean apenasNoLocal, String restauranteId) {
        return """
                {
                  "nome": "%s",
                  "descricao": "Massa fresca com molho artesanal",
                  "preco": %s,
                  "disponivelApenasNoLocal": %s,
                  "caminhoFoto": "/fotos/%s.jpg",
                  "restauranteId": "%s"
                }
                """.formatted(nome, preco, apenasNoLocal, nome, restauranteId);
    }

    @Test
    @DisplayName("deve percorrer a jornada completa: tipo -> usuarios -> associacao -> restaurante -> cardapio -> CRUD -> exclusoes")
    void devePercorrerJornadaCompletaEntreTodosOsDominios() throws Exception {
        String sufixo = sufixoUnico();

        // -------------------------------------------------------------------
        // 1. Criar um novo tipo de usuário (ex.: "Gerente")
        // -------------------------------------------------------------------
        String nomeTipo = "Gerente-" + sufixo;
        String respostaTipo = mockMvc.perform(post("/v1/tipos-usuario")
                        .header("Authorization", tokenValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTipoUsuario(nomeTipo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nome").value(nomeTipo))
                .andExpect(jsonPath("$.data.id").exists())
                .andReturn().getResponse().getContentAsString();
        UUID tipoGerenteId = UUID.fromString(extrairId(respostaTipo));

        // -------------------------------------------------------------------
        // 2. Criar o usuário que será o DONO do restaurante.
        //    Inicialmente nasce como Cliente (tipo semeado pela migration).
        // -------------------------------------------------------------------
        String emailDono = "dono-" + sufixo + "@email.com";
        String loginDono = "dono" + sufixo.replace("-", "").substring(0, 10);
        String respostaDono = mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario("Dono da Silva", emailDono, loginDono, TIPO_USUARIO_CLIENTE_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value(emailDono))
                .andExpect(jsonPath("$.data.tipoUsuarioId").value(TIPO_USUARIO_CLIENTE_ID.toString()))
                .andReturn().getResponse().getContentAsString();
        String donoId = extrairId(respostaDono);

        // -------------------------------------------------------------------
        // 3. Criar um segundo usuário: o CLIENTE que consulta o cardápio.
        // -------------------------------------------------------------------
        String emailCliente = "cliente-" + sufixo + "@email.com";
        String loginCliente = "cli" + sufixo.replace("-", "").substring(0, 10);
        String respostaCliente = mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario("Cliente Fiel", emailCliente, loginCliente, TIPO_USUARIO_CLIENTE_ID)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String clienteId = extrairId(respostaCliente);

        // -------------------------------------------------------------------
        // 4. Associar o novo tipo "Gerente" ao usuário dono (regra transversal
        //    entre os domínios Usuário e Tipo de Usuário).
        // -------------------------------------------------------------------
        String corpoAssociacao = """
                {
                  "tipoUsuarioId": "%s"
                }
                """.formatted(tipoGerenteId);
        mockMvc.perform(patch("/v1/usuarios/{id}/tipo-usuario", donoId)
                        .header("Authorization", tokenValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoAssociacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tipoUsuarioId").value(tipoGerenteId.toString()));

        // -------------------------------------------------------------------
        // 5. Criar o restaurante tendo o usuário dono como responsável.
        // -------------------------------------------------------------------
        String nomeRestaurante = "Cantina-" + sufixo;
        String respostaRestaurante = mockMvc.perform(post("/v1/restaurantes")
                        .header("Authorization", tokenValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRestaurante(nomeRestaurante, donoId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nome").value(nomeRestaurante))
                .andExpect(jsonPath("$.data.donoId").value(donoId))
                .andExpect(jsonPath("$.data.id").exists())
                .andReturn().getResponse().getContentAsString();
        String restauranteId = extrairId(respostaRestaurante);

        // -------------------------------------------------------------------
        // 6. Criar itens de cardápio para esse restaurante.
        // -------------------------------------------------------------------
        String nomeItemUm = "Lasanha-" + sufixo;
        String respostaItemUm = mockMvc.perform(post("/v1/itens-cardapio")
                        .header("Authorization", tokenValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem(nomeItemUm, "49.90", false, restauranteId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nome").value(nomeItemUm))
                .andExpect(jsonPath("$.data.restauranteId").value(restauranteId))
                .andReturn().getResponse().getContentAsString();
        String itemUmId = extrairId(respostaItemUm);

        String nomeItemDois = "Tiramisu-" + sufixo;
        String respostaItemDois = mockMvc.perform(post("/v1/itens-cardapio")
                        .header("Authorization", tokenValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem(nomeItemDois, "24.00", true, restauranteId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.disponivelApenasNoLocal").value(true))
                .andReturn().getResponse().getContentAsString();
        String itemDoisId = extrairId(respostaItemDois);

        // -------------------------------------------------------------------
        // 7. CLIENTE consulta: lista paginada de itens do restaurante.
        // -------------------------------------------------------------------
        mockMvc.perform(get("/v1/itens-cardapio")
                        .param("restauranteId", restauranteId)
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", tokenValido()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.totalElements").value(2));

        // Consulta pontual de um item pelo id.
        mockMvc.perform(get("/v1/itens-cardapio/{id}", itemUmId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(itemUmId))
                .andExpect(jsonPath("$.data.nome").value(nomeItemUm));

        // -------------------------------------------------------------------
        // 8. DONO atualiza um item do cardápio (preço e nome).
        // -------------------------------------------------------------------
        String nomeItemAtualizado = "Ravioli-" + sufixo;
        mockMvc.perform(put("/v1/itens-cardapio/{id}", itemUmId)
                        .header("Authorization", tokenValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem(nomeItemAtualizado, "59.90", false, restauranteId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value(nomeItemAtualizado))
                .andExpect(jsonPath("$.data.preco").value(59.90));

        // -------------------------------------------------------------------
        // 9. DONO atualiza dados do restaurante (nome e tipo de cozinha).
        // -------------------------------------------------------------------
        String nomeRestauranteAtualizado = "Trattoria-" + sufixo;
        String corpoRestauranteAtualizado = """
                {
                  "nome": "%s",
                  "endereco": {
                    "rua": "Rua Nova",
                    "numero": "99",
                    "cidade": "São Paulo",
                    "cep": "04567000"
                  },
                  "tipoCozinha": "Mediterrânea",
                  "horarioFuncionamento": "Todos os dias, das 12h às 23h",
                  "donoId": "%s"
                }
                """.formatted(nomeRestauranteAtualizado, donoId);
        mockMvc.perform(put("/v1/restaurantes/{id}", restauranteId)
                        .header("Authorization", tokenValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoRestauranteAtualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value(nomeRestauranteAtualizado))
                .andExpect(jsonPath("$.data.tipoCozinha").value("Mediterrânea"));

        // Consulta pontual do restaurante para confirmar a persistência.
        mockMvc.perform(get("/v1/restaurantes/{id}", restauranteId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(restauranteId))
                .andExpect(jsonPath("$.data.nome").value(nomeRestauranteAtualizado));

        // -------------------------------------------------------------------
        // 10. Regra transversal: não é possível excluir o usuário que é dono
        //     de um restaurante ativo (Usuário x Restaurante).
        // -------------------------------------------------------------------
        mockMvc.perform(delete("/v1/usuarios/{id}", donoId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"));

        // -------------------------------------------------------------------
        // 11. Encerramento do ciclo de vida na ordem correta de dependências:
        //     item -> restaurante -> usuários -> tipo de usuário.
        // -------------------------------------------------------------------
        mockMvc.perform(delete("/v1/itens-cardapio/{id}", itemUmId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/v1/itens-cardapio/{id}", itemUmId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/v1/itens-cardapio/{id}", itemDoisId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/v1/restaurantes/{id}", restauranteId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/v1/restaurantes/{id}", restauranteId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isNotFound());

        // Sem restaurante vinculado, agora o dono pode ser excluído.
        mockMvc.perform(delete("/v1/usuarios/{id}", donoId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/v1/usuarios/{id}", clienteId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isNoContent());

        // Por fim, remove o tipo de usuário criado no início da jornada.
        mockMvc.perform(delete("/v1/tipos-usuario/{id}", tipoGerenteId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/v1/tipos-usuario/{id}", tipoGerenteId)
                        .header("Authorization", tokenValido()))
                .andExpect(status().isNotFound());
    }
}