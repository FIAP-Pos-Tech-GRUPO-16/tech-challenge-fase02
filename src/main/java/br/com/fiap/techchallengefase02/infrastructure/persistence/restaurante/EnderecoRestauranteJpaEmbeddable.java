package br.com.fiap.techchallengefase02.infrastructure.persistence.restaurante;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoRestauranteJpaEmbeddable {

    @Column(name = "address_street", nullable = false)
    private String rua;
    @Column(name = "address_number", nullable = false, length = 20)
    private String numero;
    @Column(name = "address_city", nullable = false)
    private String cidade;
    @Column(name = "address_zip_code", length = 8)
    private String cep;
}
