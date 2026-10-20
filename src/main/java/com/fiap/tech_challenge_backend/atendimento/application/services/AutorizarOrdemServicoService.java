package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AutorizarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: autorizar uma Ordem de Serviço pelo cliente (via link do e-mail),
 * passando de AGUARDANDO_APROVACAO para EM_EXECUCAO.
 * Contexto Delimitado: atendimento
 */
@Service
public class AutorizarOrdemServicoService implements AutorizarOrdemServicoUseCase {

    private static final Logger log = LoggerFactory.getLogger(AutorizarOrdemServicoService.class);

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;
    private final OrdemServicoNotificacaoService notificacaoService;

    public AutorizarOrdemServicoService(OrdemServicoRepositoryPort ordemServicoRepository,
                                         OsHistoricoStatusRepositoryPort osHistoricoStatusRepository,
                                         OrdemServicoNotificacaoService notificacaoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.osHistoricoStatusRepository = osHistoricoStatusRepository;
        this.notificacaoService = notificacaoService;
    }

    @Override
    @Transactional
    public void autorizar(Long id) {
        OrdemServico os = buscarEntidade(id);

        StatusOrdemServico statusAnterior = os.getStatus();

        os.autorizarPeloCliente();

        OrdemServico salva = ordemServicoRepository.salvar(os);

        osHistoricoStatusRepository.salvar(OsHistoricoStatus.builder()
                .ordemServico(salva)
                .statusOrigem(statusAnterior)
                .statusDestino(StatusOrdemServico.EM_EXECUCAO)
                .usuarioId(null)
                .build());

        notificacaoService.notificarMudancaStatus(salva, statusAnterior);

        log.info("Ordem de serviço autorizada pelo cliente. OS: {} | Status anterior: {} | Status novo: {}",
                id, statusAnterior, StatusOrdemServico.EM_EXECUCAO);
    }

    private OrdemServico buscarEntidade(Long id) {
        return ordemServicoRepository.buscarPorId(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(
                        "Ordem de serviço não encontrada: " + id));
    }
}
