package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoJpaEntity;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoMapper;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
public class OrdemServicoRepositoryAdapter implements OrdemServicoRepositoryPort {

    private final OrdemServicoRepository repository;

    public OrdemServicoRepositoryAdapter(OrdemServicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        OrdemServicoJpaEntity saved = repository.save(OrdemServicoMapper.toEntity(ordemServico));
        return OrdemServicoMapper.toDomain(saved);
    }

    @Override
    public Optional<OrdemServico> buscarPorId(Long id) {
        return repository.findById(id).map(OrdemServicoMapper::toDomain);
    }

    @Override
    public List<OrdemServico> listarTodos() {
        return repository.findAll().stream().map(OrdemServicoMapper::toDomain).toList();
    }

    @Override
    public List<OrdemServico> listarPriorizadas() {
        return repository.findAllPrioritized().stream().map(OrdemServicoMapper::toDomain).toList();
    }

    @Override
    public List<OrdemServico> listarAtivasPriorizadas() {
        return repository.findAllAtivasPrioritized().stream().map(OrdemServicoMapper::toDomain).toList();
    }

    @Override
    public List<OrdemServico> listarPorStatus(StatusOrdemServico status) {
        return repository.findAllByStatusPrioritized(status).stream().map(OrdemServicoMapper::toDomain).toList();
    }

    @Override
    public void remover(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<OrdemServico> buscarPorOrcamentoId(Long orcamentoId) {
        return repository.findByOrcamentoId(orcamentoId).map(OrdemServicoMapper::toDomain);
    }

    @Override
    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    @Override
    public List<OrdemServico> listarParaRelatorio() {
        return repository.findAllForRelatorio().stream().map(OrdemServicoMapper::toDomain).toList();
    }

    @Override
    public Long buscarMaiorIdOrdemServico() {
        return repository.findMaxId();
    }

    @Override
    public Long buscarMaiorIdOrcamento() {
        return repository.findMaxOrcamentoId();
    }

    @Override
    public List<OrdemServico> buscarPorClienteId(UUID clienteId) {
        return repository.findByClienteIdWithDetails(clienteId).stream().map(OrdemServicoMapper::toDomain).toList();
    }
}
