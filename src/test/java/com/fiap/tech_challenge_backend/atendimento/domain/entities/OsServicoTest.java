package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OsServico - Testes Unitários")
class OsServicoTest {

    private OsServico osServico;
    private OsOrcamento orcamento;
    private ServicoCatalogo servico;

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

        servico = ServicoCatalogo.builder()
                .id(UUID.randomUUID())
                .nome("Troca de Óleo")
                .precoMaoDeObra(BigDecimal.valueOf(100))
                .categoria("PREVENTIVA")
                .build();

        osServico = OsServico.builder()
                .id(UUID.randomUUID())
                .orcamento(orcamento)
                .servico(servico)
                .precoMaoDeObraAplicado(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    @DisplayName("Deve criar OsServico corretamente")
    void testCriarOsServico() {
        assertNotNull(osServico);
        assertEquals(orcamento, osServico.getOrcamento());
        assertEquals(servico, osServico.getServico());
        assertEquals(BigDecimal.valueOf(100), osServico.getPrecoMaoDeObraAplicado());
    }

    @Test
    @DisplayName("Deve ter ID gerado automaticamente")
    void testIdGerado() {
        assertNotNull(osServico.getId());
    }

    @Test
    @DisplayName("Deve permitir atualizar preço de mão de obra aplicado")
    void testAtualizarPreco() {
        osServico.setPrecoMaoDeObraAplicado(BigDecimal.valueOf(150));
        assertEquals(BigDecimal.valueOf(150), osServico.getPrecoMaoDeObraAplicado());
    }

    @Test
    @DisplayName("Deve permitir preço zero")
    void testPrecoZero() {
        osServico.setPrecoMaoDeObraAplicado(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, osServico.getPrecoMaoDeObraAplicado());
    }

    @Test
    @DisplayName("Deve manter referência do orçamento")
    void testReferenciaOrcamento() {
        assertEquals(orcamento.getId(), osServico.getOrcamento().getId());
    }

    @Test
    @DisplayName("Deve manter referência do serviço")
    void testReferenciaServico() {
        assertEquals(servico.getId(), osServico.getServico().getId());
    }

    @Test
    @DisplayName("Deve permitir ordensServicoId null")
    void testOrdenServicoIdNull() {
        osServico.setOrdemServicoId(null);
        assertNull(osServico.getOrdemServicoId());
    }

    @Test
    @DisplayName("Deve permitir atualizar ordensServicoId")
    void testAtualizarOrdenServicoId() {
        osServico.setOrdemServicoId(1L);
        assertEquals(1L, osServico.getOrdemServicoId());
    }

    @Test
    @DisplayName("Deve ter toString válido")
    void testToString() {
        assertNotNull(osServico.toString());
    }

    @Test
    @DisplayName("Deve ter hash code consistente")
    void testHashCode() {
        assertEquals(osServico.hashCode(), osServico.hashCode());
    }

    @Test
    @DisplayName("Deve aceitar preço com decimais")
    void testPrecoComDecimais() {
        osServico.setPrecoMaoDeObraAplicado(BigDecimal.valueOf(123.45));
        assertEquals(BigDecimal.valueOf(123.45), osServico.getPrecoMaoDeObraAplicado());
    }
}
