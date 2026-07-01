package br.com.fiap.techchallengefase02.infrastructure.persistence.tipousuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidade JPA de Tipo de Usuário. Persiste na tabela {@code tipos_usuario}.
 *
 * Mantida enxuta nesta etapa (Membro 1) — apenas o necessário para
 * {@code Usuario} referenciar um tipo válido. O Membro 2 completa o CRUD.
 */
@Entity
@Table(name = "tipos_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;
}
