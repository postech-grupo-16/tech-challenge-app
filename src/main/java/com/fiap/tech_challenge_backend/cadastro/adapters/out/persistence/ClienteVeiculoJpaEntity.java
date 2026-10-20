package com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculoId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Entidade de persistência (JPA) de
 * {@link com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculo}.
 */
@Entity(name = "ClienteVeiculo")
@IdClass(ClienteVeiculoId.class)
@Table(name = "cliente_veiculo", indexes = {
        @Index(name = "idx_cliente_veiculo_cliente_id", columnList = "cliente_id"),
        @Index(name = "idx_cliente_veiculo_veiculo_id", columnList = "veiculo_id"),
        @Index(name = "idx_cliente_veiculo_ativo", columnList = "ativo"),
        @Index(name = "idx_cliente_veiculo_cliente_ativo", columnList = "cliente_id, ativo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteVeiculoJpaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Id
    @Column(name = "veiculo_id", nullable = false)
    private UUID veiculoId;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;
}
