package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.cadastro.application.dto.AtualizarClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarClienteResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.ClienteNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.AtualizarClienteUseCase;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Cep;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AtualizarClienteService implements AtualizarClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public AtualizarClienteService(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public BuscarClienteResponse execute(String cpfCnpj, AtualizarClienteRequest request) {
        Cliente cliente = clienteRepository.buscarPorCpfCnpj(new CpfCnpj(cpfCnpj))
                .orElseThrow(() -> new ClienteNaoEncontradoException(cpfCnpj));

        cliente.setNome(request.nome());
        cliente.setTelefone(new Telefone(request.telefone()));
        cliente.setCep(new Cep(request.cep()));
        cliente.setRua(request.rua());
        cliente.setNumero(request.numero());
        cliente.setComplemento(request.complemento());
        cliente.setCidade(request.cidade());
        cliente.setEstado(request.estado());

        Cliente clienteAtualizado = clienteRepository.salvar(cliente);

        return new BuscarClienteResponse(
                clienteAtualizado.getId(),
                clienteAtualizado.getNome(),
                clienteAtualizado.getCpfCnpj().valor(),
                clienteAtualizado.getTelefone().valor(),
                clienteAtualizado.getCep().valor(),
                clienteAtualizado.getRua(),
                clienteAtualizado.getNumero(),
                clienteAtualizado.getComplemento(),
                clienteAtualizado.getCidade(),
                clienteAtualizado.getEstado(),
                clienteAtualizado.getStatus().name()
        );
    }
}
