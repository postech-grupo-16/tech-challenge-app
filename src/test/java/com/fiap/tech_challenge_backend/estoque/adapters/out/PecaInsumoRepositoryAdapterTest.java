package com.fiap.tech_challenge_backend.estoque.adapters.out;

import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import com.fiap.tech_challenge_backend.estoque.adapters.out.persistence.PecaInsumoJpaEntity;
import com.fiap.tech_challenge_backend.estoque.adapters.out.persistence.PecaInsumoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PecaInsumoRepositoryAdapter (estoque)")
class PecaInsumoRepositoryAdapterTest {

    @Mock
    private PecaInsumoRepository repository;

    @InjectMocks
    private PecaInsumoRepositoryAdapter adapter;

    private PecaInsumo peca;
    private PecaInsumoJpaEntity pecaEntity;
    private UUID pecaId;

    @BeforeEach
    void setUp() {
        pecaId = UUID.randomUUID();
        peca = PecaInsumo.builder()
                .id(pecaId)
                .nome("Pastilha de freio")
                .tipo(TipoPecaInsumo.PECA)
                .precoVenda(BigDecimal.valueOf(80))
                .precoCompra(BigDecimal.valueOf(50))
                .quantidadeEstoque(10)
                .quantidadeMinima(3)
                .build();
        pecaEntity = PecaInsumoJpaEntity.builder()
                .id(pecaId)
                .nome("Pastilha de freio")
                .tipo(TipoPecaInsumo.PECA)
                .precoVenda(BigDecimal.valueOf(80))
                .precoCompra(BigDecimal.valueOf(50))
                .quantidadeEstoque(10)
                .quantidadeMinima(3)
                .build();
    }

    @Test
    @DisplayName("buscarPorId deve delegar ao JpaRepository e mapear para domínio")
    void deveBuscarPorId() {
        when(repository.findById(pecaId)).thenReturn(Optional.of(pecaEntity));

        var resultado = adapter.buscarPorId(pecaId);

        assertThat(resultado).contains(peca);
        verify(repository).findById(pecaId);
    }

    @Test
    @DisplayName("salvar deve delegar ao JpaRepository e mapear para domínio")
    void deveSalvar() {
        when(repository.save(any(PecaInsumoJpaEntity.class))).thenReturn(pecaEntity);

        var resultado = adapter.salvar(peca);

        assertThat(resultado).isEqualTo(peca);
    }

    @Test
    @DisplayName("buscarTodos deve delegar ao JpaRepository e mapear para domínio")
    void deveBuscarTodos() {
        when(repository.findAll()).thenReturn(List.of(pecaEntity));

        var resultado = adapter.buscarTodos();

        assertThat(resultado).containsExactly(peca);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("buscarPorTipo deve delegar ao JpaRepository e mapear para domínio")
    void deveBuscarPorTipo() {
        when(repository.findByTipo(TipoPecaInsumo.PECA)).thenReturn(List.of(pecaEntity));

        var resultado = adapter.buscarPorTipo(TipoPecaInsumo.PECA);

        assertThat(resultado).containsExactly(peca);
        verify(repository).findByTipo(TipoPecaInsumo.PECA);
    }

    @Test
    @DisplayName("buscarAbaixoDoMinimo deve delegar ao JpaRepository e mapear para domínio")
    void deveBuscarAbaixoDoMinimo() {
        when(repository.findAbaixoDoMinimo()).thenReturn(List.of(pecaEntity));

        var resultado = adapter.buscarAbaixoDoMinimo();

        assertThat(resultado).containsExactly(peca);
        verify(repository).findAbaixoDoMinimo();
    }
}
