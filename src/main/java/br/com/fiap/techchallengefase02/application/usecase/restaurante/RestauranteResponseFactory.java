package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.application.dto.EnderecoDTO;
import br.com.fiap.techchallengefase02.application.dto.RestauranteResponse;
import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Restaurante;

public final class RestauranteResponseFactory {

    private RestauranteResponseFactory() {
    }

    public static RestauranteResponse criar(Restaurante restaurante) {
        return new RestauranteResponse(
                restaurante.getId(),
                restaurante.getNome(),
                criarEndereco(restaurante.getEndereco()),
                restaurante.getTipoCozinha(),
                restaurante.getHorarioFuncionamento(),
                restaurante.getDonoId()
        );
    }

    private static EnderecoDTO criarEndereco(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoDTO(
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getCidade(),
                endereco.getCep()
        );
    }
}
