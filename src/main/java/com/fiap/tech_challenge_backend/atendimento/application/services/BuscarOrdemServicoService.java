package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.BuscarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso: consultar Ordens de Serviço (por ID ou em lista).
 * Contexto Delimitado: atendimento
 */
@Service
public class BuscarOrdemServicoService implements BuscarOrdemServicoUseCase {

    private static final Logger log = LoggerFactory.getLogger(BuscarOrdemServicoService.class);

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final OrdemServicoEnriquecimentoService enriquecimentoService;

    public BuscarOrdemServicoService(OrdemServicoRepositoryPort ordemServicoRepository,
                                      OrdemServicoEnriquecimentoService enriquecimentoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.enriquecimentoService = enriquecimentoService;
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarPorId(Long id) {
        log.info("Buscando Ordem de Serviço por ID: {}", id);

        return ordemServicoRepository.buscarPorId(id)
                .map(os -> {
                    log.debug("OS encontrada: {} | Status: {} | Orcamentos carregados: {}",
                            os.getId(), os.getStatus(), os.getOrcamentos().size());

                    os.getOrcamentos().forEach(orc ->
                        log.debug("  Orcamento: {} | Tipo: {} | Servicos: {} | Pecas: {}",
                                orc.getId(), orc.getTipo(), orc.getServicos().size(), orc.getPecas().size())
                    );

                    return enriquecimentoService.montar(os);
                })
                .orElseThrow(() -> {
                    log.warn("Ordem de Serviço não encontrada para o ID: {}", id);
                    return new OrdemServicoNaoEncontradaException("Ordem de serviço não encontrada: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> listarTodos() {
        return ordemServicoRepository.listarPriorizadas().stream()
                .map(enriquecimentoService::montar)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> listarAtivasPriorizadas() {
        return ordemServicoRepository.listarAtivasPriorizadas().stream()
                .map(enriquecimentoService::montar)
                .toList();
    }
}
