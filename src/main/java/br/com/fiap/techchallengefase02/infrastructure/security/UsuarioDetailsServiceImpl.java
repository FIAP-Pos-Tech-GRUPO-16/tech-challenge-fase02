package br.com.fiap.techchallengefase02.infrastructure.security;

import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementação de {@link UserDetailsService} do Spring Security, detalhe
 * de framework isolado na infraestrutura. Depende apenas do contrato de
 * domínio {@link UsuarioRepository}.
 *
 * Não atribui nenhuma authority/role — controle de acesso por tipo de
 * usuário não é escopo desta etapa.
 */
@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.buscarPorLogin(login.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + login));

        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getLogin())
                .password(usuario.getSenha())
                .authorities(Collections.emptyList())
                .build();
    }
}
