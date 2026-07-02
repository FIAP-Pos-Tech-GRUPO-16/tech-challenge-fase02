package br.com.fiap.techchallengefase02.application.usecase.restaurante;

import br.com.fiap.techchallengefase02.application.dto.CriarRestauranteRequest;
import br.com.fiap.techchallengefase02.application.dto.EnderecoDTO;
import br.com.fiap.techchallengefase02.application.dto.RestauranteResponse;
import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import br.com.fiap.techchallengefase02.domain.repository.RestauranteRepository;
import br.com.fiap.techchallengefase02.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarRestauranteUseCaseTest {

    @InjectMocks
    private CriarRestauranteUseCase useCase;
    @Mock
    private RestauranteRepository restauranteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Nested
    class CriarComSucesso {

        @Test
        void deveCriarRestauranteQuandoDonoExistir() {
            UUID donoId = UUID.randomUUID();
            UUID restauranteId = UUID.randomUUID();
            CriarRestauranteRequest request = request(donoId);
            Restaurante restauranteSalvo = Restaurante.builder()
                    .id(restauranteId)
                    .nome("Cantina da Fiap")
                    .endereco(endereco())
                    .tipoCozinha("Italiana")
                    .horarioFuncionamento("Segunda a sexta")
                    .donoId(donoId)
                    .build();
            when(usuarioRepository.existePorId(donoId)).thenReturn(true);
            when(restauranteRepository.salvar(any(Restaurante.class))).thenReturn(restauranteSalvo);
            RestauranteResponse response = useCase.executar(request);
            assertThat(response.id()).isEqualTo(restauranteId);
            assertThat(response.nome()).isEqualTo("Cantina da Fiap");
            assertThat(response.donoId()).isEqualTo(donoId);
        }
        @Test
        void deveRemoverEspacosEmBrancoAntesDeSalvar() {
            UUID donoId = UUID.randomUUID();
            CriarRestauranteRequest request = new CriarRestauranteRequest(
                    "  Cantina da Fiap  ",
                    new EnderecoDTO("  Rua das Flores  ", "123", "  Sao Paulo  ", "01310100"),
                    "  Italiana  ",
                    "  Segunda a sexta  ",
                    donoId
            );
            Restaurante restauranteSalvo = Restaurante.builder().id(UUID.randomUUID()).nome("Cantina da Fiap").endereco(endereco()).tipoCozinha("Italiana").horarioFuncionamento("Segunda a sexta").donoId(donoId).build();
            ArgumentCaptor<Restaurante> captor = ArgumentCaptor.forClass(Restaurante.class);
            when(usuarioRepository.existePorId(donoId)).thenReturn(true);
            when(restauranteRepository.salvar(any(Restaurante.class))).thenReturn(restauranteSalvo);
            useCase.executar(request);
            verify(restauranteRepository).salvar(captor.capture());
            Restaurante restaurante = captor.getValue();
            assertThat(restaurante.getNome()).isEqualTo("Cantina da Fiap");
            assertThat(restaurante.getEndereco().getRua()).isEqualTo("Rua das Flores");
            assertThat(restaurante.getTipoCozinha()).isEqualTo("Italiana");
            assertThat(restaurante.getHorarioFuncionamento()).isEqualTo("Segunda a sexta");
        }
    }
    @Nested
    class ValidacoesDeErro {

        @Test
        void deveLancarExcecaoQuandoDonoNaoExistir() {
            UUID donoId = UUID.randomUUID();
            when(usuarioRepository.existePorId(donoId)).thenReturn(false);
            assertThatThrownBy(() -> useCase.executar(request(donoId)))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Dono do restaurante nao encontrado");
            verify(restauranteRepository, never()).salvar(any());
        }
    }

    private CriarRestauranteRequest request(UUID donoId) {
        return new CriarRestauranteRequest("Cantina da Fiap", new EnderecoDTO("Rua das Flores", "123", "Sao Paulo", "01310100"), "Italiana", "Segunda a sexta", donoId);
    }

    private Endereco endereco() {
        return Endereco.builder().rua("Rua das Flores").numero("123").cidade("Sao Paulo").cep("01310100").build();
    }
}
