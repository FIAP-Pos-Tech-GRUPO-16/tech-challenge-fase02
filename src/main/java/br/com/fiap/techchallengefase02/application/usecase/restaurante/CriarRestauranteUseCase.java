package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.application.dto.CriarRestauranteRequest;
import br.com.fiap.techchallengefase02.application.dto.RestauranteResponse;
import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

/**
 * Caso de uso: criar um novo restaurante.
 * <p>
 * Valida apenas se o usuário dono existe. Validar tipo/perfil do usuário está
 * fora do escopo documentado para esta etapa.
 */
@Component
public class CriarRestauranteUseCase {

    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;

    public CriarRestauranteUseCase(RestauranteRepository restauranteRepository,
                                   UsuarioRepository usuarioRepository) {
        this.restauranteRepository = restauranteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public RestauranteResponse executar(CriarRestauranteRequest request) {
        if (!usuarioRepository.existePorId(request.donoId())) {
            throw new NoSuchElementException("Dono do restaurante não encontrado");
        }
        Restaurante restaurante = Restaurante.builder()
                .nome(request.nome().trim())
                .endereco(request.endereco().paraDominio())
                .tipoCozinha(request.tipoCozinha().trim())
                .horarioFuncionamento(request.horarioFuncionamento().trim())
                .donoId(request.donoId())
                .build();
        Restaurante restauranteSalvo = restauranteRepository.salvar(restaurante);
        return RestauranteResponseFactory.criar(restauranteSalvo);
    }
}
