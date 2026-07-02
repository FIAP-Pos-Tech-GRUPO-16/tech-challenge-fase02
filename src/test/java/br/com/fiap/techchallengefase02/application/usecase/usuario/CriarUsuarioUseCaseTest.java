package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.application.dto.CriarUsuarioRequest;
import br.com.fiap.techchallengefase02.application.dto.EnderecoDTO;
import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.application.port.CodificadorDeSenha;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.exception.RecursoJaExistenteException;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarUsuarioUseCaseTest {

    @InjectMocks
    private CriarUsuarioUseCase useCase;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;
    @Mock
    private CodificadorDeSenha codificadorDeSenha;

    private static final UUID TIPO_USUARIO_ID = UUID.randomUUID();

    private CriarUsuarioRequest buildRequest() {
        EnderecoDTO endereco = new EnderecoDTO("Rua das Flores", "123", "São Paulo", "12345678");
        return new CriarUsuarioRequest("João Silva", "joao@email.com", "joao123", "123456", endereco, TIPO_USUARIO_ID);
    }

    private Usuario buildUsuarioSalvo() {
        return Usuario.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .email("joao@email.com")
                .login("joao123")
                .senha("hashed")
                .tipoUsuarioId(TIPO_USUARIO_ID)
                .dataUltimaAlteracao(LocalDateTime.now())
                .build();
    }

    @Nested
    class CriarComSucesso {

        @Test
        void deveCriarUsuarioQuandoDadosValidos() {
            CriarUsuarioRequest request = buildRequest();
            Usuario usuarioSalvo = buildUsuarioSalvo();

            when(tipoUsuarioRepository.existePorId(TIPO_USUARIO_ID)).thenReturn(true);
            when(usuarioRepository.existePorEmail("joao@email.com")).thenReturn(false);
            when(usuarioRepository.existePorLogin("joao123")).thenReturn(false);
            when(codificadorDeSenha.codificar("123456")).thenReturn("hashed");
            when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(usuarioSalvo);

            UsuarioResponse response = useCase.executar(request);

            assertThat(response.nome()).isEqualTo("João Silva");
            assertThat(response.tipoUsuarioId()).isEqualTo(TIPO_USUARIO_ID);
            verify(codificadorDeSenha).codificar("123456");
        }

        @Test
        void deveCriarUsuarioComEnderecoSemCepQuandoCepNaoInformado() {
            EnderecoDTO enderecoSemCep = new EnderecoDTO("Rua das Flores", "123", "São Paulo", null);
            CriarUsuarioRequest request = new CriarUsuarioRequest("João Silva", "joao@email.com", "joao123", "123456", enderecoSemCep, TIPO_USUARIO_ID);
            Usuario usuarioSalvo = buildUsuarioSalvo();

            when(tipoUsuarioRepository.existePorId(TIPO_USUARIO_ID)).thenReturn(true);
            when(usuarioRepository.existePorEmail("joao@email.com")).thenReturn(false);
            when(usuarioRepository.existePorLogin("joao123")).thenReturn(false);
            when(codificadorDeSenha.codificar("123456")).thenReturn("hashed");

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
            when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(usuarioSalvo);

            useCase.executar(request);

            verify(usuarioRepository).salvar(captor.capture());
            assertThat(captor.getValue().getEndereco().getCep()).isNull();
        }

        @Test
        void deveCodificarSenhaAntesDeSalvar() {
            CriarUsuarioRequest request = buildRequest();
            Usuario usuarioSalvo = buildUsuarioSalvo();

            when(tipoUsuarioRepository.existePorId(TIPO_USUARIO_ID)).thenReturn(true);
            when(usuarioRepository.existePorEmail(any())).thenReturn(false);
            when(usuarioRepository.existePorLogin(any())).thenReturn(false);
            when(codificadorDeSenha.codificar("123456")).thenReturn("hashed");
            when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(usuarioSalvo);

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);

            useCase.executar(request);

            verify(usuarioRepository).salvar(captor.capture());
            assertThat(captor.getValue().getSenha()).isEqualTo("hashed");
            assertThat(captor.getValue().getDataUltimaAlteracao()).isNotNull();
        }
    }

    @Nested
    class ValidacoesDeConflito {

        @Test
        void deveLancarExcecaoQuandoTipoUsuarioNaoExiste() {
            CriarUsuarioRequest request = buildRequest();
            when(tipoUsuarioRepository.existePorId(TIPO_USUARIO_ID)).thenReturn(false);

            assertThatThrownBy(() -> useCase.executar(request))
                    .isInstanceOf(java.util.NoSuchElementException.class)
                    .hasMessage("Tipo de usuário não encontrado");

            verify(usuarioRepository, never()).salvar(any());
        }

        @Test
        void deveLancarExcecaoQuandoEmailJaExiste() {
            CriarUsuarioRequest request = buildRequest();
            when(tipoUsuarioRepository.existePorId(TIPO_USUARIO_ID)).thenReturn(true);
            when(usuarioRepository.existePorEmail("joao@email.com")).thenReturn(true);

            assertThatThrownBy(() -> useCase.executar(request))
                    .isInstanceOf(RecursoJaExistenteException.class)
                    .hasMessage("Email já cadastrado");

            verify(usuarioRepository, never()).salvar(any());
            verify(usuarioRepository, never()).existePorLogin(any());
        }

        @Test
        void deveLancarExcecaoQuandoLoginJaExiste() {
            CriarUsuarioRequest request = buildRequest();
            when(tipoUsuarioRepository.existePorId(TIPO_USUARIO_ID)).thenReturn(true);
            when(usuarioRepository.existePorEmail("joao@email.com")).thenReturn(false);
            when(usuarioRepository.existePorLogin("joao123")).thenReturn(true);

            assertThatThrownBy(() -> useCase.executar(request))
                    .isInstanceOf(RecursoJaExistenteException.class)
                    .hasMessage("Login já cadastrado");

            verify(usuarioRepository, never()).salvar(any());
        }
    }
}
