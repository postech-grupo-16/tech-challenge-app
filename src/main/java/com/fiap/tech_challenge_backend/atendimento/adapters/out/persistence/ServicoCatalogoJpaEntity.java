package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidade de persistência (JPA) de
 * {@link com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo}.
 */
@Entity(name = "ServicoCatalogo")
@Table(name = "servico_catalogo", indexes = {
        @Index(name = "idx_servico_nome", columnList = "nome"),
        @Index(name = "idx_servico_preco", columnList = "preco_mao_de_obra")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoCatalogoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "preco_mao_de_obra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoMaoDeObra;

    @Column(name = "categoria", nullable = false, length = 30)
    private String categoria;
}
