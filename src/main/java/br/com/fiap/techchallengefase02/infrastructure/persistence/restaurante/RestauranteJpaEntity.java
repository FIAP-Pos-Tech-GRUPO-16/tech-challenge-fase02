package br.com.fiap.techchallengefase02.infrastructure.persistence.restaurante;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "name", nullable = false)
    private String nome;
    @Embedded
    private EnderecoRestauranteJpaEmbeddable endereco;
    @Column(name = "cuisine_type", nullable = false)
    private String tipoCozinha;
    @Column(name = "opening_hours", nullable = false)
    private String horarioFuncionamento;
    @Column(name = "owner_id", nullable = false)
    private UUID donoId;
}
