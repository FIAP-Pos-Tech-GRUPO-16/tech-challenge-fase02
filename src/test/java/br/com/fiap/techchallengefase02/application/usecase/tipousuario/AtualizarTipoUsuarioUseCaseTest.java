package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.application.dto.AtualizarTipoUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.TipoUsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import br.com.fiap.techchallengefase02.domain.exception.RecursoJaExistenteException;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class AtualizarTipoUsuarioUseCaseTest {

    @InjectMocks
    private AtualizarTipoUsuarioUseCase useCase;
    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Nested
    class AtualizarComSucesso {

        @Test
        void deveAtualizarNomeQuandoNovoNomeDisponivel() {
            UUID id = UUID.randomUUID();
            TipoUsuario existente = TipoUsuario.builder().id(id).nome("Cliente").build();
            TipoUsuario atualizado = TipoUsuario.builder().id(id).nome("Cliente Vip").build();

            when(tipoUsuarioRepository.buscarPorId(id)).thenReturn(Optional.of(existente));
            when(tipoUsuarioRepository.existePorNome("Cliente Vip")).thenReturn(false);
            when(tipoUsuarioRepository.salvar(any(TipoUsuario.class))).thenReturn(atualizado);

            TipoUsuarioResponse response = useCase.executar(id, new AtualizarTipoUsuarioRequest("Cliente Vip"));

            assertThat(response.nome()).isEqualTo("Cliente Vip");
        }

        @Test
        void devePermitirManterOMesmoNome() {
            UUID id = UUID.randomUUID();
            TipoUsuario existente = TipoUsuario.builder().id(id).nome("Cliente").build();

            when(tipoUsuarioRepository.buscarPorId(id)).thenReturn(Optional.of(existente));
            when(tipoUsuarioRepository.salvar(any(TipoUsuario.class))).thenReturn(existente);

            useCase.executar(id, new AtualizarTipoUsuarioRequest("Cliente"));

            verify(tipoUsuarioRepository, never()).existePorNome(any());
            verify(tipoUsuarioRepository).salvar(any(TipoUsuario.class));
        }
    }

    @Nested
    class ValidacoesDeErro {

        @Test
        void deveLancarExcecaoQuandoTipoUsuarioNaoEncontrado() {
            UUID id = UUID.randomUUID();
            when(tipoUsuarioRepository.buscarPorId(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.executar(id, new AtualizarTipoUsuarioRequest("Cliente")))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Tipo de usuário não encontrado");

            verify(tipoUsuarioRepository, never()).salvar(any());
        }

        @Test
        void deveLancarExcecaoQuandoNovoNomeJaExiste() {
            UUID id = UUID.randomUUID();
            TipoUsuario existente = TipoUsuario.builder().id(id).nome("Cliente").build();

            when(tipoUsuarioRepository.buscarPorId(id)).thenReturn(Optional.of(existente));
            when(tipoUsuarioRepository.existePorNome("Dono de Restaurante")).thenReturn(true);

            assertThatThrownBy(() -> useCase.executar(id, new AtualizarTipoUsuarioRequest("Dono de Restaurante")))
                    .isInstanceOf(RecursoJaExistenteException.class)
                    .hasMessage("Nome do tipo de usuário já cadastrado");

            verify(tipoUsuarioRepository, never()).salvar(any());
        }
    }
}
