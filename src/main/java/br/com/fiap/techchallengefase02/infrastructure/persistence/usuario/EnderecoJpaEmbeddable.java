package br.com.fiap.techchallengefase02.infrastructure.persistence.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representação JPA do endereço embutido na tabela {@code users}.
 * Os nomes de coluna físicos são mantidos como na Fase 1 (não são
 * identificadores Java, então a regra de nomenclatura em PT-BR não se
 * aplica a eles).
 */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoJpaEmbeddable {

    @Column(name = "address_street")
    private String rua;

    @Column(name = "address_number")
    private String numero;

    @Column(name = "address_city")
    private String cidade;

    @Column(name = "address_zip_code")
    private String cep;
}
