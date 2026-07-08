package br.com.fiap.techchallengefase02.infrastructure.persistence.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
