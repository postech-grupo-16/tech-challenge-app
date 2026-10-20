package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.cadastro.application.exceptions.ClienteNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.DeletarUsuarioClientePort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.DeletarClienteUseCase;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeletarClienteService implements DeletarClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final DeletarUsuarioClientePort deletarUsuarioClientePort;

    public DeletarClienteService(
            ClienteRepositoryPort clienteRepository,
            DeletarUsuarioClientePort deletarUsuarioClientePort
    ) {
        this.clienteRepository = clienteRepository;
        this.deletarUsuarioClientePort = deletarUsuarioClientePort;
    }

    @Override
    @Transactional
    public void execute(String cpfCnpj) {
        Cliente cliente = clienteRepository.buscarPorCpfCnpj(new CpfCnpj(cpfCnpj))
                .orElseThrow(() -> new ClienteNaoEncontradoException(cpfCnpj));

        clienteRepository.deletar(cliente.getId());
        deletarUsuarioClientePort.deletar(cpfCnpj);
    }

}
