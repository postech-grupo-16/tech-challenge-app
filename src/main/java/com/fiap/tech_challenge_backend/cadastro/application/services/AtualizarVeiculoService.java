package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.cadastro.application.dto.AtualizarVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.VeiculoNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.VeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.AtualizarVeiculoUseCase;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AtualizarVeiculoService implements AtualizarVeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepository;

    public AtualizarVeiculoService(VeiculoRepositoryPort veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    @Transactional
    public BuscarVeiculoResponse execute(String placa, AtualizarVeiculoRequest request) {
        Veiculo veiculo = veiculoRepository.buscarPorPlaca(new Placa(placa))
                .orElseThrow(() -> new VeiculoNaoEncontradoException(placa));

        veiculo.setModelo(request.modelo());

        Veiculo veiculoAtualizado = veiculoRepository.salvar(veiculo);

        return new BuscarVeiculoResponse(
                veiculoAtualizado.getId(),
                veiculoAtualizado.getPlaca().valor(),
                veiculoAtualizado.getMarca(),
                veiculoAtualizado.getModelo(),
                veiculoAtualizado.getAno(),
                veiculoAtualizado.getCor()
        );
    }
}
