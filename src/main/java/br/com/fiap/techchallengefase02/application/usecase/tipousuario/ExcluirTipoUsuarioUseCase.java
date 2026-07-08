package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class ExcluirTipoUsuarioUseCase {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public ExcluirTipoUsuarioUseCase(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public void executar(UUID id) {
        if (!tipoUsuarioRepository.existePorId(id)) {
            throw new NoSuchElementException("Tipo de usuário não encontrado");
        }
        tipoUsuarioRepository.excluirPorId(id);
    }
}
