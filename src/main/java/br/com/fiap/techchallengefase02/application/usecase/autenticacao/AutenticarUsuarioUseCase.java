package br.com.fiap.techchallengefase02.application.usecase.autenticacao;

import br.com.fiap.techchallengefase02.application.dto.LoginRequest;
import br.com.fiap.techchallengefase02.application.dto.TokenResponse;
import br.com.fiap.techchallengefase02.application.port.CodificadorDeSenha;
import br.com.fiap.techchallengefase02.application.port.GeradorDeToken;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.exception.CredenciaisInvalidasException;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

/**
 * Caso de uso: autenticar um usuário (login) e gerar seu token JWT.
 */
@Component
public class AutenticarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final CodificadorDeSenha codificadorDeSenha;
    private final GeradorDeToken geradorDeToken;

    public AutenticarUsuarioUseCase(UsuarioRepository usuarioRepository,
                                     CodificadorDeSenha codificadorDeSenha,
                                     GeradorDeToken geradorDeToken) {
        this.usuarioRepository = usuarioRepository;
        this.codificadorDeSenha = codificadorDeSenha;
        this.geradorDeToken = geradorDeToken;
    }

    public TokenResponse executar(LoginRequest request) {
        String login = request.login().trim();

        Usuario usuario = usuarioRepository.buscarPorLogin(login)
                .orElseThrow(() -> new CredenciaisInvalidasException("Usuário ou senha incorreta"));

        if (!codificadorDeSenha.corresponde(request.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Usuário ou senha incorreta");
        }

        String token = geradorDeToken.gerar(usuario);
        return new TokenResponse(token);
    }
}
