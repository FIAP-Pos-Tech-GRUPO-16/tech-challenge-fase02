package br.com.fiap.techchallengefase02.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCardapio {

    private UUID id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private boolean disponivelApenasNoLocal;
    private String caminhoFoto;
    private UUID restauranteId;
}
