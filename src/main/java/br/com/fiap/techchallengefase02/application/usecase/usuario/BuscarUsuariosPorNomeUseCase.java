package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class BuscarUsuariosPorNomeUseCase {

    private final UsuarioRepository usuarioRepository;

    public BuscarUsuariosPorNomeUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Pagina<UsuarioResponse> executar(String nome, int pagina, int tamanho) {
        ResultadoPaginado<Usuario> resultado = usuarioRepository.buscarPorNome(nome, pagina, tamanho);

        return new Pagina<>(
                resultado.conteudo().stream().map(UsuarioResponseFactory::criar).toList(),
                resultado.totalElementos(),
                resultado.totalPaginas(),
                resultado.pagina(),
                resultado.tamanho()
        );
    }
}
