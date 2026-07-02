package br.com.fiap.techchallengefase02.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio que representa um usuário do sistema.
 *
 * A associação com {@link TipoUsuario} é feita por referência simples de ID
 * ({@code tipoUsuarioId}), sem navegação de objeto entre agregados — mesmo
 * padrão que será usado depois entre Restaurante e o usuário-dono.
 *
 * Classe livre de qualquer dependência de framework (sem anotações de
 * persistência ou de Spring): a ligação com a entidade JPA correspondente é
 * feita por um mapper na camada de infraestrutura.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private UUID id;
    private String nome;
    private String email;
    private String login;
    private String senha;
    private Endereco endereco;
    private UUID tipoUsuarioId;
    private LocalDateTime dataUltimaAlteracao;

    /**
     * Marca o usuário como alterado agora. Deve ser chamado pelos casos de
     * uso sempre que os dados do usuário forem criados ou modificados.
     */
    public void marcarComoAlterado() {
        this.dataUltimaAlteracao = LocalDateTime.now();
    }

    /**
     * Associa este usuário a um tipo de usuário existente (ex: Cliente,
     * Dono de Restaurante). Método específico em vez do setter genérico,
     * para deixar explícita a intenção de negócio de quem chama.
     */
    public void associarTipo(UUID tipoUsuarioId) {
        this.tipoUsuarioId = tipoUsuarioId;
    }
}