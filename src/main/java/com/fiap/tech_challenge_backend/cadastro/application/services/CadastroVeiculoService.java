package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.ClienteNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.VeiculoJaCadastradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteVeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.VeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.CadastrarVeiculoUseCase;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculo;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Caso de uso responsável pelo cadastro de veículos.
 * Contexto Delimitado: cadastro
 * Camada: Application
 */
@Service
public class CadastroVeiculoService implements CadastrarVeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final ClienteVeiculoRepositoryPort clienteVeiculoRepository;

    public CadastroVeiculoService(
            VeiculoRepositoryPort veiculoRepository,
            ClienteRepositoryPort clienteRepository,
            ClienteVeiculoRepositoryPort clienteVeiculoRepository
    ) {
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
        this.clienteVeiculoRepository = clienteVeiculoRepository;
    }

    @Override
    @Transactional
    public CadastroVeiculoResponse execute(CadastroVeiculoRequest request) {
        CpfCnpj cpfCnpj = new CpfCnpj(request.cpfCnpj());
        Placa placa = new Placa(request.placa());
        validarVeiculoExistente(placa);

        Cliente cliente = buscarClientePeloCpfCnpj(cpfCnpj);

        Veiculo veiculo = Veiculo.builder()
                .placa(placa)
                .marca(request.marca())
                .modelo(request.modelo())
                .ano(request.ano())
                .cor(request.cor())
                .build();

        Veiculo veiculoCadastrado = veiculoRepository.salvar(veiculo);

        ClienteVeiculo clienteVeiculo = ClienteVeiculo.builder()
                .clienteId(cliente.getId())
                .veiculoId(veiculoCadastrado.getId())
                .ativo(true)
                .build();

        clienteVeiculoRepository.salvar(clienteVeiculo);

        return new CadastroVeiculoResponse(
                veiculoCadastrado.getId(),
                veiculoCadastrado.getPlaca().valor(),
                veiculoCadastrado.getMarca(),
                veiculoCadastrado.getModelo(),
                veiculoCadastrado.getAno(),
                veiculoCadastrado.getCor(),
                cliente.getCpfCnpj().valor()
        );
    }

    private void validarVeiculoExistente(Placa placa) {
        if (veiculoRepository.existePorPlaca(placa)) {
            throw new VeiculoJaCadastradoException(placa.valor());
        }
    }

    private Cliente buscarClientePeloCpfCnpj(CpfCnpj cpfCnpj) {
        return clienteRepository.buscarPorCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new ClienteNaoEncontradoException(cpfCnpj.valor()));

    }

}
