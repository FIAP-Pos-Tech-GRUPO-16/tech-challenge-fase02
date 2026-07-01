package br.com.fiap.techchallengefase02.infrastructure.security;

import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET =
            "dGVjaGZvb2Qtand0LXNlY3JldC1rZXktZmlhcC0yMDI0LXRlY2hjaGFsbGVuZ2U=";

    private JwtService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", 86400000L);
    }

    private Usuario buildUsuario() {
        return Usuario.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .email("joao@email.com")
                .login("joao123")
                .senha("hashed-password")
                .tipoUsuarioId(UUID.randomUUID())
                .build();
    }

    @Nested
    class GerarTokenTests {
        @Test
        void deveGerarTokenNaoNulo() {
            String token = jwtService.gerar(buildUsuario());
            assertThat(token).isNotNull().isNotBlank();
        }

        @Test
        void deveGerarTokenComTresPartes() {
            String token = jwtService.gerar(buildUsuario());
            assertThat(token.split("\\.")).hasSize(3);
        }
    }

    @Nested
    class ExtrairLoginTests {
        @Test
        void deveExtrairLoginDoToken() {
            Usuario usuario = buildUsuario();
            String token = jwtService.gerar(usuario);

            assertThat(jwtService.extractLogin(token)).isEqualTo("joao123");
        }
    }

    @Nested
    class ValidarTokenTests {
        @Test
        void deveRetornarTrueParaTokenValido() {
            String token = jwtService.gerar(buildUsuario());
            assertThat(jwtService.isTokenValid(token)).isTrue();
        }

        @Test
        void deveRetornarFalseParaTokenAdulterado() {
            assertThat(jwtService.isTokenValid("token.invalido.aqui")).isFalse();
        }

        @Test
        void deveRetornarFalseParaTokenEmBranco() {
            assertThat(jwtService.isTokenValid("")).isFalse();
        }

        @Test
        void deveRetornarFalseParaTokenExpirado() {
            ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
            String token = jwtService.gerar(buildUsuario());
            assertThat(jwtService.isTokenValid(token)).isFalse();
        }
    }
}
