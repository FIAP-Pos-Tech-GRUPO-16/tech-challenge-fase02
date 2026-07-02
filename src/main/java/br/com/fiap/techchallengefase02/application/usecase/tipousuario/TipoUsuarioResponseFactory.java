package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.application.dto.TipoUsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;

/**
 * Conversão de {@link TipoUsuario} (domínio) para {@link TipoUsuarioResponse}
 * (DTO de saída dos casos de uso). Compartilhada pelos casos de uso de tipo
 * de usuário para evitar duplicação.
 */
final class TipoUsuarioResponseFactory {

    private TipoUsuarioResponseFactory() {
    }

    static TipoUsuarioResponse criar(TipoUsuario tipoUsuario) {
        return new TipoUsuarioResponse(tipoUsuario.getId(), tipoUsuario.getNome());
    }
}
