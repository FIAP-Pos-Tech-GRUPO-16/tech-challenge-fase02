package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.application.dto.CriarUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.EnderecoDTO;
import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.application.port.CodificadorDeSenha;
import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.exception.RecursoJaExistenteException;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

/**
 * Caso de uso: criar um novo usuário.
 *
 * Valida que o {@code tipoUsuarioId} informado existe, e que email/login
 * ainda não estão em uso, antes de persistir o usuário com a senha
 * codificada.
 */
@Component
public class CriarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final CodificadorDeSenha codificadorDeSenha;

    public CriarUsuarioUseCase(UsuarioRepository usuarioRepository,
                                TipoUsuarioRepository tipoUsuarioRepository,
                                CodificadorDeSenha codificadorDeSenha) {
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        this.codificadorDeSenha = codificadorDeSenha;
    }

    public UsuarioResponse executar(CriarUsuarioRequest request) {
        if (!tipoUsuarioRepository.existePorId(request.tipoUsuarioId())) {
            throw new NoSuchElementException("Tipo de usuário não encontrado");
        }

        if (usuarioRepository.existePorEmail(request.email())) {
            throw new RecursoJaExistenteException("Email já cadastrado");
        }

        if (usuarioRepository.existePorLogin(request.login())) {
            throw new RecursoJaExistenteException("Login já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome().trim())
                .email(request.email().trim())
                .login(request.login().trim())
                .senha(codificadorDeSenha.codificar(request.senha()))
                .endereco(paraEndereco(request.endereco()))
                .tipoUsuarioId(request.tipoUsuarioId())
                .build();
        usuario.marcarComoAlterado();

        Usuario usuarioSalvo = usuarioRepository.salvar(usuario);

        return UsuarioResponseFactory.criar(usuarioSalvo);
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
