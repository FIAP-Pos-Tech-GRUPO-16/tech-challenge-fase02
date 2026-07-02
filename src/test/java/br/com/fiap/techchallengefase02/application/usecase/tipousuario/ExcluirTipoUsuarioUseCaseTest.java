package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
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
class ExcluirTipoUsuarioUseCaseTest {

    @InjectMocks
    private ExcluirTipoUsuarioUseCase useCase;
    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Test
    void deveExcluirTipoUsuarioQuandoExiste() {
        UUID id = UUID.randomUUID();
        when(tipoUsuarioRepository.existePorId(id)).thenReturn(true);

        useCase.executar(id);

        verify(tipoUsuarioRepository).excluirPorId(id);
    }

    @Test
    void deveLancarExcecaoQuandoTipoUsuarioNaoExiste() {
        UUID id = UUID.randomUUID();
        when(tipoUsuarioRepository.existePorId(id)).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Tipo de usuário não encontrado");

        verify(tipoUsuarioRepository, never()).excluirPorId(any());
    }
}
