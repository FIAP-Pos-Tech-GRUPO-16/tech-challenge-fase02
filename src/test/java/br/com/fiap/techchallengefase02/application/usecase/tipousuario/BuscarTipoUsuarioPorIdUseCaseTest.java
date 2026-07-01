package br.com.fiap.techchallengefase02.application.usecase.tipousuario;

import br.com.fiap.techchallengefase02.application.dto.TipoUsuarioResponse;
import br.com.fiap.techchallengefase02.domain.entity.TipoUsuario;
import br.com.fiap.techchallengefase02.domain.repository.TipoUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarTipoUsuarioPorIdUseCaseTest {

    @InjectMocks
    private BuscarTipoUsuarioPorIdUseCase useCase;
    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Test
    void deveRetornarTipoUsuarioQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        TipoUsuario tipoUsuario = TipoUsuario.builder().id(id).nome("Cliente").build();

        when(tipoUsuarioRepository.buscarPorId(id)).thenReturn(Optional.of(tipoUsuario));

        TipoUsuarioResponse response = useCase.executar(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.nome()).isEqualTo("Cliente");
    }

    @Test
    void deveLancarExcecaoQuandoTipoUsuarioNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(tipoUsuarioRepository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Tipo de usuário não encontrado");
    }
}
