package br.com.fiap.techchallengefase02.infrastructure.persistence.itemcardapio;

import br.com.fiap.techchallengefase02.domain.entity.ItemCardapio;
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
class ItemCardapioRepositoryImplTest {

    @InjectMocks
    private ItemCardapioRepositoryImpl repository;
    @Mock
    private ItemCardapioJpaRepository jpaRepository;
    @Mock
    private ItemCardapioMapper mapper;

    @Test
    void deveRetornarItemMapeadoQuandoIdExistir() {
        UUID id = UUID.randomUUID();
        ItemCardapioJpaEntity entidade = ItemCardapioJpaEntity.builder().id(id).nome("Lasanha").build();
        ItemCardapio item = ItemCardapio.builder().id(id).nome("Lasanha").build();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entidade));
        when(mapper.toDomain(entidade)).thenReturn(item);

        Optional<ItemCardapio> resultado = repository.buscarPorId(id);

        assertThat(resultado).contains(item);
    }

    @Test
    void deveRetornarOptionalVazioQuandoIdNaoExistir() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<ItemCardapio> resultado = repository.buscarPorId(id);

        assertThat(resultado).isEmpty();
    }
}
