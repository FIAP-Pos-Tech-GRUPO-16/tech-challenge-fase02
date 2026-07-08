package br.com.fiap.techchallengefase02.domain.repository;

import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;

import java.util.Optional;
import java.util.UUID;

public interface TipoUsuarioRepository {

    Optional<TipoUsuario> buscarPorId(UUID id);

    boolean existePorId(UUID id);

    boolean existePorNome(String nome);

    TipoUsuario salvar(TipoUsuario tipoUsuario);

    void excluirPorId(UUID id);

    ResultadoPaginado<TipoUsuario> listarPaginado(int pagina, int tamanho);
}
