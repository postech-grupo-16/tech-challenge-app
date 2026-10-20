package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsOrcamento {

    private Long id;

    @NotNull(message = "A ordem de servico do orcamento e obrigatoria")
    @ToString.Exclude
    private OrdemServico ordemServico;

    @NotNull(message = "O tipo do orcamento e obrigatorio")
    private TipoOrcamento tipo;

    @NotNull(message = "O status do orcamento e obrigatorio")
    @Builder.Default
    private StatusOrcamento status = StatusOrcamento.PENDENTE;

    @NotNull(message = "O valor total do orcamento e obrigatorio")
    @PositiveOrZero(message = "O valor total do orcamento nao pode ser negativo")
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    private LocalDateTime prazoEstipulado;

    @NotNull(message = "A data de criacao do orcamento e obrigatoria")
    private LocalDateTime dataCriacao;

    @Valid
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OsServico> servicos = new ArrayList<>();

    @Valid
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OsPeca> pecas = new ArrayList<>();

    public void calcularTotal() {
        BigDecimal totalServicos = servicos.stream()
                .map(OsServico::getPrecoMaoDeObraAplicado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPecas = pecas.stream()
                .map(p -> p.getPrecoVendaAplicado().multiply(BigDecimal.valueOf(p.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.valorTotal = totalServicos.add(totalPecas);
    }

    public void aprovar() {
        this.status = StatusOrcamento.APROVADO;
    }

    public void rejeitar() {
        this.status = StatusOrcamento.REJEITADO;
    }

    public void adicionarServico(OsServico servico) {
        servico.setOrcamento(this);
        this.servicos.add(servico);
    }

    public void adicionarPeca(OsPeca peca) {
        peca.setOrcamento(this);
        this.pecas.add(peca);
    }
}
