package br.com.fiap.techchallengefase02.infrastructure.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter filter;
    @Mock
    private JwtService jwtService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class SemToken {
        @Test
        void deveContinuarFiltroQuandoNaoHaCabecalhoDeAutorizacao() throws Exception {
            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(jwtService, never()).isTokenValid(any());
        }

        @Test
        void deveContinuarFiltroQuandoCabecalhoNaoEhBearer() throws Exception {
            request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(jwtService, never()).isTokenValid(any());
        }
    }

    @Nested
    class ComTokenInvalido {
        @Test
        void naoDeveAutenticarQuandoTokenInvalido() throws Exception {
            request.addHeader("Authorization", "Bearer token-invalido");
            when(jwtService.isTokenValid("token-invalido")).thenReturn(false);

            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(jwtService, never()).extractLogin(any());
        }
    }

    @Nested
    class ComTokenValido {
        @Test
        void deveAutenticarQuandoTokenValido() throws Exception {
            request.addHeader("Authorization", "Bearer token-valido");
            when(jwtService.isTokenValid("token-valido")).thenReturn(true);
            when(jwtService.extractLogin("token-valido")).thenReturn("joao123");

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();
            assertThat(auth.getPrincipal()).isEqualTo("joao123");
            assertThat(auth.isAuthenticated()).isTrue();
        }

        @Test
        void naoDeveSubstituirAutenticacaoExistente() throws Exception {
            UsernamePasswordAuthenticationToken existente = new UsernamePasswordAuthenticationToken(
                    "usuario-existente", null, Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(existente);

            request.addHeader("Authorization", "Bearer token-valido");
            when(jwtService.isTokenValid("token-valido")).thenReturn(true);

            filter.doFilter(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .isEqualTo("usuario-existente");
            verify(jwtService, never()).extractLogin(any());
        }
    }
}
