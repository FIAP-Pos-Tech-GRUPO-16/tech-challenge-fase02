package br.com.fiap.techchallengefase02.config;


import br.com.fiap.techchallengefase02.security.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public JwtService jwtService() {
        return mock(JwtService.class);
    }
}
