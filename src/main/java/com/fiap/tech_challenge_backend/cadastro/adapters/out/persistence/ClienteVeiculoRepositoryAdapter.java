package com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence;

import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteVeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculo;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ClienteVeiculoRepositoryAdapter implements ClienteVeiculoRepositoryPort {

    private final ClienteVeiculoJpaRepository clienteVeiculoJpaRepository;

    public ClienteVeiculoRepositoryAdapter(
            ClienteVeiculoJpaRepository clienteVeiculoJpaRepository
    ) {
        this.clienteVeiculoJpaRepository = clienteVeiculoJpaRepository;
    }

    @Override
    public ClienteVeiculo salvar(ClienteVeiculo clienteVeiculo) {
        var salvo = this.clienteVeiculoJpaRepository.save(ClienteVeiculoMapper.toEntity(clienteVeiculo));
        return ClienteVeiculoMapper.toDomain(salvo);
    }

    @Override
    public boolean existeVinculoAtivo(UUID clienteId, UUID veiculoId) {
        return this.clienteVeiculoJpaRepository.existsByClienteIdAndVeiculoIdAndAtivoTrue(
                clienteId,
                veiculoId
        );
    }

    @Override
    public void deletarPorVeiculoId(UUID veiculoId) {
        this.clienteVeiculoJpaRepository.deleteByVeiculoId(veiculoId);
    }
}
