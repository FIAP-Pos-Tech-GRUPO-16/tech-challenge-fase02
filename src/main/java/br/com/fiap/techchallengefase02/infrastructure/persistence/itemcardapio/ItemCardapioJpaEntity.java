package br.com.fiap.techchallengefase02.infrastructure.persistence.itemcardapio;

import jakarta.persistence.Column;
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

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCardapioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "name", nullable = false)
    private String nome;
    @Column(name = "description", nullable = false, length = 1000)
    private String descricao;
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;
    @Column(name = "available_only_on_site", nullable = false)
    private boolean disponivelApenasNoLocal;
    @Column(name = "photo_path", length = 500)
    private String caminhoFoto;
    @Column(name = "restaurant_id", nullable = false)
    private UUID restauranteId;
}
