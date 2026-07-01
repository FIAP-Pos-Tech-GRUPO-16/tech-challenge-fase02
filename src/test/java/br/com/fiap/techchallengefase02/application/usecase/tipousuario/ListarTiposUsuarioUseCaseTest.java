package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.application.dto.Pagina;
import br.com.fiap.techchallengefase02.application.dto.TipoUsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import br.com.fiap.techchallengefase02.domain.repository.ResultadoPaginado;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarTiposUsuarioUseCaseTest {

    @InjectMocks
    private ListarTiposUsuarioUseCase useCase;
    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Test
    void deveListarTiposUsuarioPaginados() {
        TipoUsuario cliente = TipoUsuario.builder().id(UUID.randomUUID()).nome("Cliente").build();
        TipoUsuario dono = TipoUsuario.builder().id(UUID.randomUUID()).nome("Dono de Restaurante").build();

        ResultadoPaginado<TipoUsuario> resultado = new ResultadoPaginado<>(List.of(cliente, dono), 2, 1, 0, 10);
        when(tipoUsuarioRepository.listarPaginado(0, 10)).thenReturn(resultado);

        Pagina<TipoUsuarioResponse> pagina = useCase.executar(0, 10);

        assertThat(pagina.conteudo()).hasSize(2);
        assertThat(pagina.conteudo().get(0).nome()).isEqualTo("Cliente");
        assertThat(pagina.totalElementos()).isEqualTo(2);
        assertThat(pagina.totalPaginas()).isEqualTo(1);
    }
}
