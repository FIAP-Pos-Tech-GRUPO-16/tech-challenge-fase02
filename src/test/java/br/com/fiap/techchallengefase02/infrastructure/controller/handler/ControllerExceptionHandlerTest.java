package br.com.fiap.techchallengefase02.infrastructure.controller.handler;

import br.com.fiap.techchallengefase02.infrastructure.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestExceptionController.class)
@Import({ControllerExceptionHandler.class, TestSecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class ControllerExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornar404QuandoNoSuchElementException() throws Exception {
        mockMvc.perform(get("/test/exceptions/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.detail").value("Usuário não encontrado"))
                .andExpect(jsonPath("$.instance").exists());
    }

    @Test
    void deveRetornar400QuandoIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/exceptions/illegal"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisição inválida"))
                .andExpect(jsonPath("$.detail").value("Requisição inválida"));
    }

    @Test
    void deveRetornar409QuandoRecursoJaExistenteException() throws Exception {
        mockMvc.perform(get("/test/exceptions/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Recurso já existe"));
    }

    @Test
    void deveRetornar401QuandoCredenciaisInvalidasException() throws Exception {
        mockMvc.perform(get("/test/exceptions/unauthorized"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Não autorizado"));
    }

    @Test
    void deveRetornar409QuandoDataIntegrityViolationException() throws Exception {
        mockMvc.perform(get("/test/exceptions/data-integrity"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"))
                .andExpect(jsonPath("$.detail").value("Não é possível concluir a operação: o registro possui vínculos com outros dados."))
                .andExpect(jsonPath("$.instance").exists());
    }

    @Test
    void deveRetornar500QuandoExcecaoInesperada() throws Exception {
        mockMvc.perform(get("/test/exceptions/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Erro interno do servidor"));
    }

    @Test
    void deveRetornarErroDeValidacao() throws Exception {
        mockMvc.perform(post("/test/exceptions/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "email": "joao@gmail.com"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'nome')].message").value(hasItem("Nome é obrigatório")))
                .andExpect(jsonPath("$.title").value("Erro de validação"))
                .andExpect(jsonPath("$.errors").isArray());
    }
}
