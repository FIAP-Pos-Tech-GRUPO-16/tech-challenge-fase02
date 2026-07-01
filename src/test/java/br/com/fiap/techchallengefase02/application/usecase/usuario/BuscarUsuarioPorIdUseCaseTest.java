package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.application.dto.UsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Usuario;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarUsuarioPorIdUseCaseTest {

    @InjectMocks
    private BuscarUsuarioPorIdUseCase useCase;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarUsuarioQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        Usuario usuario = Usuario.builder()
                .id(id)
                .nome("João Silva")
                .email("joao@email.com")
                .login("joao123")
                .senha("hashed")
                .endereco(new Endereco("Rua", "1", "Cidade", "12345678"))
                .tipoUsuarioId(UUID.randomUUID())
                .dataUltimaAlteracao(LocalDateTime.now())
                .build();

        when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.of(usuario));

        UsuarioResponse response = useCase.executar(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.nome()).isEqualTo("João Silva");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Usuário não encontrado");
    }
}
