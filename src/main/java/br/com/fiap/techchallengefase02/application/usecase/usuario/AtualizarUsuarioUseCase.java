package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.application.dto.AtualizarUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.EnderecoDTO;
import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.exception.RecursoJaExistenteException;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Caso de uso: atualizar parcialmente os dados de um usuário (exceto senha).
 */
@Component
public class AtualizarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public AtualizarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse executar(UUID id, AtualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        if (request.email() != null && !request.email().equals(usuario.getEmail())) {
            if (usuarioRepository.existePorEmail(request.email())) {
                throw new RecursoJaExistenteException("Email já cadastrado");
            }
            usuario.setEmail(request.email());
        }

        if (request.login() != null && !request.login().equals(usuario.getLogin())) {
            if (usuarioRepository.existePorLogin(request.login())) {
                throw new RecursoJaExistenteException("Login já cadastrado");
            }
            usuario.setLogin(request.login());
        }

        if (request.nome() != null) {
            usuario.setNome(request.nome());
        }

        if (request.endereco() != null) {
            usuario.setEndereco(paraEndereco(request.endereco()));
        }

        usuario.marcarComoAlterado();

        Usuario usuarioAtualizado = usuarioRepository.salvar(usuario);

        return UsuarioResponseFactory.criar(usuarioAtualizado);
    }

    private Endereco paraEndereco(EnderecoDTO dto) {
        return Endereco.builder()
                .rua(dto.rua().trim())
                .numero(dto.numero().trim())
                .cidade(dto.cidade().trim())
                .cep(dto.cep() == null ? null : dto.cep().trim())
                .build();
    }
}
