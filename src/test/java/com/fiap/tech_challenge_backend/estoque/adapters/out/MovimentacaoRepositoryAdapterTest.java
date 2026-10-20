package com.fiap.tech_challenge_backend.estoque.adapters.out;

import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import com.fiap.tech_challenge_backend.estoque.adapters.out.persistence.MovimentacaoEstoqueJpaEntity;
import com.fiap.tech_challenge_backend.estoque.adapters.out.persistence.MovimentacaoRepository;
import com.fiap.tech_challenge_backend.estoque.adapters.out.persistence.PecaInsumoJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovimentacaoRepositoryAdapter (estoque)")
class MovimentacaoRepositoryAdapterTest {

    @Mock
    private MovimentacaoRepository repository;

    @InjectMocks
    private MovimentacaoRepositoryAdapter adapter;

    private UUID pecaId;
    private PecaInsumo peca;
    private MovimentacaoEstoque movimentacao;
    private MovimentacaoEstoqueJpaEntity movimentacaoEntity;

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

        PecaInsumoJpaEntity pecaEntity = PecaInsumoJpaEntity.builder()
                .id(pecaId)
                .nome("Pastilha de freio")
                .tipo(TipoPecaInsumo.PECA)
                .precoVenda(BigDecimal.valueOf(80))
                .precoCompra(BigDecimal.valueOf(50))
                .quantidadeEstoque(10)
                .quantidadeMinima(3)
                .build();

        UUID movId = UUID.randomUUID();
        movimentacao = MovimentacaoEstoque.builder()
                .id(movId)
                .pecaInsumo(peca)
                .tipoMovimentacao(TipoMovimentacao.ENTRADA)
                .quantidade(5)
                .build();

        movimentacaoEntity = MovimentacaoEstoqueJpaEntity.builder()
                .id(movId)
                .pecaInsumo(pecaEntity)
                .tipoMovimentacao(TipoMovimentacao.ENTRADA)
                .quantidade(5)
                .build();
    }

    @Test
    @DisplayName("salvar deve delegar ao JpaRepository e mapear para domínio")
    void deveSalvar() {
        when(repository.save(any(MovimentacaoEstoqueJpaEntity.class))).thenReturn(movimentacaoEntity);

        var resultado = adapter.salvar(movimentacao);

        assertThat(resultado).isEqualTo(movimentacao);
    }

    @Test
    @DisplayName("buscarPorPecaInsumoOrdenadoPorDataDesc deve delegar ao JpaRepository e mapear para domínio")
    void deveBuscarPorPecaInsumoOrdenado() {
        when(repository.findByPecaInsumoIdOrderByCriadoEmDesc(pecaId)).thenReturn(List.of(movimentacaoEntity));

        var resultado = adapter.buscarPorPecaInsumoOrdenadoPorDataDesc(peca);

        assertThat(resultado).containsExactly(movimentacao);
        verify(repository).findByPecaInsumoIdOrderByCriadoEmDesc(pecaId);
    }
}
