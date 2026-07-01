package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.application.dto.AtualizarTipoUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.TipoUsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import br.com.fiap.techchallengefase02.domain.exception.RecursoJaExistenteException;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Caso de uso: atualizar o nome de um tipo de usuário existente.
 */
@Component
public class AtualizarTipoUsuarioUseCase {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public AtualizarTipoUsuarioUseCase(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public TipoUsuarioResponse executar(UUID id, AtualizarTipoUsuarioRequest request) {
        TipoUsuario tipoUsuario = tipoUsuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Tipo de usuário não encontrado"));

        String novoNome = request.nome().trim();

        if (!novoNome.equalsIgnoreCase(tipoUsuario.getNome()) && tipoUsuarioRepository.existePorNome(novoNome)) {
            throw new RecursoJaExistenteException("Nome do tipo de usuário já cadastrado");
        }

        tipoUsuario.setNome(novoNome);

        TipoUsuario tipoUsuarioAtualizado = tipoUsuarioRepository.salvar(tipoUsuario);

        return TipoUsuarioResponseFactory.criar(tipoUsuarioAtualizado);
    }
}
