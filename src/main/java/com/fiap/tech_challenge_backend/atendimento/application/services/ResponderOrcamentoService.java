package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.AprovarRejeitarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.ResponderOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PecaInsumoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: registrar a aprovação ou rejeição de um orçamento pelo cliente.
 * Contexto Delimitado: atendimento
 */
@Service
public class ResponderOrcamentoService implements ResponderOrcamentoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ResponderOrcamentoService.class);

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;
    private final OrdemServicoNotificacaoService notificacaoService;
    private final PecaInsumoCatalogoRepositoryPort pecaInsumoRepository;

    public ResponderOrcamentoService(OrdemServicoRepositoryPort ordemServicoRepository,
                                      OsHistoricoStatusRepositoryPort osHistoricoStatusRepository,
                                      OrdemServicoNotificacaoService notificacaoService,
                                      PecaInsumoCatalogoRepositoryPort pecaInsumoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.osHistoricoStatusRepository = osHistoricoStatusRepository;
        this.notificacaoService = notificacaoService;
        this.pecaInsumoRepository = pecaInsumoRepository;
    }

    @Override
    @Transactional
    public OrcamentoResponseDTO responder(Long ordemServicoId, Long orcamentoId, AprovarRejeitarOrcamentoRequestDTO request) {
        log.info("Respondendo orçamento | OS: {} | Orcamento: {} | Status: {}",
                ordemServicoId, orcamentoId, request.status());

        OrdemServico os = buscarEntidade(ordemServicoId);

        if (request.status() == StatusOrcamento.APROVADO) {
            StatusOrdemServico statusAnterior = os.getStatus();
            os.aprovarOrcamento(orcamentoId);
            log.info("Orçamento aprovado | Orcamento: {} | OS Status: {} | Valor Acumulado: {}",
                    orcamentoId, os.getStatus(), os.getValorTotalAcumulado());

            OrdemServico salva = ordemServicoRepository.salvar(os);

            osHistoricoStatusRepository.salvar(OsHistoricoStatus.builder()
                    .ordemServico(salva)
                    .statusOrigem(statusAnterior)
                    .statusDestino(StatusOrdemServico.EM_EXECUCAO)
                    .build());

            OsOrcamento orcamentoAtualizado = salva.getOrcamentos().stream()
                    .filter(orc -> orc.getId().equals(orcamentoId))
                    .findFirst()
                    .orElseThrow();

            notificacaoService.notificarMudancaStatus(salva, statusAnterior);
            notificacaoService.notificarRespostaOrcamento(salva, orcamentoAtualizado, true);

            return OrcamentoResponseDTO.from(orcamentoAtualizado,
                id -> pecaInsumoRepository.buscarPorId(id).map(PecaInsumo::getNome).orElse(null));
        } else if (request.status() == StatusOrcamento.REJEITADO) {
            os.rejeitarOrcamento(orcamentoId);
            log.info("Orçamento rejeitado | Orcamento: {}", orcamentoId);
        }

        OrdemServico salva = ordemServicoRepository.salvar(os);

        OsOrcamento orcamentoAtualizado = salva.getOrcamentos().stream()
                .filter(orc -> orc.getId().equals(orcamentoId))
                .findFirst()
                .orElseThrow();

        if (request.status() == StatusOrcamento.REJEITADO) {
            notificacaoService.notificarRespostaOrcamento(salva, orcamentoAtualizado, false);
        }

        return OrcamentoResponseDTO.from(orcamentoAtualizado,
                id -> pecaInsumoRepository.buscarPorId(id).map(PecaInsumo::getNome).orElse(null));
    }

    private OrdemServico buscarEntidade(Long id) {
        return ordemServicoRepository.buscarPorId(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(
                        "Ordem de serviço não encontrada: " + id));
    }
}
