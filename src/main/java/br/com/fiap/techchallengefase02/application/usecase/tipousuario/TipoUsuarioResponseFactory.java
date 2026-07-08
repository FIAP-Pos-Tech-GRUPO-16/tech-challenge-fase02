package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.application.dto.TipoUsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;

final class TipoUsuarioResponseFactory {

    private TipoUsuarioResponseFactory() {
    }

    static TipoUsuarioResponse criar(TipoUsuario tipoUsuario) {
        return new TipoUsuarioResponse(tipoUsuario.getId(), tipoUsuario.getNome());
    }
}
