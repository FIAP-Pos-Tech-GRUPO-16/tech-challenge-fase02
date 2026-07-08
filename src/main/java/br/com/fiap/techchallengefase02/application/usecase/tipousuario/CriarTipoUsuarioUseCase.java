package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.application.dto.CriarTipoUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.TipoUsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import br.com.fiap.techchallengefase02.domain.exception.RecursoJaExistenteException;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class CriarTipoUsuarioUseCase {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public CriarTipoUsuarioUseCase(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public TipoUsuarioResponse executar(CriarTipoUsuarioRequest request) {
        String nome = request.nome().trim();

        if (tipoUsuarioRepository.existePorNome(nome)) {
            throw new RecursoJaExistenteException("Nome do tipo de usuário já cadastrado");
        }

        TipoUsuario tipoUsuario = TipoUsuario.builder()
                .nome(nome)
                .build();

        TipoUsuario tipoUsuarioSalvo = tipoUsuarioRepository.salvar(tipoUsuario);

        return TipoUsuarioResponseFactory.criar(tipoUsuarioSalvo);
    }
}
