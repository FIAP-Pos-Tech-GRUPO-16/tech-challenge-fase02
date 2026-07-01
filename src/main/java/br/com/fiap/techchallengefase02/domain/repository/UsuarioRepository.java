package br.com.fiap.techchallengefase02.domain.repository;

import br.com.fiap.techchallengefase02.domain.entity.Usuario;

import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de persistência de {@link Usuario}. A implementação concreta
 * (JPA) vive em {@code infrastructure.persistence.usuario}.
 */
public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(UUID id);

    Optional<Usuario> buscarPorLogin(String login);

    Optional<Usuario> buscarPorEmail(String email);

    ResultadoPaginado<Usuario> buscarPorNome(String nome, int pagina, int tamanho);

    boolean existePorEmail(String email);

    boolean existePorLogin(String login);

    boolean existePorId(UUID id);

    void excluirPorId(UUID id);
}
