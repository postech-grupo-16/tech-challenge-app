package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "OsHistoricoStatus")
@Table(name = "os_historico_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsHistoricoStatusJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_os_historico_status_ordem_servico"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrdemServicoJpaEntity ordemServico;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_origem", length = 30)
    private StatusOrdemServico statusOrigem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_destino", nullable = false, length = 30)
    private StatusOrdemServico statusDestino;

    @Column(name = "data_mudanca", nullable = false)
    private LocalDateTime dataMudanca;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @PrePersist
    void prePersist() {
        if (dataMudanca == null) {
            dataMudanca = LocalDateTime.now();
        }
    }
}
