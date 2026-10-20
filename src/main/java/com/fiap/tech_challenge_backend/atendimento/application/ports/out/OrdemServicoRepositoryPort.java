package com.fiap.tech_challenge_backend.atendimento.application.ports.out;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface OrdemServicoRepositoryPort {

    OrdemServico salvar(OrdemServico ordemServico);

    Optional<OrdemServico> buscarPorId(Long id);

    List<OrdemServico> listarTodos();

    List<OrdemServico> listarPriorizadas();

    List<OrdemServico> listarAtivasPriorizadas();

    List<OrdemServico> listarPorStatus(StatusOrdemServico status);

    Optional<OrdemServico> buscarPorOrcamentoId(Long orcamentoId);

    void remover(Long id);

    boolean existePorId(Long id);

    List<OrdemServico> listarParaRelatorio();

    Long buscarMaiorIdOrdemServico();

    Long buscarMaiorIdOrcamento();

    List<OrdemServico> buscarPorClienteId(UUID clienteId);
}

