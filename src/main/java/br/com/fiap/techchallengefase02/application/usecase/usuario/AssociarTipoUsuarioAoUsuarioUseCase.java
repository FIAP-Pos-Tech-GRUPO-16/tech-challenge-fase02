package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Caso de uso: associar um tipo de usuário existente a um usuário já
 * cadastrado.
 *
 * Fica no pacote de casos de uso de {@code Usuario} (e não no de
 * {@code TipoUsuario}) porque quem é alterado e salvo é o agregado
 * {@link Usuario} — mesmo padrão já usado em {@link CriarUsuarioUseCase},
 * que também depende de {@link TipoUsuarioRepository} apenas para validar
 * o tipo informado.
 */
@Component
public class AssociarTipoUsuarioAoUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;

    public AssociarTipoUsuarioAoUsuarioUseCase(UsuarioRepository usuarioRepository,
                                                TipoUsuarioRepository tipoUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public UsuarioResponse executar(UUID usuarioId, UUID tipoUsuarioId) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));

        if (!tipoUsuarioRepository.existePorId(tipoUsuarioId)) {
            throw new NoSuchElementException("Tipo de usuário não encontrado");
        }

        usuario.associarTipo(tipoUsuarioId);
        usuario.marcarComoAlterado();

        Usuario usuarioAtualizado = usuarioRepository.salvar(usuario);

        return UsuarioResponseFactory.criar(usuarioAtualizado);
    }
}
