package br.com.fiap.techchallengefase02.infrastructure.security;

import br.com.fiap.techchallengefase02.application.port.GeradorDeToken;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Implementação de {@link GeradorDeToken} baseada em JWT (JJWT).
 *
 * A claim de tipo de usuário deixou de ser {@code userType} (enum, que não
 * existe mais) e passou a ser {@code tipoUsuarioId} (UUID como string),
 * refletindo a associação por ID entre Usuario e TipoUsuario. Autorização
 * por tipo/roles não faz parte deste escopo.
 */
@Service
public class JwtService implements GeradorDeToken {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Override
    public String gerar(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getLogin())
                .claim("usuarioId", usuario.getId().toString())
                .claim("tipoUsuarioId", usuario.getTipoUsuarioId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractLogin(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
