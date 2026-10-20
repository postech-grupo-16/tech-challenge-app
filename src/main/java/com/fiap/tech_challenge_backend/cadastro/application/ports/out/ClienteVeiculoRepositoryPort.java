package com.fiap.tech_challenge_backend.cadastro.application.ports.out;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculo;

import java.util.UUID;

public interface ClienteVeiculoRepositoryPort {

    ClienteVeiculo salvar(ClienteVeiculo clienteVeiculo);

    boolean existeVinculoAtivo(UUID clienteId, UUID veiculoId);

    void deletarPorVeiculoId(UUID veiculoId);
}
