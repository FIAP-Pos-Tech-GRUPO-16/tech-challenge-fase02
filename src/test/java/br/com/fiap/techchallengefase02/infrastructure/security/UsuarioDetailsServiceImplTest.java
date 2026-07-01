package br.com.fiap.techchallengefase02.infrastructure.security;

import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceImplTest {

    @InjectMocks
    private UsuarioDetailsServiceImpl service;
    @Mock
    private UsuarioRepository usuarioRepository;

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
    class CarregarUsuarioPorLogin {
        @Test
        void deveCarregarUsuarioPorLogin() {
            Usuario usuario = buildUsuario("joao", "hashed");
            when(usuarioRepository.buscarPorLogin("joao")).thenReturn(Optional.of(usuario));

            UserDetails userDetails = service.loadUserByUsername("joao");

            assertThat(userDetails.getUsername()).isEqualTo("joao");
            assertThat(userDetails.getPassword()).isEqualTo("hashed");
            assertThat(userDetails.getAuthorities()).isEmpty();
        }

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            when(usuarioRepository.buscarPorLogin("desconhecido")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.loadUserByUsername("desconhecido"))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }
}
