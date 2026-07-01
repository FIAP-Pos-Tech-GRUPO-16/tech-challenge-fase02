package br.com.fiap.techchallengefase02.infrastructure.persistence.tipousuario;

import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TipoUsuarioRepositoryImpl implements TipoUsuarioRepository {

    private final TipoUsuarioJpaRepository jpaRepository;
    private final TipoUsuarioMapper mapper;

    public TipoUsuarioRepositoryImpl(TipoUsuarioJpaRepository jpaRepository, TipoUsuarioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<TipoUsuario> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existePorId(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existePorNome(String nome) {
        return jpaRepository.existsByNome(nome);
    }

    @Override
    public TipoUsuario salvar(TipoUsuario tipoUsuario) {
        TipoUsuarioJpaEntity entidadeSalva = jpaRepository.save(mapper.toJpaEntity(tipoUsuario));
        return mapper.toDomain(entidadeSalva);
    }

    @Override
    public void excluirPorId(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public ResultadoPaginado<TipoUsuario> listarPaginado(int pagina, int tamanho) {
        Page<TipoUsuarioJpaEntity> resultado = jpaRepository.findAll(PageRequest.of(pagina, tamanho));

        return new ResultadoPaginado<>(
                resultado.getContent().stream().map(mapper::toDomain).toList(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.getNumber(),
                resultado.getSize()
        );
    }
}
