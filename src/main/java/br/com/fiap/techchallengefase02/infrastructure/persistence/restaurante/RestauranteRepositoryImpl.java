package br.com.fiap.techchallengefase02.infrastructure.persistence.restaurante;

import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RestauranteRepositoryImpl implements RestauranteRepository {

    private final RestauranteJpaRepository jpaRepository;
    private final RestauranteMapper mapper;

    public RestauranteRepositoryImpl(RestauranteJpaRepository jpaRepository, RestauranteMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    @Override

    public Restaurante salvar(Restaurante restaurante) {
        RestauranteJpaEntity entidadeSalva = jpaRepository.save(mapper.toJpaEntity(restaurante));
        return mapper.toDomain(entidadeSalva);
    }
    @Override

    public Optional<Restaurante> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    @Override

    public ResultadoPaginado<Restaurante> listarPaginado(int pagina, int tamanho) {
        Page<RestauranteJpaEntity> resultado = jpaRepository.findAll(PageRequest.of(pagina, tamanho));
        return new ResultadoPaginado<>(
                resultado.getContent().stream().map(mapper::toDomain).toList(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.getNumber(),
                resultado.getSize()
        );
    }
    @Override

    public boolean existePorId(UUID id) {
        return jpaRepository.existsById(id);
    }
    @Override

    public void excluirPorId(UUID id) {
        jpaRepository.deleteById(id);
    }
}
