package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrcamentoNaoEncontradoException;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.BuscarOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PecaInsumoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: buscar um orçamento específico dentro de uma Ordem de Serviço.
 * Contexto Delimitado: atendimento
 */
@Service
public class BuscarOrcamentoService implements BuscarOrcamentoUseCase {

    private static final Logger log = LoggerFactory.getLogger(BuscarOrcamentoService.class);

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final PecaInsumoCatalogoRepositoryPort pecaInsumoRepository;

    public BuscarOrcamentoService(OrdemServicoRepositoryPort ordemServicoRepository,
                                   PecaInsumoCatalogoRepositoryPort pecaInsumoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.pecaInsumoRepository = pecaInsumoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public OrcamentoResponseDTO buscarPorId(Long ordemServicoId, Long orcamentoId) {
        log.info("Buscando orçamento | OS: {} | Orcamento: {}", ordemServicoId, orcamentoId);

        OrdemServico os = buscarEntidade(ordemServicoId);

        OsOrcamento orcamento = os.getOrcamentos().stream()
                .filter(orc -> orc.getId().equals(orcamentoId))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Orçamento não encontrado: {} na OS: {}", orcamentoId, ordemServicoId);
                    return new OrcamentoNaoEncontradoException(
                            "Orçamento não encontrado: " + orcamentoId + " na Ordem de Serviço: " + ordemServicoId);
                });

        log.debug("Orçamento encontrado | ID: {} | Tipo: {} | Status: {} | Valor: {}",
                orcamento.getId(), orcamento.getTipo(), orcamento.getStatus(), orcamento.getValorTotal());

        return OrcamentoResponseDTO.from(orcamento,
                id -> pecaInsumoRepository.buscarPorId(id).map(PecaInsumo::getNome).orElse(null));
    }

    private OrdemServico buscarEntidade(Long id) {
        return ordemServicoRepository.buscarPorId(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(
                        "Ordem de serviço não encontrada: " + id));
    }
}
