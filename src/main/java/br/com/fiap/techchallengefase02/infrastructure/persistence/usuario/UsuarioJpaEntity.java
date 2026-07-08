package br.com.fiap.techchallengefase02.infrastructure.persistence.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(name = "password", nullable = false)
    private String senha;

    @Embedded
    private EnderecoJpaEmbeddable endereco;

    @Column(name = "tipo_usuario_id", nullable = false)
    private UUID tipoUsuarioId;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime dataUltimaAlteracao;

    @PrePersist
    @PreUpdate
    private void marcarComoAlterado() {
        this.dataUltimaAlteracao = LocalDateTime.now();
    }
}
