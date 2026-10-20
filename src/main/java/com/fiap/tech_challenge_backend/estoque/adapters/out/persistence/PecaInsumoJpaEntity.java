package com.fiap.tech_challenge_backend.estoque.adapters.out.persistence;

import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de persistência (JPA) de
 * {@link com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo}.
 */
@Entity(name = "PecaInsumo")
@Table(name = "peca_insumo", indexes = {
        @Index(name = "idx_peca_nome", columnList = "nome"),
        @Index(name = "idx_peca_quantidade_estoque", columnList = "quantidade_estoque")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted_at IS NULL")
public class PecaInsumoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoPecaInsumo tipo;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "preco_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoCompra;

    @Column(name = "quantidade_por_unidade", length = 50)
    private String quantidadePorUnidade;

    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque;

    @Column(name = "quantidade_minima", nullable = false)
    private Integer quantidadeMinima;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Preserva, no nível de persistência, a validação que antes rodava como hook JPA
     * diretamente na entidade de domínio.
     */
    @PostLoad
    @PostPersist
    @PostUpdate
    void validarPrecos() {
        if (this.precoCompra != null && this.precoVenda != null
                && this.precoCompra.compareTo(this.precoVenda) > 0) {
            throw new IllegalArgumentException(
                    "O preço de compra não pode ser maior que o preço de venda");
        }
    }
}
