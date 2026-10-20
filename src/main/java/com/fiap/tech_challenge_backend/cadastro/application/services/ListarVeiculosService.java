package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.VeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.ListarVeiculosUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarVeiculosService implements ListarVeiculosUseCase {

    private final VeiculoRepositoryPort veiculoRepository;

    public ListarVeiculosService(VeiculoRepositoryPort veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public List<BuscarVeiculoResponse> execute() {
        return veiculoRepository.listar().stream()
                .map(v -> new BuscarVeiculoResponse(
                        v.getId(),
                        v.getPlaca().valor(),
                        v.getMarca(),
                        v.getModelo(),
                        v.getAno(),
                        v.getCor()))
                .toList();
    }
}
