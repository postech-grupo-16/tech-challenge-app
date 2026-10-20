package com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteVeiculoJpaRepository extends JpaRepository<ClienteVeiculoJpaEntity, ClienteVeiculoId> {

    boolean existsByClienteIdAndVeiculoIdAndAtivoTrue(UUID clienteId, UUID veiculoId);

    void deleteByVeiculoId(UUID veiculoId);
}
