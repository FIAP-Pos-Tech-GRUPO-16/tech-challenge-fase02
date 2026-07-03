package br.com.fiap.techchallengefase02.infrastructure.persistence.restaurante;

import br.com.fiap.techchallengefase02.domain.entity.Endereco;
import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RestauranteMapperTest {

    private final RestauranteMapper mapper = new RestauranteMapperImpl();
    @Test
    void deveRetornarNuloQuandoEntidadeJpaForNula() {
        assertThat(mapper.toDomain(null)).isNull();
    }
    @Test
    void deveRetornarNuloQuandoRestauranteDeDominioForNulo() {
        assertThat(mapper.toJpaEntity(null)).isNull();
    }
    @Test
    void deveConverterDominioParaJpa() {
        UUID id = UUID.randomUUID();
        Restaurante restaurante = Restaurante.builder()
                .id(id)
                .nome("Cantina")
                .endereco(Endereco.builder().rua("Rua").numero("1").cidade("Sao Paulo").cep("01310100").build())
                .tipoCozinha("Italiana")
                .horarioFuncionamento("Almoco")
                .donoId(UUID.randomUUID())
                .build();
        RestauranteJpaEntity entity = mapper.toJpaEntity(restaurante);
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEndereco().getRua()).isEqualTo("Rua");
        assertThat(entity.getTipoCozinha()).isEqualTo("Italiana");
    }
}
