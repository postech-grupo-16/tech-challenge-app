package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarClienteResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.ClienteNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.BuscarClienteUseCase;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import org.springframework.stereotype.Service;

@Service
public class BuscarClienteService implements BuscarClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public BuscarClienteService(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public BuscarClienteResponse execute(String cpfCnpj) {
        return clienteRepository.buscarPorCpfCnpj(new CpfCnpj(cpfCnpj))
                .map(c -> new BuscarClienteResponse(
                        c.getId(),
                        c.getNome(),
                        c.getCpfCnpj().valor(),
                        c.getTelefone().valor(),
                        c.getCep().valor(),
                        c.getNumero(),
                        c.getRua(),
                        c.getComplemento(),
                        c.getCidade(),
                        c.getEstado(),
                        c.getStatus().name()))
                .orElseThrow(() -> new ClienteNaoEncontradoException(cpfCnpj));
    }
}
