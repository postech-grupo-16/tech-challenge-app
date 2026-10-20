package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.ClienteJaCadastradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.CriarUsuarioClienteCommand;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.CriarUsuarioClientePort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.CadastrarClienteUseCase;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Cep;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Caso de uso responsável pelo cadastro de clientes.
 * Contexto Delimitado: cadastro
 * Camada: Application
 */
@Service
public class CadastroClienteService implements CadastrarClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final CriarUsuarioClientePort criarUsuarioClientePort;

    public CadastroClienteService(
            ClienteRepositoryPort clienteRepository,
            CriarUsuarioClientePort criarUsuarioClientePort
    ) {
        this.clienteRepository = clienteRepository;
        this.criarUsuarioClientePort = criarUsuarioClientePort;
    }

    @Override
    @Transactional
    public CadastroClienteResponse execute(CadastroClienteRequest request) {
        Email email = new Email(request.email());
        CpfCnpj cpfCnpj = new CpfCnpj(request.cpfCnpj());
        Telefone telefone = new Telefone(request.telefone());
        Cep cep = new Cep(request.cep());

        validarClienteExistente(cpfCnpj);

        Usuario usuario = criarUsuarioClientePort.criarUsuarioCliente(
                new CriarUsuarioClienteCommand(
                        request.nome(),
                        email,
                        request.senha(),
                        telefone,
                        cpfCnpj
                )
        );

        Cliente cliente = Cliente.builder()
                .usuarioId(usuario.getId())
                .nome(request.nome())
                .cpfCnpj(cpfCnpj)
                .telefone(telefone)
                .cep(cep)
                .rua(request.rua())
                .numero(request.numero())
                .complemento(request.complemento())
                .cidade(request.cidade())
                .estado(request.estado())
                .build();

        Cliente clienteCadastrado = clienteRepository.salvar(cliente);

        return new CadastroClienteResponse(
                clienteCadastrado.getId(),
                clienteCadastrado.getNome(),
                clienteCadastrado.getCpfCnpj().valor(),
                clienteCadastrado.getTelefone() != null ? clienteCadastrado.getTelefone().valor() : null,
                clienteCadastrado.getCep() != null ? clienteCadastrado.getCep().valor() : null,
                clienteCadastrado.getRua(),
                clienteCadastrado.getNumero(),
                clienteCadastrado.getComplemento(),
                clienteCadastrado.getCidade(),
                clienteCadastrado.getEstado(),
                clienteCadastrado.getStatus().name()
        );
    }

    private void validarClienteExistente(CpfCnpj cpfCnpj) {
        if (clienteRepository.existePorCpfCnpj(cpfCnpj)) {
            throw new ClienteJaCadastradoException(cpfCnpj.valor());
        }
    }

}
