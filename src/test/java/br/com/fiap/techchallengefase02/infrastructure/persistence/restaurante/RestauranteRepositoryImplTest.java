package br.com.fiap.techchallengefase02.infrastructure.persistence.restaurante;

import br.com.fiap.techchallengefase02.domain.entity.Restaurante;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestauranteRepositoryImplTest {

    @InjectMocks
    private RestauranteRepositoryImpl repository;
    @Mock
    private RestauranteJpaRepository jpaRepository;
    @Mock
    private RestauranteMapper mapper;
    @Test
    void deveRetornarRestauranteMapeadoQuandoIdExistir() {
        UUID id = UUID.randomUUID();
        RestauranteJpaEntity entidade = RestauranteJpaEntity.builder().id(id).nome("Cantina").build();
        Restaurante restaurante = Restaurante.builder().id(id).nome("Cantina").build();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entidade));
        when(mapper.toDomain(entidade)).thenReturn(restaurante);
        Optional<Restaurante> resultado = repository.buscarPorId(id);
        assertThat(resultado).contains(restaurante);
    }
    @Test
    void deveRetornarOptionalVazioQuandoIdNaoExistir() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());
        Optional<Restaurante> resultado = repository.buscarPorId(id);
        assertThat(resultado).isEmpty();
    }
}
