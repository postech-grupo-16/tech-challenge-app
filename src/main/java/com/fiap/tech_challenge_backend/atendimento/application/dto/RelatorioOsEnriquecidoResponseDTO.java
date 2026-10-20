package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RelatorioOsEnriquecidoResponseDTO(
        Long id,
        String clienteNome,
        String statusAtual,
        Boolean urgente,
        String tempoTotalAtendimento,
        Map<String, String> tempoPorStatus,
        BigDecimal valorTotal,

        // Expands
        ClienteInfoDTO cliente,
        VeiculoInfoDTO veiculo,
        MecanicoInfoDTO mecanico,
        List<OrcamentoDTO> orcamentos
) {
    public static RelatorioOsEnriquecidoResponseDTO from(RelatorioOrdemServicoResponseDTO base, BigDecimal valorTotal) {
        return new RelatorioOsEnriquecidoResponseDTO(
                base.id(),
                base.clienteNome(),
                base.statusAtual(),
                base.urgente(),
                base.tempoTotalAtendimento(),
                base.tempoPorStatus(),
                valorTotal,
                null,
                null,
                null,
                null
        );
    }

    public RelatorioOsEnriquecidoResponseDTO comCliente(ClienteInfoDTO clienteInfo) {
        return new RelatorioOsEnriquecidoResponseDTO(
                this.id,
                this.clienteNome,
                this.statusAtual,
                this.urgente,
                this.tempoTotalAtendimento,
                this.tempoPorStatus,
                this.valorTotal,
                clienteInfo,
                this.veiculo,
                this.mecanico,
                this.orcamentos
        );
    }

    public RelatorioOsEnriquecidoResponseDTO comVeiculo(VeiculoInfoDTO veiculoInfo) {
        return new RelatorioOsEnriquecidoResponseDTO(
                this.id,
                this.clienteNome,
                this.statusAtual,
                this.urgente,
                this.tempoTotalAtendimento,
                this.tempoPorStatus,
                this.valorTotal,
                this.cliente,
                veiculoInfo,
                this.mecanico,
                this.orcamentos
        );
    }

    public RelatorioOsEnriquecidoResponseDTO comMecanico(MecanicoInfoDTO mecanicoInfo) {
        return new RelatorioOsEnriquecidoResponseDTO(
                this.id,
                this.clienteNome,
                this.statusAtual,
                this.urgente,
                this.tempoTotalAtendimento,
                this.tempoPorStatus,
                this.valorTotal,
                this.cliente,
                this.veiculo,
                mecanicoInfo,
                this.orcamentos
        );
    }

    public RelatorioOsEnriquecidoResponseDTO comOrcamentos(List<OrcamentoDTO> orcamentosInfo) {
        return new RelatorioOsEnriquecidoResponseDTO(
                this.id,
                this.clienteNome,
                this.statusAtual,
                this.urgente,
                this.tempoTotalAtendimento,
                this.tempoPorStatus,
                this.valorTotal,
                this.cliente,
                this.veiculo,
                this.mecanico,
                orcamentosInfo
        );
    }
}
