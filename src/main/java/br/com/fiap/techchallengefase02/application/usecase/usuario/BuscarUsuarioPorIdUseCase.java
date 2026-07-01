package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Caso de uso: buscar um usuário pelo seu ID.
 */
@Component
public class BuscarUsuarioPorIdUseCase {

    private final UsuarioRepository usuarioRepository;

    public BuscarUsuarioPorIdUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse executar(UUID id) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));
        return UsuarioResponseFactory.criar(usuario);
    }
}
