package com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence;

import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository clienteJpaRepository) {
        this.clienteJpaRepository = clienteJpaRepository;
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        var salvo = clienteJpaRepository.save(ClienteMapper.toEntity(cliente));
        return ClienteMapper.toDomain(salvo);
    }

    @Override
    public boolean existePorCpfCnpj(CpfCnpj cpfCnpj) {
        return clienteJpaRepository.existsByCpfCnpj(cpfCnpj.valor());
    }

    @Override
    public Optional<Cliente> buscarPorCpfCnpj(CpfCnpj cpfCnpj) {
        return clienteJpaRepository.findByCpfCnpj(cpfCnpj.valor()).map(ClienteMapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return clienteJpaRepository.findById(id).map(ClienteMapper::toDomain);
    }

    @Override
    public List<Cliente> listar() {
        return this.clienteJpaRepository.findAll().stream().map(ClienteMapper::toDomain).toList();
    }

    @Override
    public void deletar(UUID id) {
        this.clienteJpaRepository.deleteById(id);
    }
}
