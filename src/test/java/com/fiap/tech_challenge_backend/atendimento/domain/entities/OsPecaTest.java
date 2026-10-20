package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OsPeca - Testes Unitários")
class OsPecaTest {

    private OsPeca osPeca;
    private OsOrcamento orcamento;
    private PecaInsumo peca;

    @BeforeEach
    void setUp() {
        Cliente cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .build();

        Veiculo veiculo = Veiculo.builder()
                .id(UUID.randomUUID())
                .placa(new Placa("ABC1234"))
                .marca("Ford")
                .modelo("Fiesta")
                .ano(2020)
                .cor("Branco")
                .build();

        OrdemServico ordemServico = OrdemServico.builder()
                .id(1L)
                .clienteId(cliente.getId())
                .veiculoId(veiculo.getId())
                .status(StatusOrdemServico.RECEBIDA)
                .queixaCliente("Problema")
                .dataCriacao(LocalDateTime.now())
                .build();

        orcamento = OsOrcamento.builder()
                .id(1L)
                .tipo(TipoOrcamento.INICIAL)
                .status(StatusOrcamento.PENDENTE)
                .ordemServico(ordemServico)
                .dataCriacao(LocalDateTime.now())
                .build();

        peca = PecaInsumo.builder()
                .id(UUID.randomUUID())
                .nome("Filtro de Óleo")
                .precoVenda(BigDecimal.valueOf(50))
                .build();

        osPeca = OsPeca.builder()
                .id(UUID.randomUUID())
                .orcamento(orcamento)
                .pecaInsumoId(peca.getId())
                .quantidade(2)
                .precoVendaAplicado(BigDecimal.valueOf(50))
                .build();
    }

    @Test
    @DisplayName("Deve criar OsPeca corretamente")
    void testCriarOsPeca() {
        assertNotNull(osPeca);
        assertEquals(orcamento, osPeca.getOrcamento());
        assertEquals(peca.getId(), osPeca.getPecaInsumoId());
        assertEquals(2, osPeca.getQuantidade());
        assertEquals(BigDecimal.valueOf(50), osPeca.getPrecoVendaAplicado());
    }

    @Test
    @DisplayName("Deve ter ID gerado automaticamente")
    void testIdGerado() {
        assertNotNull(osPeca.getId());
    }

    @Test
    @DisplayName("Deve permitir atualizar quantidade")
    void testAtualizarQuantidade() {
        osPeca.setQuantidade(5);
        assertEquals(5, osPeca.getQuantidade());
    }

    @Test
    @DisplayName("Deve permitir quantidade 1")
    void testQuantidadeUm() {
        osPeca.setQuantidade(1);
        assertEquals(1, osPeca.getQuantidade());
    }

    @Test
    @DisplayName("Deve permitir quantidade grande")
    void testQuantidadeGrande() {
        osPeca.setQuantidade(1000);
        assertEquals(1000, osPeca.getQuantidade());
    }

    @Test
    @DisplayName("Deve permitir atualizar preço de venda aplicado")
    void testAtualizarPreco() {
        osPeca.setPrecoVendaAplicado(BigDecimal.valueOf(75));
        assertEquals(BigDecimal.valueOf(75), osPeca.getPrecoVendaAplicado());
    }

    @Test
    @DisplayName("Deve permitir preço zero")
    void testPrecoZero() {
        osPeca.setPrecoVendaAplicado(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, osPeca.getPrecoVendaAplicado());
    }

    @Test
    @DisplayName("Deve manter referência do orçamento")
    void testReferenciaOrcamento() {
        assertEquals(orcamento.getId(), osPeca.getOrcamento().getId());
    }

    @Test
    @DisplayName("Deve manter referência da peça")
    void testReferenciaPeca() {
        assertEquals(peca.getId(), osPeca.getPecaInsumoId());
    }

    @Test
    @DisplayName("Deve permitir ordensServicoId null")
    void testOrdenServicoIdNull() {
        osPeca.setOrdemServicoId(null);
        assertNull(osPeca.getOrdemServicoId());
    }

    @Test
    @DisplayName("Deve permitir atualizar ordensServicoId")
    void testAtualizarOrdenServicoId() {
        osPeca.setOrdemServicoId(1L);
        assertEquals(1L, osPeca.getOrdemServicoId());
    }

    @Test
    @DisplayName("Deve ter toString válido")
    void testToString() {
        assertNotNull(osPeca.toString());
    }

    @Test
    @DisplayName("Deve ter hash code consistente")
    void testHashCode() {
        assertEquals(osPeca.hashCode(), osPeca.hashCode());
    }

    @Test
    @DisplayName("Deve aceitar preço com decimais")
    void testPrecoComDecimais() {
        osPeca.setPrecoVendaAplicado(BigDecimal.valueOf(123.45));
        assertEquals(BigDecimal.valueOf(123.45), osPeca.getPrecoVendaAplicado());
    }

    @Test
    @DisplayName("Deve calcular valor total da peça")
    void testCalcularValorTotalPeca() {
        BigDecimal valorTotal = osPeca.getPrecoVendaAplicado().multiply(BigDecimal.valueOf(osPeca.getQuantidade()));
        assertEquals(BigDecimal.valueOf(100), valorTotal);
    }

    @Test
    @DisplayName("Deve calcular valor total com múltiplas quantidades")
    void testCalcularValorTotalMultiplas() {
        osPeca.setQuantidade(10);
        osPeca.setPrecoVendaAplicado(BigDecimal.valueOf(25));

        BigDecimal valorTotal = osPeca.getPrecoVendaAplicado().multiply(BigDecimal.valueOf(osPeca.getQuantidade()));
        assertEquals(BigDecimal.valueOf(250), valorTotal);
    }
}
