package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssociarTipoUsuarioAoUsuarioUseCaseTest {

    @InjectMocks
    private AssociarTipoUsuarioAoUsuarioUseCase useCase;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Nested
    class AssociarComSucesso {

        @Test
        void deveAssociarTipoUsuarioAoUsuario() {
            UUID usuarioId = UUID.randomUUID();
            UUID tipoUsuarioId = UUID.randomUUID();
            Usuario usuario = Usuario.builder().id(usuarioId).nome("João Silva").build();

            when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
            when(tipoUsuarioRepository.existePorId(tipoUsuarioId)).thenReturn(true);
            when(usuarioRepository.salvar(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UsuarioResponse response = useCase.executar(usuarioId, tipoUsuarioId);

            assertThat(response.tipoUsuarioId()).isEqualTo(tipoUsuarioId);
        }

        @Test
        void deveMarcarUsuarioComoAlteradoAoAssociar() {
            UUID usuarioId = UUID.randomUUID();
            UUID tipoUsuarioId = UUID.randomUUID();
            Usuario usuario = Usuario.builder().id(usuarioId).build();

            when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
            when(tipoUsuarioRepository.existePorId(tipoUsuarioId)).thenReturn(true);
            when(usuarioRepository.salvar(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);

            useCase.executar(usuarioId, tipoUsuarioId);

            verify(usuarioRepository).salvar(captor.capture());
            assertThat(captor.getValue().getTipoUsuarioId()).isEqualTo(tipoUsuarioId);
            assertThat(captor.getValue().getDataUltimaAlteracao()).isNotNull();
        }
    }

    @Nested
    class ValidacoesDeErro {

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            UUID usuarioId = UUID.randomUUID();
            UUID tipoUsuarioId = UUID.randomUUID();

            when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.executar(usuarioId, tipoUsuarioId))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Usuário não encontrado");

            verify(usuarioRepository, never()).salvar(any());
            verify(tipoUsuarioRepository, never()).existePorId(any());
        }

        @Test
        void deveLancarExcecaoQuandoTipoUsuarioNaoEncontrado() {
            UUID usuarioId = UUID.randomUUID();
            UUID tipoUsuarioId = UUID.randomUUID();
            Usuario usuario = Usuario.builder().id(usuarioId).build();

            when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
            when(tipoUsuarioRepository.existePorId(tipoUsuarioId)).thenReturn(false);

            assertThatThrownBy(() -> useCase.executar(usuarioId, tipoUsuarioId))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Tipo de usuário não encontrado");

            verify(usuarioRepository, never()).salvar(any());
        }
    }
}
