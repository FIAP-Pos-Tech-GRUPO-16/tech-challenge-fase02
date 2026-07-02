package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.application.dto.TipoUsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Caso de uso: buscar um tipo de usuário pelo ID.
 */
@Component
public class BuscarTipoUsuarioPorIdUseCase {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public BuscarTipoUsuarioPorIdUseCase(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public TipoUsuarioResponse executar(UUID id) {
        TipoUsuario tipoUsuario = tipoUsuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Tipo de usuário não encontrado"));

        return TipoUsuarioResponseFactory.criar(tipoUsuario);
    }
}
