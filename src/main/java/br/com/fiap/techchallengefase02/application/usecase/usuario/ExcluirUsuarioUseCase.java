package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Caso de uso: excluir permanentemente um usuário pelo ID.
 */
@Component
public class ExcluirUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public ExcluirUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void executar(UUID id) {
        if (!usuarioRepository.existePorId(id)) {
            throw new NoSuchElementException("Usuário não encontrado");
        }
        usuarioRepository.excluirPorId(id);
    }
}
