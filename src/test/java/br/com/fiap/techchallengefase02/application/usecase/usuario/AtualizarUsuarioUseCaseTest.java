package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.application.dto.AtualizarUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.EnderecoDTO;
import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.exception.RecursoJaExistenteException;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarUsuarioUseCaseTest {

    @InjectMocks
    private AtualizarUsuarioUseCase useCase;
    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario buildUsuario(UUID id, String email, String login) {
        return Usuario.builder()
                .id(id)
                .nome("João Silva")
                .email(email)
                .login(login)
                .senha("hashed")
                .endereco(new Endereco("Rua das Flores", "123", "São Paulo", "01310100"))
                .tipoUsuarioId(UUID.randomUUID())
                .dataUltimaAlteracao(LocalDateTime.now())
                .build();
    }

    @Nested
    class AtualizarComSucesso {

        @Test
        void deveAtualizarNomeComSucesso() {
            UUID id = UUID.randomUUID();
            Usuario usuario = buildUsuario(id, "joao@email.com", "joao123");
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("Novo Nome", null, null, null);

            when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.salvar(usuario)).thenReturn(usuario);

            UsuarioResponse response = useCase.executar(id, request);

            assertThat(response.nome()).isEqualTo("Novo Nome");
            verify(usuarioRepository).salvar(usuario);
        }

        @Test
        void devePermitirLoginIgualSemValidarConflito() {
            UUID id = UUID.randomUUID();
            Usuario usuario = buildUsuario(id, "joao@email.com", "joao123");
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(null, null, "joao123", null);

            when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.salvar(usuario)).thenReturn(usuario);

            useCase.executar(id, request);

            verify(usuarioRepository, never()).existePorLogin(any());
        }

        @Test
        void devePermitirEmailIgualSemValidarConflito() {
            UUID id = UUID.randomUUID();
            Usuario usuario = buildUsuario(id, "joao@email.com", "joao123");
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(null, "joao@email.com", null, null);

            when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.salvar(usuario)).thenReturn(usuario);

            useCase.executar(id, request);

            verify(usuarioRepository, never()).existePorEmail(any());
        }

        @Test
        void deveAtualizarEnderecoComSucesso() {
            UUID id = UUID.randomUUID();
            Usuario usuario = buildUsuario(id, "joao@email.com", "joao123");
            EnderecoDTO novoEndereco = new EnderecoDTO("Nova Rua", "456", "Rio de Janeiro", "20040020");
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(null, null, null, novoEndereco);

            when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.salvar(usuario)).thenReturn(usuario);

            UsuarioResponse response = useCase.executar(id, request);

            assertThat(response.endereco().rua()).isEqualTo("Nova Rua");
        }
    }

    @Nested
    class ValidacoesDeConflito {

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            UUID id = UUID.randomUUID();
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("Nome", null, null, null);
            when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.executar(id, request))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Usuário não encontrado");
        }

        @Test
        void deveLancarExcecaoQuandoNovoEmailJaExiste() {
            UUID id = UUID.randomUUID();
            Usuario usuario = buildUsuario(id, "joao@email.com", "joao123");
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(null, "existente@email.com", null, null);

            when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.existePorEmail("existente@email.com")).thenReturn(true);

            assertThatThrownBy(() -> useCase.executar(id, request))
                    .isInstanceOf(RecursoJaExistenteException.class)
                    .hasMessage("Email já cadastrado");

            verify(usuarioRepository, never()).salvar(any());
        }

        @Test
        void deveLancarExcecaoQuandoNovoLoginJaExiste() {
            UUID id = UUID.randomUUID();
            Usuario usuario = buildUsuario(id, "joao@email.com", "joao123");
            AtualizarUsuarioRequest request = new AtualizarUsuarioRequest(null, null, "loginexistente", null);

            when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.existePorLogin("loginexistente")).thenReturn(true);

            assertThatThrownBy(() -> useCase.executar(id, request))
                    .isInstanceOf(RecursoJaExistenteException.class)
                    .hasMessage("Login já cadastrado");

            verify(usuarioRepository, never()).salvar(any());
        }
    }
}
