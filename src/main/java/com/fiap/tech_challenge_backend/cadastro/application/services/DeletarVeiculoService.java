package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.cadastro.application.exceptions.VeiculoNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteVeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.VeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.DeletarVeiculoUseCase;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DeletarVeiculoService implements DeletarVeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepository;
    private final ClienteVeiculoRepositoryPort clienteVeiculoRepository;

    public DeletarVeiculoService(
            VeiculoRepositoryPort veiculoRepository,
            ClienteVeiculoRepositoryPort clienteVeiculoRepository
    ) {
        this.veiculoRepository = veiculoRepository;
        this.clienteVeiculoRepository = clienteVeiculoRepository;
    }

    @Override
    @Transactional
    public void execute(String placa) {
        Veiculo veiculo = veiculoRepository.buscarPorPlaca(new Placa(placa))
                .orElseThrow(() -> new VeiculoNaoEncontradoException(placa));

        veiculo.setDeletedAt(LocalDateTime.now());
        veiculoRepository.salvar(veiculo);
        clienteVeiculoRepository.deletarPorVeiculoId(veiculo.getId());
    }
}
