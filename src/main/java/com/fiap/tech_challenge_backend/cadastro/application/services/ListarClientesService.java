package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarClienteResponse;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.ListarClientesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarClientesService implements ListarClientesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListarClientesService.class);
    private final ClienteRepositoryPort clienteRepository;

    public ListarClientesService(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<BuscarClienteResponse> execute() {
        log.debug("Iniciando listagem de clientes");

        List<BuscarClienteResponse> clientes = clienteRepository.listar().stream()
                .map(c -> {
                    log.debug("Processando cliente | ID: {} | Nome: {} | CPF/CNPJ: {}",
                        c.getId(), c.getNome(),
                        c.getCpfCnpj() != null ? c.getCpfCnpj().valor() : "N/A");

                    return new BuscarClienteResponse(
                            c.getId(),
                            c.getNome(),
                            c.getCpfCnpj() != null ? c.getCpfCnpj().valor() : null,
                            c.getTelefone() != null ? c.getTelefone().valor() : null,
                            c.getCep() != null ? c.getCep().valor() : null,
                            c.getRua(),
                            c.getNumero(),
                            c.getComplemento(),
                            c.getCidade(),
                            c.getEstado(),
                            c.getStatus().name()
                    );
                })
                .toList();

        log.debug("Listagem de clientes concluída | Total: {}", clientes.size());
        return clientes;
    }
}
