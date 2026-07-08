package br.com.fiap.techchallengefase02.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public void marcarComoAlterado() {
        this.dataUltimaAlteracao = LocalDateTime.now();
    }

    public void associarTipo(UUID tipoUsuarioId) {
        this.tipoUsuarioId = tipoUsuarioId;
    }
}