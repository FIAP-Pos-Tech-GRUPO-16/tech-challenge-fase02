package br.com.fiap.techchallengefase02.application.usecase.autenticacao;

import br.com.fiap.techchallengefase02.application.dto.AlterarSenhaRequest;
import br.com.fiap.techchallengefase02.application.port.CodificadorDeSenha;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.exception.CredenciaisInvalidasException;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlterarSenhaUseCaseTest {

    @InjectMocks
    private AlterarSenhaUseCase useCase;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CodificadorDeSenha codificadorDeSenha;

    private static final UUID USUARIO_ID = UUID.randomUUID();

    private Usuario buildUsuario(String login, String senha) {
        return Usuario.builder()
                .id(USUARIO_ID)
                .nome("João Silva")
                .email("joao@email.com")
                .login(login)
                .senha(senha)
                .tipoUsuarioId(UUID.randomUUID())
                .build();
    }

    @Nested
    class AlterarComSucesso {
        @Test
        void deveAlterarSenhaComSucesso() {
            AlterarSenhaRequest request = new AlterarSenhaRequest("ana", "senhaAntiga", "senhaNova");
            Usuario usuario = buildUsuario("ana", "hashed-antiga");

            when(usuarioRepository.buscarPorId(USUARIO_ID)).thenReturn(Optional.of(usuario));
            when(codificadorDeSenha.corresponde("senhaAntiga", "hashed-antiga")).thenReturn(true);
            when(codificadorDeSenha.codificar("senhaNova")).thenReturn("hashed-nova");

            useCase.executar(USUARIO_ID, request);

            assertThat(usuario.getSenha()).isEqualTo("hashed-nova");
            verify(usuarioRepository).salvar(usuario);
        }
    }

    @Nested
    class AlterarComFalha {
        @Test
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            AlterarSenhaRequest request = new AlterarSenhaRequest("tiago", "senhaAntiga", "senhaNova");
            when(usuarioRepository.buscarPorId(USUARIO_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.executar(USUARIO_ID, request))
                    .isInstanceOf(CredenciaisInvalidasException.class)
                    .hasMessage("Usuário ou senha inválidos");
        }

        @Test
        void deveLancarExcecaoQuandoLoginNaoCorresponde() {
            AlterarSenhaRequest request = new AlterarSenhaRequest("loginErrado", "senhaAntiga", "senhaNova");
            Usuario usuario = buildUsuario("liandra", "hashed-antiga");

            when(usuarioRepository.buscarPorId(USUARIO_ID)).thenReturn(Optional.of(usuario));

            assertThatThrownBy(() -> useCase.executar(USUARIO_ID, request))
                    .isInstanceOf(CredenciaisInvalidasException.class)
                    .hasMessage("Login informado não corresponde ao usuário");
        }

        @Test
        void deveLancarExcecaoQuandoSenhaAtualIncorreta() {
            AlterarSenhaRequest request = new AlterarSenhaRequest("ernesto", "senhaErrada", "senhaNova");
            Usuario usuario = buildUsuario("ernesto", "hashed-antiga");

            when(usuarioRepository.buscarPorId(USUARIO_ID)).thenReturn(Optional.of(usuario));
            when(codificadorDeSenha.corresponde("senhaErrada", "hashed-antiga")).thenReturn(false);

            assertThatThrownBy(() -> useCase.executar(USUARIO_ID, request))
                    .isInstanceOf(CredenciaisInvalidasException.class)
                    .hasMessage("Usuário ou senha inválidos");
        }
    }
}
