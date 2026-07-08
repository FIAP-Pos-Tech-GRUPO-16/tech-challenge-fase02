package br.com.fiap.techchallengefase02.infrastructure.controller.response;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ApiSuccessResponseTest {

    private final Meta meta = new Meta(10L, 2, 0, 5, true, false);

    @Test
    void deveRetornarOkComDataMensagemEMetaQuandoInformadosOsTres() {
        ResponseEntity<ApiSuccessResponse<String>> resposta = ApiSuccessResponse.ok("conteudo", "mensagem", meta);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().data()).isEqualTo("conteudo");
        assertThat(resposta.getBody().message()).isEqualTo("mensagem");
        assertThat(resposta.getBody().meta()).isEqualTo(meta);
        assertThat(resposta.getBody().timestamp()).isNotNull();
    }

    @Test
    void deveRetornarCreatedApenasComDataQuandoNenhumOutroParametroForInformado() {
        ResponseEntity<ApiSuccessResponse<String>> resposta = ApiSuccessResponse.created("conteudo");

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().data()).isEqualTo("conteudo");
        assertThat(resposta.getBody().message()).isNull();
        assertThat(resposta.getBody().meta()).isNull();
    }

    @Test
    void deveRetornarCreatedComDataMensagemEMetaQuandoInformadosOsTres() {
        ResponseEntity<ApiSuccessResponse<String>> resposta = ApiSuccessResponse.created("conteudo", "mensagem", meta);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().data()).isEqualTo("conteudo");
        assertThat(resposta.getBody().message()).isEqualTo("mensagem");
        assertThat(resposta.getBody().meta()).isEqualTo(meta);
    }
}
