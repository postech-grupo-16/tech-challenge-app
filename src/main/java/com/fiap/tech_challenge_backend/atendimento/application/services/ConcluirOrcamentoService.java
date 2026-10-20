package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.ConcluirOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.ConcluirOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: concluir o diagnóstico de uma Ordem de Serviço e enviar o orçamento
 * gerado por e-mail (com PDF anexado) para aprovação do cliente.
 * Contexto Delimitado: atendimento
 */
@Service
public class ConcluirOrcamentoService implements ConcluirOrcamentoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConcluirOrcamentoService.class);

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;
    private final OrdemServicoNotificacaoService notificacaoService;

    public ConcluirOrcamentoService(OrdemServicoRepositoryPort ordemServicoRepository,
                                     OsHistoricoStatusRepositoryPort osHistoricoStatusRepository,
                                     OrdemServicoNotificacaoService notificacaoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.osHistoricoStatusRepository = osHistoricoStatusRepository;
        this.notificacaoService = notificacaoService;
    }

    @Override
    @Transactional
    public void concluirEEnviar(ConcluirOrcamentoRequestDTO request) {
        OrdemServico os = ordemServicoRepository.buscarPorOrcamentoId(request.orcamentoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(
                        "Ordem de serviço não encontrada para o orçamento: " + request.orcamentoId()));

        StatusOrdemServico statusAnterior = os.getStatus();

        os.concluirDiagnostico(request.orcamentoId(), request.prazoEstipulado());

        OrdemServico salva = ordemServicoRepository.salvar(os);

        osHistoricoStatusRepository.salvar(OsHistoricoStatus.builder()
                .ordemServico(salva)
                .statusOrigem(statusAnterior)
                .statusDestino(StatusOrdemServico.AGUARDANDO_APROVACAO)
                .build());

        OsOrcamento orcamentoConcluido = salva.getOrcamentos().stream()
                .filter(o -> o.getId().equals(request.orcamentoId()))
                .findFirst()
                .orElseThrow();

        notificacaoService.enviarEmailAprovacaoOrcamento(salva, orcamentoConcluido, request.emailCliente());
        notificacaoService.notificarMudancaStatus(salva, statusAnterior);

        log.info("Orçamento finalizado e e-mail enviado para: {} | OS: {} | Orçamento: {}",
                request.emailCliente(), salva.getId(), orcamentoConcluido.getId());
    }
}
