package com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de persistência (JPA) de {@link com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo}.
 */
@Entity(name = "Veiculo")
@Table(name = "veiculos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_veiculo_placa", columnNames = "placa")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeiculoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "placa", nullable = false, unique = true, length = 8)
    private String placa;

    @Column(name = "marca", nullable = false, length = 100)
    private String marca;

    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @Column(name = "cor", nullable = false, length = 50)
    private String cor;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
