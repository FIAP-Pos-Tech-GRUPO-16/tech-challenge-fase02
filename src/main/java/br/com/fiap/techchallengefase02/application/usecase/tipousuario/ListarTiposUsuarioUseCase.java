package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.application.dto.TipoUsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: listar os tipos de usuário cadastrados, de forma paginada.
 */
@Component
public class ListarTiposUsuarioUseCase {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public ListarTiposUsuarioUseCase(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public Pagina<TipoUsuarioResponse> executar(int pagina, int tamanho) {
        ResultadoPaginado<TipoUsuario> resultado = tipoUsuarioRepository.listarPaginado(pagina, tamanho);

        return new Pagina<>(
                resultado.conteudo().stream().map(TipoUsuarioResponseFactory::criar).toList(),
                resultado.totalElementos(),
                resultado.totalPaginas(),
                resultado.pagina(),
                resultado.tamanho()
        );
    }
}
