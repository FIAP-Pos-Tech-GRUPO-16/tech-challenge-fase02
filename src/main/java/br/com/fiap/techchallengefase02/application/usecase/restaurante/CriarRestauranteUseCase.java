package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.application.dto.CriarRestauranteRequest;
import br.com.fiap.techchallengefase02.application.dto.EnderecoDTO;
import br.com.fiap.techchallengefase02.application.dto.RestauranteResponse;
import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

/**
 * Caso de uso: criar um novo restaurante.
 * <p>
 * Valida apenas se o usuario dono existe. Validar tipo/perfil do usuario esta
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
            throw new NoSuchElementException("Dono do restaurante nao encontrado");
        }
        Restaurante restaurante = Restaurante.builder()
                .nome(request.nome().trim())
                .endereco(paraEndereco(request.endereco()))
                .tipoCozinha(request.tipoCozinha().trim())
                .horarioFuncionamento(request.horarioFuncionamento().trim())
                .donoId(request.donoId())
                .build();
        Restaurante restauranteSalvo = restauranteRepository.salvar(restaurante);
        return RestauranteResponseFactory.criar(restauranteSalvo);
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
