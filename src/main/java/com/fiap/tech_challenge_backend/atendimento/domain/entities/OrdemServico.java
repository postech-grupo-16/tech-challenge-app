package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AccessLevel;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.exceptions.OrdemServicoStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServico {

    private Long id;

    @NotNull(message = "O cliente da ordem de servico e obrigatorio")
    private UUID clienteId;

    @NotNull(message = "O veiculo da ordem de servico e obrigatorio")
    private UUID veiculoId;

    private UUID mecanicoId;

    @NotNull(message = "O status da ordem de servico e obrigatorio")
    @Builder.Default
    private StatusOrdemServico status = StatusOrdemServico.RECEBIDA;

    @NotNull(message = "O valor total acumulado e obrigatorio")
    @PositiveOrZero(message = "O valor total acumulado nao pode ser negativo")
    @Builder.Default
    private BigDecimal valorTotalAcumulado = BigDecimal.ZERO;

    @PositiveOrZero(message = "O valor total nao pode ser negativo")
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @NotNull(message = "A data de criacao e obrigatoria")
    private LocalDateTime dataCriacao;

    private LocalDateTime dataInicioExecucao;

    private LocalDateTime dataFinalizacao;

    @NotNull(message = "A flag de urgência é obrigatória")
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private Boolean urgente = false;

    @NotBlank(message = "A queixa do cliente é obrigatória")
    private String queixaCliente;

    private String observacoes;

    @Valid
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OsOrcamento> orcamentos = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OsHistoricoStatus> historicoStatus = new ArrayList<>();

    public void concluirDiagnostico(Long orcamentoId, LocalDateTime prazoEstipulado) {
        if (this.status != StatusOrdemServico.EM_DIAGNOSTICO) {
            throw new OrdemServicoStatusException(
                    "A OS deve estar no status EM_DIAGNOSTICO para concluir o diagnóstico. Status atual: " + this.status);
        }

        OsOrcamento orcamento = this.orcamentos.stream()
                .filter(o -> o.getId().equals(orcamentoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Orçamento não encontrado na OS: " + orcamentoId));

        orcamento.setPrazoEstipulado(prazoEstipulado);
        orcamento.calcularTotal();

        this.status = StatusOrdemServico.AGUARDANDO_APROVACAO;
    }

    public void autorizarPeloCliente() {
        if (this.status != StatusOrdemServico.AGUARDANDO_APROVACAO) {
            throw new OrdemServicoStatusException(
                    "Apenas orçamentos no status AGUARDANDO_APROVACAO podem ser autorizados. Status atual: " + this.status);
        }

        this.status = StatusOrdemServico.EM_EXECUCAO;
        this.dataInicioExecucao = LocalDateTime.now();
    }

    public void definirUrgente(Boolean urgente) {
        if (urgente == null) {
            return;
        }

        if (Boolean.TRUE.equals(urgente)
                && (status == StatusOrdemServico.FINALIZADA || status == StatusOrdemServico.ENTREGUE)) {
            throw new IllegalArgumentException("Não é permitido marcar como urgente uma OS finalizada ou entregue");
        }

        this.urgente = urgente;
    }

    public void alterarStatus(StatusOrdemServico novoStatus, Usuario novoMecanico) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("O novo status da ordem de serviço é obrigatório");
        }

        if (this.status == StatusOrdemServico.ENTREGUE && novoStatus != StatusOrdemServico.ENTREGUE) {
            throw new OrdemServicoStatusException(
                    "Não é permitido alterar o status de uma OS já entregue. Status atual: " + this.status);
        }

        if (novoStatus.ordinal() < this.status.ordinal()) {
            throw new OrdemServicoStatusException(
                    "Não é permitido retroceder o status da OS de " + this.status + " para " + novoStatus);
        }

        this.status = novoStatus;
        if (novoMecanico != null) {
            this.mecanicoId = novoMecanico.getId();
        }
    }

    public void adicionarOrcamento(OsOrcamento novoOrcamento) {
        if (novoOrcamento.getTipo() == TipoOrcamento.INICIAL) {
            boolean jaPossuiOrcamentoInicial = this.orcamentos.stream()
                    .anyMatch(orc -> orc.getTipo() == TipoOrcamento.INICIAL);

            if (jaPossuiOrcamentoInicial) {
                throw new IllegalArgumentException(
                        "Ordem de Serviço já possui um orçamento INICIAL. Não é permitido adicionar outro do mesmo tipo.");
            }

            novoOrcamento.calcularTotal();
            this.orcamentos.add(novoOrcamento);
            this.status = StatusOrdemServico.AGUARDANDO_APROVACAO;
        } else {
            novoOrcamento.calcularTotal();
            this.orcamentos.add(novoOrcamento);
        }
    }

    public void aprovarOrcamento(Long orcamentoId) {
        OsOrcamento orcamento = this.orcamentos.stream()
                .filter(orc -> orc.getId().equals(orcamentoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Orçamento não encontrado nesta Ordem de Serviço: " + orcamentoId));

        if (this.status != StatusOrdemServico.AGUARDANDO_APROVACAO) {
            throw new OrdemServicoStatusException(
                    "A OS deve estar no status AGUARDANDO_APROVACAO para aprovar um orçamento. Status atual: " + this.status);
        }

        orcamento.aprovar();
        this.valorTotalAcumulado = this.valorTotalAcumulado.add(orcamento.getValorTotal());
        this.status = StatusOrdemServico.EM_EXECUCAO;
        this.dataInicioExecucao = LocalDateTime.now();
    }

    public void rejeitarOrcamento(Long orcamentoId) {
        OsOrcamento orcamento = this.orcamentos.stream()
                .filter(orc -> orc.getId().equals(orcamentoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Orçamento não encontrado nesta Ordem de Serviço: " + orcamentoId));

        if (orcamento.getTipo() == TipoOrcamento.INICIAL) {
            throw new IllegalArgumentException(
                    "Não é permitido rejeitar um orçamento INICIAL. Solicite a criação de um novo orçamento.");
        }

        orcamento.rejeitar();
    }
}
