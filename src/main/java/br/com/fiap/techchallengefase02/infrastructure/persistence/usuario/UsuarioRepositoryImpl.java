package br.com.fiap.techchallengefase02.infrastructure.persistence.usuario;

import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do contrato de domínio {@link UsuarioRepository} usando
 * Spring Data JPA. É a única camada que conhece {@code Page}/{@code Pageable}
 * — o domínio e a aplicação só lidam com {@link ResultadoPaginado}.
 */
@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioMapper mapper;

    public UsuarioRepositoryImpl(UsuarioJpaRepository jpaRepository, UsuarioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioJpaEntity entidadeSalva = jpaRepository.save(mapper.toJpaEntity(usuario));
        return mapper.toDomain(entidadeSalva);
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return jpaRepository.findByLogin(login).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public ResultadoPaginado<Usuario> buscarPorNome(String nome, int pagina, int tamanho) {
        Page<UsuarioJpaEntity> resultado = jpaRepository.findByNomeContainingIgnoreCase(nome, PageRequest.of(pagina, tamanho));

        return new ResultadoPaginado<>(
                resultado.getContent().stream().map(mapper::toDomain).toList(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.getNumber(),
                resultado.getSize()
        );
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existePorLogin(String login) {
        return jpaRepository.existsByLogin(login);
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
