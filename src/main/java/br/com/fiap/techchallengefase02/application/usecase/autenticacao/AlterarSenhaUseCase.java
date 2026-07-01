package br.com.fiap.techchallengefase02.application.usecase.autenticacao;

import br.com.fiap.techchallengefase02.application.dto.AlterarSenhaRequest;
import br.com.fiap.techchallengefase02.application.port.CodificadorDeSenha;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.exception.CredenciaisInvalidasException;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Caso de uso: alterar a senha de um usuário, mediante confirmação do
 * login e da senha atual.
 */
@Component
public class AlterarSenhaUseCase {

    private final UsuarioRepository usuarioRepository;
    private final CodificadorDeSenha codificadorDeSenha;

    public AlterarSenhaUseCase(UsuarioRepository usuarioRepository, CodificadorDeSenha codificadorDeSenha) {
        this.usuarioRepository = usuarioRepository;
        this.codificadorDeSenha = codificadorDeSenha;
    }

    public void executar(UUID id, AlterarSenhaRequest request) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new CredenciaisInvalidasException("Usuário ou senha inválidos"));

        if (!usuario.getLogin().equals(request.login())) {
            throw new CredenciaisInvalidasException("Login informado não corresponde ao usuário");
        }

        if (!codificadorDeSenha.corresponde(request.senhaAtual(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Usuário ou senha inválidos");
        }

        usuario.setSenha(codificadorDeSenha.codificar(request.novaSenha()));
        usuario.marcarComoAlterado();

        usuarioRepository.salvar(usuario);
    }
}
