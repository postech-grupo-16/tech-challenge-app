package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.*;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RelatorioEnriquecimentoService")
class RelatorioEnriquecimentoServiceTest {

    @Mock
    private OrdemServicoEnriquecimentoService enriquecimentoService;

    private RelatorioEnriquecimentoService service;

    @BeforeEach
    void setUp() {
        service = new RelatorioEnriquecimentoService(enriquecimentoService);
    }

    @Test
    @DisplayName("Deve enriquecer relatório base sem expands")
    void testEnriquecerSemExpands() {
        Map<String, String> tempoPorStatus = new HashMap<>();
        tempoPorStatus.put("RECEBIDA", "2h");

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "João Silva",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .status(com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.EM_DIAGNOSTICO)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, null);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.clienteNome()).isEqualTo("João Silva");
        assertThat(resultado.valorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resultado.cliente()).isNull();
        assertThat(resultado.veiculo()).isNull();
        assertThat(resultado.mecanico()).isNull();
    }

    @Test
    @DisplayName("Deve enriquecer relatório com array vazio")
    void testEnriquecerComArrayVazio() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                2L,
                "Maria Santos",
                "FINALIZADA",
                false,
                "10h",
                tempoPorStatus
        );

        OrdemServico os = OrdemServico.builder()
                .id(2L)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{});

        assertThat(resultado).isNotNull();
        assertThat(resultado.cliente()).isNull();
    }

    @Test
    @DisplayName("Deve enriquecer com Cliente expand")
    void testEnriquecerComClienteExpand() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "João Silva",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome("João Silva");
        org.mockito.Mockito.when(enriquecimentoService.resolverCliente(cliente.getId())).thenReturn(cliente);

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .clienteId(cliente.getId())
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{"Cliente"});

        assertThat(resultado).isNotNull();
        assertThat(resultado.cliente()).isNotNull();
        assertThat(resultado.cliente().nome()).isEqualTo("João Silva");
        assertThat(resultado.veiculo()).isNull();
    }

    @Test
    @DisplayName("Deve enriquecer com Veiculo expand")
    void testEnriquecerComVeiculoExpand() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente A",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        Veiculo veiculo = new Veiculo();
        veiculo.setId(UUID.randomUUID());
        veiculo.setModelo("Ford Ka");
        veiculo.setCor("Branco");
        org.mockito.Mockito.when(enriquecimentoService.resolverVeiculo(veiculo.getId())).thenReturn(veiculo);

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .veiculoId(veiculo.getId())
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{"Veiculo"});

        assertThat(resultado).isNotNull();
        assertThat(resultado.veiculo()).isNotNull();
        assertThat(resultado.veiculo().modelo()).isEqualTo("Ford Ka");
        assertThat(resultado.cliente()).isNull();
    }

    @Test
    @DisplayName("Deve enriquecer com Mecanico expand")
    void testEnriquecerComMecanicoExpand() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente A",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        Usuario mecanico = new Usuario();
        mecanico.setId(UUID.randomUUID());
        mecanico.setNome("Carlos Mecânico");
        org.mockito.Mockito.when(enriquecimentoService.resolverMecanico(mecanico.getId())).thenReturn(mecanico);

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .mecanicoId(mecanico.getId())
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{"Mecanico"});

        assertThat(resultado).isNotNull();
        assertThat(resultado.mecanico()).isNotNull();
        assertThat(resultado.mecanico().nome()).isEqualTo("Carlos Mecânico");
    }

    @Test
    @DisplayName("Deve enriquecer com Mecanico null e expand")
    void testEnriquecerComMecanicoNullExpand() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente A",
                "RECEBIDA",
                false,
                "0h",
                tempoPorStatus
        );

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .mecanicoId(null)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{"Mecanico"});

        assertThat(resultado).isNotNull();
        assertThat(resultado.mecanico()).isNull();
    }

    @Test
    @DisplayName("Deve enriquecer com múltiplos expands")
    void testEnriquecerComMultiplosExpands() {
        Map<String, String> tempoPorStatus = new HashMap<>();
        tempoPorStatus.put("RECEBIDA", "1h");
        tempoPorStatus.put("EM_DIAGNOSTICO", "4h");

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "João Silva",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome("João Silva");
        org.mockito.Mockito.when(enriquecimentoService.resolverCliente(cliente.getId())).thenReturn(cliente);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(UUID.randomUUID());
        veiculo.setModelo("Chevrolet");
        org.mockito.Mockito.when(enriquecimentoService.resolverVeiculo(veiculo.getId())).thenReturn(veiculo);

        Usuario mecanico = new Usuario();
        mecanico.setId(UUID.randomUUID());
        mecanico.setNome("Carlos");
        org.mockito.Mockito.when(enriquecimentoService.resolverMecanico(mecanico.getId())).thenReturn(mecanico);

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .clienteId(cliente.getId())
                .veiculoId(veiculo.getId())
                .mecanicoId(mecanico.getId())
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(
                baseDTO, os, new String[]{"Cliente", "Veiculo", "Mecanico"}
        );

        assertThat(resultado).isNotNull();
        assertThat(resultado.cliente()).isNotNull();
        assertThat(resultado.veiculo()).isNotNull();
        assertThat(resultado.mecanico()).isNotNull();
    }

    @Test
    @DisplayName("Deve calcular valor total correto")
    void testCalcularValorTotalCorreto() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, null);

        assertThat(resultado.valorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ─── Novos testes ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve somar valorTotal de múltiplos orçamentos")
    void testSomaValorTotalOrcamentos() {
        Map<String, String> tempoPorStatus = new HashMap<>();
        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L, "Cliente", "EM_DIAGNOSTICO", false, "3h", tempoPorStatus
        );

        OsOrcamento orc1 = OsOrcamento.builder()
                .id(1L).tipo(TipoOrcamento.INICIAL)
                .valorTotal(BigDecimal.valueOf(300))
                .dataCriacao(LocalDateTime.now())
                .build();
        OsOrcamento orc2 = OsOrcamento.builder()
                .id(2L).tipo(TipoOrcamento.ADICIONAL)
                .valorTotal(BigDecimal.valueOf(200))
                .dataCriacao(LocalDateTime.now())
                .build();

        OrdemServico os = OrdemServico.builder()
                .id(1L).orcamentos(List.of(orc1, orc2)).build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, null);

        assertThat(resultado.valorTotal()).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    @DisplayName("Deve tratar valorTotal null em orçamento como zero na soma")
    void testOrcamentoComValorTotalNullContadoComoZero() {
        Map<String, String> tempoPorStatus = new HashMap<>();
        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L, "Cliente", "RECEBIDA", false, "1h", tempoPorStatus
        );

        OsOrcamento orc = OsOrcamento.builder()
                .id(1L).tipo(TipoOrcamento.INICIAL)
                .valorTotal(null)
                .dataCriacao(LocalDateTime.now())
                .build();

        OrdemServico os = OrdemServico.builder()
                .id(1L).orcamentos(List.of(orc)).build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, null);

        assertThat(resultado.valorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve preencher expand Orcamentos com lista mapeada")
    void testEnriquecerComOrcamentosExpand() {
        Map<String, String> tempoPorStatus = new HashMap<>();
        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L, "Cliente", "EM_DIAGNOSTICO", true, "5h", tempoPorStatus
        );

        OsOrcamento orc = OsOrcamento.builder()
                .id(10L).tipo(TipoOrcamento.INICIAL)
                .valorTotal(BigDecimal.valueOf(400))
                .dataCriacao(LocalDateTime.now())
                .build();

        OrdemServico os = OrdemServico.builder()
                .id(1L).orcamentos(List.of(orc)).build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{"Orcamentos"});

        assertThat(resultado.orcamentos()).isNotNull().hasSize(1);
        assertThat(resultado.orcamentos().get(0).id()).isEqualTo(10L);
        assertThat(resultado.orcamentos().get(0).tipo()).isEqualTo(TipoOrcamento.INICIAL);
    }

    @Test
    @DisplayName("Deve retornar lista vazia no expand Orcamentos quando orcamentos e null")
    void testExpandOrcamentosComOrcamentosNull() {
        Map<String, String> tempoPorStatus = new HashMap<>();
        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L, "Cliente", "RECEBIDA", false, "0h", tempoPorStatus
        );

        OrdemServico os = OrdemServico.builder()
                .id(1L).orcamentos(null).build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{"Orcamentos"});

        assertThat(resultado.orcamentos()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Expand com nome incorreto (case diferente) nao deve preencher campo")
    void testExpandCaseSensitividade() {
        Map<String, String> tempoPorStatus = new HashMap<>();
        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L, "Cliente", "RECEBIDA", false, "0h", tempoPorStatus
        );

        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome("Joao");

        OrdemServico os = OrdemServico.builder()
                .id(1L).clienteId(cliente.getId()).orcamentos(null).build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{"cliente"}); // lowercase

        assertThat(resultado.cliente()).isNull();
    }
}