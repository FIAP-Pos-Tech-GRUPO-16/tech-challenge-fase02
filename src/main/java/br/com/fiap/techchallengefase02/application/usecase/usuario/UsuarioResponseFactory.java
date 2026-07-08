package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.application.dto.EnderecoDTO;
import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;

final class UsuarioResponseFactory {

    private UsuarioResponseFactory() {
    }

    static UsuarioResponse criar(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getLogin(),
                usuario.getTipoUsuarioId(),
                criarEndereco(usuario.getEndereco()),
                usuario.getDataUltimaAlteracao()
        );
    }

    private static EnderecoDTO criarEndereco(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoDTO(endereco.getRua(), endereco.getNumero(), endereco.getCidade(), endereco.getCep());
    }
}
