package br.com.fiap.techchallengefase02.domain.repository;

import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;

import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de persistência de {@link TipoUsuario}.
 *
 * Nesta etapa (Membro 1) só existem as operações necessárias para que a
 * criação de {@code Usuario} consiga validar um {@code tipoUsuarioId}
 * informado. O CRUD completo de Tipo de Usuário é escopo do Membro 2, que
 * deve estender esta interface conforme necessário (ex: salvar, listar,
 * excluir).
 */
public interface TipoUsuarioRepository {

    Optional<TipoUsuario> buscarPorId(UUID id);

    boolean existePorId(UUID id);
}
