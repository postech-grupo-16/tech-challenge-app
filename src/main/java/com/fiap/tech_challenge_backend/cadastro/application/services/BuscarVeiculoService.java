package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.VeiculoNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.VeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.BuscarVeiculoUseCase;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.springframework.stereotype.Service;

@Service
public class BuscarVeiculoService implements BuscarVeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepository;

    public BuscarVeiculoService(VeiculoRepositoryPort veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public BuscarVeiculoResponse execute(String placa) {
        return veiculoRepository.buscarPorPlaca(new Placa(placa))
                .map(v -> new BuscarVeiculoResponse(
                        v.getId(),
                        v.getPlaca().valor(),
                        v.getMarca(),
                        v.getModelo(),
                        v.getAno(),
                        v.getCor()))
                .orElseThrow(() -> new VeiculoNaoEncontradoException(placa));
    }
}
