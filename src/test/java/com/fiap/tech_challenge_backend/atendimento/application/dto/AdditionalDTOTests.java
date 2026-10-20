package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Additional DTO Tests - Edge Cases")
class AdditionalDTOTests {

    @Test
    @DisplayName("ClienteInfoDTO - deve criar com email null")
    void testClienteInfoComEmailNull() {
        UUID id = UUID.randomUUID();

        ClienteInfoDTO dto = new ClienteInfoDTO(id, "Cliente", "11999999999", null);

        assertThat(dto.email()).isNull();
        assertThat(dto.telefone()).isNotNull();
    }

    @Test
    @DisplayName("ClienteInfoDTO - deve criar com todos null exceto id e nome")
    void testClienteInfoComMinimoCampos() {
        UUID id = UUID.randomUUID();

        ClienteInfoDTO dto = new ClienteInfoDTO(id, "Cliente", null, null);

        assertThat(dto.id()).isNotNull();
        assertThat(dto.nome()).isNotNull();
        assertThat(dto.telefone()).isNull();
        assertThat(dto.email()).isNull();
    }

    @Test
    @DisplayName("VeiculoInfoDTO - deve criar com modelo null")
    void testVeiculoInfoComModeloNull() {
        VeiculoInfoDTO dto = new VeiculoInfoDTO("ABC1234", null, "Azul");

        assertThat(dto.placa()).isEqualTo("ABC1234");
        assertThat(dto.modelo()).isNull();
        assertThat(dto.cor()).isEqualTo("Azul");
    }

    @Test
    @DisplayName("PecaDTO - deve criar com quantidade grande")
    void testPecaDTOComQuantidadeGrande() {
        UUID id = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();

        PecaDTO dto = new PecaDTO(id, pecaId, "Pneu", 999, BigDecimal.valueOf(5000.00));

        assertThat(dto.quantidade()).isEqualTo(999);
        assertThat(dto.precoVendaAplicado()).isEqualByComparingTo(BigDecimal.valueOf(5000.00));
    }

    @Test
    @DisplayName("ServicoDTO - deve criar com preço zero")
    void testServicoDTOComPrecoZero() {
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        ServicoDTO dto = new ServicoDTO(id, servicoId, "Serviço Teste", BigDecimal.ZERO);

        assertThat(dto.precoMaoDeObraAplicado()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("CriarOrdemServicoClienteRequestDTO - deve criar com urgente true")
    void testCriarOrdemServicoClienteComUrgenteTrue() {
        CriarOrdemServicoClienteRequestDTO dto = new CriarOrdemServicoClienteRequestDTO(
                "12345678901",
                "ABC1234",
                "Queixa importante",
                "Obs importante",
                true
        );

        assertThat(dto.urgente()).isTrue();
    }

    @Test
    @DisplayName("RelatorioOrdemServicoResponseDTO - deve ter campo urgente false")
    void testRelatorioComUrgenteFalse() {
        Map<String, String> mapa = new HashMap<>();
        mapa.put("STATUS", "1h");

        RelatorioOrdemServicoResponseDTO dto = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente",
                "FINALIZADA",
                false,
                "10h",
                mapa
        );

        assertThat(dto.urgente()).isFalse();
    }

    @Test
    @DisplayName("DeletarOrdemServicoResponseDTO - deve ter mensagem correta")
    void testDeletarOrdemServicoComMensagemCompleta() {
        LocalDateTime agora = LocalDateTime.now();

        DeletarOrdemServicoResponseDTO dto = new DeletarOrdemServicoResponseDTO(
                123L,
                "Ordem deletada com sucesso",
                agora,
                "DELETADO"
        );

        assertThat(dto.mensagem()).isEqualTo("Ordem deletada com sucesso");
        assertThat(dto.status()).isEqualTo("DELETADO");
    }

    @Test
    @DisplayName("AprovarRejeitarOrcamentoRequestDTO - deve comparar com status pendente")
    void testAprovarRejeitarComStatusPendente() {
        AprovarRejeitarOrcamentoRequestDTO dto1 = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.PENDENTE
        );
        AprovarRejeitarOrcamentoRequestDTO dto2 = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.APROVADO
        );

        assertThat(dto1.status()).isEqualTo(StatusOrcamento.PENDENTE);
        assertThat(dto2.status()).isNotEqualTo(dto1.status());
    }

    @Test
    @DisplayName("ConcluirOrcamentoRequestDTO - deve ter prazo estipulado futuro")
    void testConcluirOrcamentoComPrazoFuturo() {
        LocalDateTime prazoFuturo = LocalDateTime.now().plusDays(30);

        ConcluirOrcamentoRequestDTO dto = new ConcluirOrcamentoRequestDTO(
                999L,
                "cliente@email.com",
                prazoFuturo
        );

        assertThat(dto.prazoEstipulado()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("CriarOrcamentoRequestDTOTest - deve ter tipo inicial")
    void testCriarOrcamentoComTipoInicial() {
        List<ServicoOrcamentoRequestDTO> servicos = List.of(
                new ServicoOrcamentoRequestDTO(UUID.randomUUID())
        );

        CriarOrcamentoRequestDTO dto = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL,
                "2026-07-15",
                servicos,
                List.of()
        );

        assertThat(dto.tipo()).isEqualTo(TipoOrcamento.INICIAL);
        assertThat(dto.servicos()).hasSize(1);
    }

    @Test
    @DisplayName("OrdemServicoRequestDTO - deve ter cliente e veiculo obrigatórios")
    void testOrdemServicoRequestComClienteVeiculoObrigatorios() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoRequestDTO dto = new OrdemServicoRequestDTO(
                clienteId,
                veiculoId,
                null,
                null
        );

        assertThat(dto.clienteId()).isNotNull();
        assertThat(dto.veiculoId()).isNotNull();
    }

    @Test
    @DisplayName("OrdemServicoAtualizarRequestDTO - deve ter status obrigatório")
    void testOrdemServicoAtualizarComStatusObrigatorio() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoAtualizarRequestDTO dto = new OrdemServicoAtualizarRequestDTO(
                clienteId,
                veiculoId,
                null,
                com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.ENTREGUE,
                null,
                null,
                true
        );

        assertThat(dto.status()).isEqualTo(com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.ENTREGUE);
    }

    @Test
    @DisplayName("StatusOrdemServicoResponseDTO - deve gerar mensagem para FINALIZADA")
    void testStatusOrdemServicoComMensagemFinalizada() {
        UUID ordemId = UUID.randomUUID();

        StatusOrdemServicoResponseDTO dto = StatusOrdemServicoResponseDTO.from(
                ordemId,
                "CONCLUIDA",
                true
        );

        assertThat(dto.mensagem()).isNotNull();
        assertThat(dto.status()).isEqualTo("CONCLUIDA");
    }

    @Test
    @DisplayName("OrcamentoDTOTest - deve ter tipo adicional")
    void testOrcamentoDTOComTipoAdicional() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoDTO dto = new OrcamentoDTO(
                1L,
                TipoOrcamento.ADICIONAL,
                StatusOrcamento.REJEITADO,
                BigDecimal.valueOf(1000.00),
                agora,
                agora,
                List.of(),
                List.of()
        );

        assertThat(dto.tipo()).isEqualTo(TipoOrcamento.ADICIONAL);
        assertThat(dto.status()).isEqualTo(StatusOrcamento.REJEITADO);
    }

    @Test
    @DisplayName("OrcamentoResponseDTO - deve ter valor total negativo zero")
    void testOrcamentoResponseComValorZero() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoResponseDTO dto = new OrcamentoResponseDTO(
                99L,
                TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE,
                BigDecimal.ZERO,
                agora,
                agora,
                List.of(),
                List.of()
        );

        assertThat(dto.valorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("PecaOrcamentoRequestDTO - deve ter quantidade válida")
    void testPecaOrcamentoComQuantidadeValida() {
        UUID pecaId = UUID.randomUUID();

        PecaOrcamentoRequestDTO dto = new PecaOrcamentoRequestDTO(pecaId, 50);

        assertThat(dto.quantidade()).isGreaterThan(0);
        assertThat(dto.pecaId()).isEqualTo(pecaId);
    }

    @Test
    @DisplayName("ServicoOrcamentoRequestDTO - deve manter id do serviço")
    void testServicoOrcamentoMantendoId() {
        UUID servicoId = UUID.randomUUID();

        ServicoOrcamentoRequestDTO dto = new ServicoOrcamentoRequestDTO(servicoId);

        assertThat(dto.servicoId()).isEqualTo(servicoId);
    }

    @Test
    @DisplayName("RelatorioOsEnriquecidoResponseDTO - deve aplicar método from")
    void testRelatorioEnriquecidoAplicandoFrom() {
        Map<String, String> mapa = new HashMap<>();
        mapa.put("RECEBIDA", "1h");

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente Teste",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                mapa
        );

        RelatorioOsEnriquecidoResponseDTO dto = RelatorioOsEnriquecidoResponseDTO.from(
                baseDTO,
                BigDecimal.valueOf(2500.50)
        );

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.valorTotal()).isEqualByComparingTo(BigDecimal.valueOf(2500.50));
        assertThat(dto.cliente()).isNull();
    }

    @Test
    @DisplayName("MecanicoInfoDTO - deve retornar null no from com null")
    void testMecanicoInfoFromComNull() {
        MecanicoInfoDTO resultado = MecanicoInfoDTO.from(null);

        assertThat(resultado).isNull();
    }
}
