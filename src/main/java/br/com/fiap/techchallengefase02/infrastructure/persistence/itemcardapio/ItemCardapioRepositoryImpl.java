package br.com.fiap.techchallengefase02.infrastructure.persistence.itemcardapio;

import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;
import br.com.fiap.techchallengefase02.domain.repository.ItemCardapioRepository;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ItemCardapioRepositoryImpl implements ItemCardapioRepository {

    private final ItemCardapioJpaRepository jpaRepository;
    private final ItemCardapioMapper mapper;

    public ItemCardapioRepositoryImpl(ItemCardapioJpaRepository jpaRepository, ItemCardapioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ItemCardapio salvar(ItemCardapio itemCardapio) {
        ItemCardapioJpaEntity entidadeSalva = jpaRepository.save(mapper.toJpaEntity(itemCardapio));
        return mapper.toDomain(entidadeSalva);
    }

    @Override
    public Optional<ItemCardapio> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public ResultadoPaginado<ItemCardapio> listarPorRestauranteId(UUID restauranteId, int pagina, int tamanho) {
        Page<ItemCardapioJpaEntity> resultado = jpaRepository.findByRestauranteId(restauranteId, PageRequest.of(pagina, tamanho));
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
