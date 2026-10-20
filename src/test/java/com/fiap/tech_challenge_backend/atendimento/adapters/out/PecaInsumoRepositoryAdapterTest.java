package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.estoque.application.ports.out.PecaInsumoRepositoryPort;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PecaInsumoRepositoryAdapter")
class PecaInsumoRepositoryAdapterTest {

    private PecaInsumoRepositoryAdapter adapter;

    @Mock
    private PecaInsumoRepositoryPort repository;

    private PecaInsumo pecaInsumo;
    private UUID pecaId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new PecaInsumoRepositoryAdapter(repository);

        pecaId = UUID.randomUUID();
        pecaInsumo = PecaInsumo.builder()
                .id(pecaId)
                .tipo(TipoPecaInsumo.PECA)
                .nome("Pneu")
                .descricao("Pneu radial para carro")
                .precoVenda(BigDecimal.valueOf(250.00))
                .precoCompra(BigDecimal.valueOf(150.00))
                .quantidadeEstoque(50)
                .quantidadeMinima(10)
                .build();
    }

    @Test
    @DisplayName("Deve buscar peça/insumo por id")
    void testBuscarPorId() {
        when(repository.buscarPorId(pecaId)).thenReturn(Optional.of(pecaInsumo));

        Optional<PecaInsumo> resultado = adapter.buscarPorId(pecaId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(pecaId);
        assertThat(resultado.get().getNome()).isEqualTo("Pneu");
        assertThat(resultado.get().getTipo()).isEqualTo(TipoPecaInsumo.PECA);
        verify(repository, times(1)).buscarPorId(pecaId);
    }

    @Test
    @DisplayName("Deve retornar vazio quando peça/insumo não existe")
    void testBuscarPorIdNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(repository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        Optional<PecaInsumo> resultado = adapter.buscarPorId(idInexistente);

        assertThat(resultado).isEmpty();
        verify(repository, times(1)).buscarPorId(idInexistente);
    }

    @Test
    @DisplayName("Deve buscar insumo por id")
    void testBuscarInsumo() {
        PecaInsumo insumo = PecaInsumo.builder()
                .id(pecaId)
                .tipo(TipoPecaInsumo.INSUMO)
                .nome("Óleo Motor")
                .descricao("Óleo sintético para motor")
                .precoVenda(BigDecimal.valueOf(50.00))
                .precoCompra(BigDecimal.valueOf(30.00))
                .quantidadeEstoque(100)
                .quantidadeMinima(20)
                .build();

        when(repository.buscarPorId(pecaId)).thenReturn(Optional.of(insumo));

        Optional<PecaInsumo> resultado = adapter.buscarPorId(pecaId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTipo()).isEqualTo(TipoPecaInsumo.INSUMO);
        assertThat(resultado.get().getNome()).isEqualTo("Óleo Motor");
        verify(repository, times(1)).buscarPorId(pecaId);
    }

    @Test
    @DisplayName("Deve buscar peça com quantidade em estoque")
    void testBuscarPecaComEstoque() {
        when(repository.buscarPorId(pecaId)).thenReturn(Optional.of(pecaInsumo));

        Optional<PecaInsumo> resultado = adapter.buscarPorId(pecaId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getQuantidadeEstoque()).isEqualTo(50);
        assertThat(resultado.get().getQuantidadeMinima()).isEqualTo(10);
        verify(repository, times(1)).buscarPorId(pecaId);
    }

    @Test
    @DisplayName("Deve validar preços da peça/insumo")
    void testBuscarPecaComPrecos() {
        when(repository.buscarPorId(pecaId)).thenReturn(Optional.of(pecaInsumo));

        Optional<PecaInsumo> resultado = adapter.buscarPorId(pecaId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getPrecoVenda()).isEqualByComparingTo(BigDecimal.valueOf(250.00));
        assertThat(resultado.get().getPrecoCompra()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        verify(repository, times(1)).buscarPorId(pecaId);
    }
}
