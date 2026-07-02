package br.com.fiap.techchallengefase02.infrastructure.persistence.usuario;

import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * {@code buscarPorEmail} não é usado por nenhum caso de uso hoje, mas faz
 * parte do contrato {@link br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository}
 * e precisa se comportar corretamente (delegando ao Spring Data e mapeando
 * o resultado para o domínio).
 */
@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryImplTest {

    @InjectMocks
    private UsuarioRepositoryImpl repository;
    @Mock
    private UsuarioJpaRepository jpaRepository;
    @Mock
    private UsuarioMapper mapper;

    @Test
    void deveRetornarUsuarioMapeadoQuandoEmailExistir() {
        String email = "joao@email.com";
        UsuarioJpaEntity entidade = UsuarioJpaEntity.builder().id(UUID.randomUUID()).email(email).build();
        Usuario usuarioDominio = Usuario.builder().id(entidade.getId()).email(email).build();

        when(jpaRepository.findByEmail(email)).thenReturn(Optional.of(entidade));
        when(mapper.toDomain(entidade)).thenReturn(usuarioDominio);

        Optional<Usuario> resultado = repository.buscarPorEmail(email);

        assertThat(resultado).contains(usuarioDominio);
    }

    @Test
    void deveRetornarOptionalVazioQuandoEmailNaoExistir() {
        String email = "inexistente@email.com";
        when(jpaRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = repository.buscarPorEmail(email);

        assertThat(resultado).isEmpty();
    }
}
