package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.application.dto.AtualizarRestauranteRequest;
import br.com.fiap.techchallengefase02.application.dto.EnderecoDTO;
import br.com.fiap.techchallengefase02.application.dto.RestauranteResponse;
import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarRestauranteUseCaseTest {

    @InjectMocks
    private AtualizarRestauranteUseCase useCase;
    @Mock
    private RestauranteRepository restauranteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Nested
    class AtualizarComSucesso {

        @Test
        void deveAtualizarRestauranteQuandoRestauranteEDonoExistirem() {
            UUID id = UUID.randomUUID();
            UUID donoId = UUID.randomUUID();
            Restaurante existente = Restaurante.builder().id(id).nome("Antigo").endereco(endereco()).tipoCozinha("Brasileira").horarioFuncionamento("Almoco").donoId(donoId).build();
            Restaurante atualizado = Restaurante.builder().id(id).nome("Novo").endereco(endereco()).tipoCozinha("Italiana").horarioFuncionamento("Jantar").donoId(donoId).build();
            when(restauranteRepository.buscarPorId(id)).thenReturn(Optional.of(existente));
            when(usuarioRepository.existePorId(donoId)).thenReturn(true);
            when(restauranteRepository.salvar(any(Restaurante.class))).thenReturn(atualizado);
            RestauranteResponse response = useCase.executar(id, request(donoId));
            assertThat(response.nome()).isEqualTo("Novo");
            assertThat(response.tipoCozinha()).isEqualTo("Italiana");
            assertThat(response.horarioFuncionamento()).isEqualTo("Jantar");
        }
    }
    @Nested
    class ValidacoesDeErro {

        @Test
        void deveLancarExcecaoQuandoRestauranteNaoExistir() {
            UUID id = UUID.randomUUID();
            UUID donoId = UUID.randomUUID();
            when(restauranteRepository.buscarPorId(id)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> useCase.executar(id, request(donoId)))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Restaurante não encontrado");
            verify(restauranteRepository, never()).salvar(any());
        }
        @Test
        void deveLancarExcecaoQuandoDonoNaoExistir() {
            UUID id = UUID.randomUUID();
            UUID donoId = UUID.randomUUID();
            Restaurante existente = Restaurante.builder().id(id).nome("Antigo").endereco(endereco()).tipoCozinha("Brasileira").horarioFuncionamento("Almoco").donoId(UUID.randomUUID()).build();
            when(restauranteRepository.buscarPorId(id)).thenReturn(Optional.of(existente));
            when(usuarioRepository.existePorId(donoId)).thenReturn(false);
            assertThatThrownBy(() -> useCase.executar(id, request(donoId)))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Dono do restaurante não encontrado");
            verify(restauranteRepository, never()).salvar(any());
        }
    }

    private AtualizarRestauranteRequest request(UUID donoId) {
        return new AtualizarRestauranteRequest("Novo", new EnderecoDTO("Rua Nova", "456", "Sao Paulo", "01310100"), "Italiana", "Jantar", donoId);
    }

    private Endereco endereco() {
        return Endereco.builder().rua("Rua").numero("123").cidade("Sao Paulo").cep("01310100").build();
    }
}
