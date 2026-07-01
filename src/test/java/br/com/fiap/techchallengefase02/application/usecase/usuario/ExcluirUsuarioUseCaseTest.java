package br.com.fiap.techchallengefase02.application.usecase.usuario;

import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcluirUsuarioUseCaseTest {

    @InjectMocks
    private ExcluirUsuarioUseCase useCase;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void deveExcluirUsuarioQuandoExiste() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.existePorId(id)).thenReturn(true);

        useCase.executar(id);

        verify(usuarioRepository).excluirPorId(id);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.existePorId(id)).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Usuário não encontrado");

        verify(usuarioRepository, never()).excluirPorId(any());
    }
}
