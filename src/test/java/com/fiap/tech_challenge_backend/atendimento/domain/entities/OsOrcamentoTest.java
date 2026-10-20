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

@DisplayName("OsOrcamento - Testes Unitários")
class OsOrcamentoTest {

    private OsOrcamento orcamento;
    private OrdemServico ordemServico;

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

        ordemServico = OrdemServico.builder()
                .id(1L)
                .clienteId(cliente.getId())
                .veiculoId(veiculo.getId())
                .status(StatusOrdemServico.RECEBIDA)
                .queixaCliente("Problema no motor")
                .dataCriacao(LocalDateTime.now())
                .build();

        orcamento = OsOrcamento.builder()
                .id(1L)
                .tipo(TipoOrcamento.INICIAL)
                .status(StatusOrcamento.PENDENTE)
                .valorTotal(BigDecimal.ZERO)
                .ordemServico(ordemServico)
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar orçamento com status padrão PENDENTE")
    void testCriarOrcamentoComStatusPadrao() {
        assertNotNull(orcamento);
        assertEquals(StatusOrcamento.PENDENTE, orcamento.getStatus());
        assertEquals(BigDecimal.ZERO, orcamento.getValorTotal());
    }

    @Test
    @DisplayName("Deve calcular total com serviços e peças")
    void testCalcularTotalComServicosEPecas() {
        ServicoCatalogo servico = ServicoCatalogo.builder()
                .id(UUID.randomUUID())
                .nome("Troca de Óleo")
                .precoMaoDeObra(BigDecimal.valueOf(100))
                .categoria("PREVENTIVA")
                .build();

        OsServico osServico = OsServico.builder()
                .id(UUID.randomUUID())
                .orcamento(orcamento)
                .servico(servico)
                .precoMaoDeObraAplicado(BigDecimal.valueOf(100))
                .build();
        orcamento.adicionarServico(osServico);

        orcamento.calcularTotal();

        assertEquals(BigDecimal.valueOf(100), orcamento.getValorTotal());
    }

    @Test
    @DisplayName("Deve calcular total com múltiplos serviços")
    void testCalcularTotalComMultiplosServicos() {
        ServicoCatalogo servico1 = ServicoCatalogo.builder()
                .id(UUID.randomUUID())
                .nome("Troca de Óleo")
                .precoMaoDeObra(BigDecimal.valueOf(100))
                .categoria("PREVENTIVA")
                .build();

        ServicoCatalogo servico2 = ServicoCatalogo.builder()
                .id(UUID.randomUUID())
                .nome("Alinhamento")
                .precoMaoDeObra(BigDecimal.valueOf(150))
                .categoria("PREVENTIVA")
                .build();

        OsServico osServico1 = OsServico.builder()
                .id(UUID.randomUUID())
                .orcamento(orcamento)
                .servico(servico1)
                .precoMaoDeObraAplicado(BigDecimal.valueOf(100))
                .build();

        OsServico osServico2 = OsServico.builder()
                .id(UUID.randomUUID())
                .orcamento(orcamento)
                .servico(servico2)
                .precoMaoDeObraAplicado(BigDecimal.valueOf(150))
                .build();

        orcamento.adicionarServico(osServico1);
        orcamento.adicionarServico(osServico2);

        orcamento.calcularTotal();

        assertEquals(BigDecimal.valueOf(250), orcamento.getValorTotal());
    }

    @Test
    @DisplayName("Deve calcular total com peças")
    void testCalcularTotalComPecas() {
        com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo peca =
                com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo.builder()
                .id(UUID.randomUUID())
                .nome("Filtro de Óleo")
                .precoVenda(BigDecimal.valueOf(50))
                .build();

        OsPeca osPeca = OsPeca.builder()
                .id(UUID.randomUUID())
                .orcamento(orcamento)
                .pecaInsumoId(peca.getId())
                .quantidade(2)
                .precoVendaAplicado(BigDecimal.valueOf(50))
                .build();

        orcamento.adicionarPeca(osPeca);
        orcamento.calcularTotal();

        assertEquals(BigDecimal.valueOf(100), orcamento.getValorTotal());
    }

    @Test
    @DisplayName("Deve calcular total com serviços e peças combinados")
    void testCalcularTotalComServicosEPecasCombinados() {
        ServicoCatalogo servico = ServicoCatalogo.builder()
                .id(UUID.randomUUID())
                .nome("Troca de Óleo")
                .precoMaoDeObra(BigDecimal.valueOf(100))
                .categoria("PREVENTIVA")
                .build();

        OsServico osServico = OsServico.builder()
                .id(UUID.randomUUID())
                .orcamento(orcamento)
                .servico(servico)
                .precoMaoDeObraAplicado(BigDecimal.valueOf(100))
                .build();

        com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo peca =
                com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo.builder()
                .id(UUID.randomUUID())
                .nome("Filtro de Óleo")
                .precoVenda(BigDecimal.valueOf(50))
                .build();

        OsPeca osPeca = OsPeca.builder()
                .id(UUID.randomUUID())
                .orcamento(orcamento)
                .pecaInsumoId(peca.getId())
                .quantidade(1)
                .precoVendaAplicado(BigDecimal.valueOf(50))
                .build();

        orcamento.adicionarServico(osServico);
        orcamento.adicionarPeca(osPeca);

        orcamento.calcularTotal();

        assertEquals(BigDecimal.valueOf(150), orcamento.getValorTotal());
    }

    @Test
    @DisplayName("Deve retornar zero quando não há serviços ou peças")
    void testCalcularTotalVazio() {
        orcamento.calcularTotal();

        assertEquals(BigDecimal.ZERO, orcamento.getValorTotal());
    }

    @Test
    @DisplayName("Deve mudar status para APROVADO")
    void testAprovar() {
        orcamento.aprovar();

        assertEquals(StatusOrcamento.APROVADO, orcamento.getStatus());
    }

    @Test
    @DisplayName("Deve mudar status para REJEITADO")
    void testRejeitar() {
        orcamento.rejeitar();

        assertEquals(StatusOrcamento.REJEITADO, orcamento.getStatus());
    }

    @Test
    @DisplayName("Deve adicionar serviço ao orçamento")
    void testAdicionarServico() {
        ServicoCatalogo servico = ServicoCatalogo.builder()
                .id(UUID.randomUUID())
                .nome("Troca de Óleo")
                .precoMaoDeObra(BigDecimal.valueOf(100))
                .categoria("PREVENTIVA")
                .build();

        OsServico osServico = OsServico.builder()
                .id(UUID.randomUUID())
                .servico(servico)
                .precoMaoDeObraAplicado(BigDecimal.valueOf(100))
                .build();

        orcamento.adicionarServico(osServico);

        assertEquals(1, orcamento.getServicos().size());
        assertEquals(orcamento, osServico.getOrcamento());
    }

    @Test
    @DisplayName("Deve adicionar peça ao orçamento")
    void testAdicionarPeca() {
        com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo peca =
                com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo.builder()
                .id(UUID.randomUUID())
                .nome("Filtro de Óleo")
                .precoVenda(BigDecimal.valueOf(50))
                .build();

        OsPeca osPeca = OsPeca.builder()
                .id(UUID.randomUUID())
                .pecaInsumoId(peca.getId())
                .quantidade(1)
                .precoVendaAplicado(BigDecimal.valueOf(50))
                .build();

        orcamento.adicionarPeca(osPeca);

        assertEquals(1, orcamento.getPecas().size());
        assertEquals(orcamento, osPeca.getOrcamento());
    }
}
