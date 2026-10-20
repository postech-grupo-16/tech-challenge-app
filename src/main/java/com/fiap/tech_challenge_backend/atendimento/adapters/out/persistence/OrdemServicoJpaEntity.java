package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
import java.util.UUID;

@Entity(name = "OrdemServico")
@Table(name = "ordens_servico")
@NamedEntityGraph(
        name = "OrdemServico.withOrcamentosAndDetails",
        attributeNodes = {
                @NamedAttributeNode("orcamentos")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServicoJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "veiculo_id", nullable = false)
    private UUID veiculoId;

    @Column(name = "mecanico_id")
    private UUID mecanicoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private StatusOrdemServico status = StatusOrdemServico.RECEBIDA;

    @Column(name = "valor_total_acumulado", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorTotalAcumulado = BigDecimal.ZERO;

    @Column(name = "valor_total", nullable = true, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_inicio_execucao")
    private LocalDateTime dataInicioExecucao;

    @Column(name = "data_finalizacao")
    private LocalDateTime dataFinalizacao;

    @Column(name = "urgente", nullable = false)
    @Builder.Default
    private Boolean urgente = false;

    @Column(name = "queixa_cliente", nullable = false, columnDefinition = "TEXT")
    private String queixaCliente;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OsOrcamentoJpaEntity> orcamentos = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataMudanca ASC")
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OsHistoricoStatusJpaEntity> historicoStatus = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        atualizarDatasPorStatus();
    }

    @PreUpdate
    void preUpdate() {
        atualizarDatasPorStatus();
    }

    private void atualizarDatasPorStatus() {
        if (status == StatusOrdemServico.EM_EXECUCAO && dataInicioExecucao == null) {
            dataInicioExecucao = LocalDateTime.now();
        }
        if (status == StatusOrdemServico.FINALIZADA && dataFinalizacao == null) {
            dataFinalizacao = LocalDateTime.now();
        }
    }
}
