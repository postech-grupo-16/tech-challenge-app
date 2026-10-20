package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "OsServico")
@Table(name = "os_servicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsServicoJpaEntity {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_os_servico_orcamento"))
    @ToString.Exclude
    private OsOrcamentoJpaEntity orcamento;

    @Column(name = "ordem_servico_id")
    private Long ordemServicoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_os_servico_servico_catalogo"))
    private ServicoCatalogoJpaEntity servico;

    @Column(name = "preco_mao_de_obra_aplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoMaoDeObraAplicado;
}
