package br.com.fiap.techchallengefase02.application.usecase.autenticacao;

import br.com.fiap.techchallengefase02.application.dto.LoginRequest;
import br.com.fiap.techchallengefase02.application.dto.TokenResponse;
import br.com.fiap.techchallengefase02.application.port.CodificadorDeSenha;
import br.com.fiap.techchallengefase02.application.port.GeradorDeToken;
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
class AutenticarUsuarioUseCaseTest {

    @InjectMocks
    private AutenticarUsuarioUseCase useCase;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CodificadorDeSenha codificadorDeSenha;
    @Mock
    private GeradorDeToken geradorDeToken;

    private Usuario buildUsuario(String login, String senha) {
        return Usuario.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .email("joao@email.com")
                .login(login)
                .senha(senha)
                .tipoUsuarioId(UUID.randomUUID())
                .build();
    }

    @Nested
    class LoginComSucesso {
        @Test
        void deveAutenticarComSucesso() {
            LoginRequest request = new LoginRequest("joao", "123");
            Usuario usuario = buildUsuario("joao", "hashed");

            when(usuarioRepository.buscarPorLogin("joao")).thenReturn(Optional.of(usuario));
            when(codificadorDeSenha.corresponde("123", "hashed")).thenReturn(true);
            when(geradorDeToken.gerar(usuario)).thenReturn("fake-jwt-token");

            TokenResponse result = useCase.executar(request);

            assertThat(result.token()).isEqualTo("fake-jwt-token");
            assertThat(result.tipo()).isEqualTo("Bearer");
            verify(geradorDeToken).gerar(usuario);
        }
    }

    @Nested
    class LoginComFalha {
        @Test
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            LoginRequest request = new LoginRequest("maria", "123");
            when(usuarioRepository.buscarPorLogin("maria")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.executar(request))
                    .isInstanceOf(CredenciaisInvalidasException.class)
                    .hasMessage("Usuário ou senha incorreta");
        }

        @Test
        void deveLancarExcecaoQuandoSenhaIncorreta() {
            LoginRequest request = new LoginRequest("pedro", "errada");
            Usuario usuario = buildUsuario("pedro", "hashed");

            when(usuarioRepository.buscarPorLogin("pedro")).thenReturn(Optional.of(usuario));
            when(codificadorDeSenha.corresponde("errada", "hashed")).thenReturn(false);

            assertThatThrownBy(() -> useCase.executar(request))
                    .isInstanceOf(CredenciaisInvalidasException.class)
                    .hasMessage("Usuário ou senha incorreta");
        }
    }
}
