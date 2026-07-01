package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarUsuariosPorNomeUseCaseTest {

    @InjectMocks
    private BuscarUsuariosPorNomeUseCase useCase;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarPaginaDeUsuariosCorrespondentes() {
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .email("joao@email.com")
                .login("joao123")
                .senha("hashed")
                .endereco(new Endereco("Rua", "1", "Cidade", "12345678"))
                .tipoUsuarioId(UUID.randomUUID())
                .dataUltimaAlteracao(LocalDateTime.now())
                .build();

        ResultadoPaginado<Usuario> resultado = new ResultadoPaginado<>(List.of(usuario), 1, 1, 0, 10);
        when(usuarioRepository.buscarPorNome("João", 0, 10)).thenReturn(resultado);

        Pagina<UsuarioResponse> pagina = useCase.executar("João", 0, 10);

        assertThat(pagina.conteudo()).hasSize(1);
        assertThat(pagina.totalElementos()).isEqualTo(1);
        assertThat(pagina.conteudo().get(0).nome()).isEqualTo("João Silva");
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoHaCorrespondencia() {
        ResultadoPaginado<Usuario> resultado = new ResultadoPaginado<>(List.of(), 0, 0, 0, 10);
        when(usuarioRepository.buscarPorNome("Desconhecido", 0, 10)).thenReturn(resultado);

        Pagina<UsuarioResponse> pagina = useCase.executar("Desconhecido", 0, 10);

        assertThat(pagina.conteudo()).isEmpty();
        assertThat(pagina.totalElementos()).isZero();
    }
}
