package com.fiap.tech_challenge_backend.acompanhamento.adapters.out;

import com.fiap.tech_challenge_backend.acompanhamento.application.ports.out.AcompanhamentoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AcompanhamentoRepositoryAdapter implements AcompanhamentoRepositoryPort {

    private final OrdemServicoRepositoryPort ordemServicoRepository;

    public AcompanhamentoRepositoryAdapter(OrdemServicoRepositoryPort ordemServicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Override
    public List<OrdemServico> buscarPorClienteId(UUID clienteId) {
        return ordemServicoRepository.buscarPorClienteId(clienteId);
    }

    @Override
    public Optional<OrdemServico> buscarPorId(Long osId) {
        return ordemServicoRepository.buscarPorId(osId);
    }
}
