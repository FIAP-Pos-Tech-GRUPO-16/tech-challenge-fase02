package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.application.dto.AtualizarRestauranteRequest;
import br.com.fiap.techchallengefase02.application.dto.RestauranteResponse;
import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class AtualizarRestauranteUseCase {

    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;

    public AtualizarRestauranteUseCase(RestauranteRepository restauranteRepository,
                                       UsuarioRepository usuarioRepository) {
        this.restauranteRepository = restauranteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public RestauranteResponse executar(UUID id, AtualizarRestauranteRequest request) {
        Restaurante restaurante = restauranteRepository.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Restaurante não encontrado"));

        if (!usuarioRepository.existePorId(request.donoId())) {
            throw new NoSuchElementException("Dono do restaurante não encontrado");
        }

        restaurante.setNome(request.nome().trim());
        restaurante.setEndereco(request.endereco().paraDominio());
        restaurante.setTipoCozinha(request.tipoCozinha().trim());
        restaurante.setHorarioFuncionamento(request.horarioFuncionamento().trim());
        restaurante.setDonoId(request.donoId());
        Restaurante restauranteAtualizado = restauranteRepository.salvar(restaurante);
        return RestauranteResponseFactory.criar(restauranteAtualizado);
    }
}
