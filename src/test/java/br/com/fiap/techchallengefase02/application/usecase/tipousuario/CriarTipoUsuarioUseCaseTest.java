package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.application.dto.CriarTipoUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.TipoUsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import br.com.fiap.techchallengefase02.domain.exception.RecursoJaExistenteException;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarTipoUsuarioUseCaseTest {

    @InjectMocks
    private CriarTipoUsuarioUseCase useCase;
    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Nested
    class CriarComSucesso {

        @Test
        void deveCriarTipoUsuarioQuandoNomeDisponivel() {
            CriarTipoUsuarioRequest request = new CriarTipoUsuarioRequest("Cliente");
            TipoUsuario tipoUsuarioSalvo = TipoUsuario.builder().id(UUID.randomUUID()).nome("Cliente").build();

            when(tipoUsuarioRepository.existePorNome("Cliente")).thenReturn(false);
            when(tipoUsuarioRepository.salvar(any(TipoUsuario.class))).thenReturn(tipoUsuarioSalvo);

            TipoUsuarioResponse response = useCase.executar(request);

            assertThat(response.nome()).isEqualTo("Cliente");
            assertThat(response.id()).isEqualTo(tipoUsuarioSalvo.getId());
        }

        @Test
        void deveRemoverEspacosEmBrancoDoNomeAntesDeSalvar() {
            CriarTipoUsuarioRequest request = new CriarTipoUsuarioRequest("  Cliente  ");
            TipoUsuario tipoUsuarioSalvo = TipoUsuario.builder().id(UUID.randomUUID()).nome("Cliente").build();

            when(tipoUsuarioRepository.existePorNome("Cliente")).thenReturn(false);
            when(tipoUsuarioRepository.salvar(any(TipoUsuario.class))).thenReturn(tipoUsuarioSalvo);

            ArgumentCaptor<TipoUsuario> captor = ArgumentCaptor.forClass(TipoUsuario.class);

            useCase.executar(request);

            verify(tipoUsuarioRepository).salvar(captor.capture());
            assertThat(captor.getValue().getNome()).isEqualTo("Cliente");
        }
    }

    @Nested
    class ValidacoesDeConflito {

        @Test
        void deveLancarExcecaoQuandoNomeJaExiste() {
            CriarTipoUsuarioRequest request = new CriarTipoUsuarioRequest("Cliente");
            when(tipoUsuarioRepository.existePorNome("Cliente")).thenReturn(true);

            assertThatThrownBy(() -> useCase.executar(request))
                    .isInstanceOf(RecursoJaExistenteException.class)
                    .hasMessage("Nome do tipo de usuário já cadastrado");

            verify(tipoUsuarioRepository, never()).salvar(any());
        }
    }
}
