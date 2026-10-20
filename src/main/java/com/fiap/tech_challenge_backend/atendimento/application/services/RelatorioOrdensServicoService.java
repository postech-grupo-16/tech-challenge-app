package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.RelatorioOrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.RelatorioOsEnriquecidoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.RelatorioOrdensServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.services.TempoAtendimentoDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Caso de uso: montar relatórios de Ordens de Serviço, com dados enriquecidos e
 * tempos de atendimento calculados a partir do histórico de status.
 * Contexto Delimitado: atendimento
 */
@Service
public class RelatorioOrdensServicoService implements RelatorioOrdensServicoUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;
    private final RelatorioEnriquecimentoService relatorioEnriquecimentoService;
    private final OrdemServicoEnriquecimentoService enriquecimentoService;

    public RelatorioOrdensServicoService(OrdemServicoRepositoryPort ordemServicoRepository,
                                          OsHistoricoStatusRepositoryPort osHistoricoStatusRepository,
                                          RelatorioEnriquecimentoService relatorioEnriquecimentoService,
                                          OrdemServicoEnriquecimentoService enriquecimentoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.osHistoricoStatusRepository = osHistoricoStatusRepository;
        this.relatorioEnriquecimentoService = relatorioEnriquecimentoService;
        this.enriquecimentoService = enriquecimentoService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelatorioOsEnriquecidoResponseDTO> listarRelatorio(String[] expands) {
        return montarRelatorio(ordemServicoRepository.listarParaRelatorio(), expands);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelatorioOsEnriquecidoResponseDTO> listarRelatorioPorStatus(StatusOrdemServico status, String[] expands) {
        return montarRelatorio(ordemServicoRepository.listarPorStatus(status), expands);
    }

    private List<RelatorioOsEnriquecidoResponseDTO> montarRelatorio(List<OrdemServico> ordens, String[] expands) {
        if (ordens.isEmpty()) {
            return List.of();
        }

        LocalDateTime referencia = LocalDateTime.now();

        List<Long> ordemIds = ordens.stream().map(OrdemServico::getId).toList();
        Map<Long, List<OsHistoricoStatus>> historicoPorOrdem = osHistoricoStatusRepository.buscarPorOrdensServicoOrdenado(ordemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        h -> h.getOrdemServico().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return ordens.stream()
                .map(os -> {
                    List<OsHistoricoStatus> historico = historicoPorOrdem.getOrDefault(os.getId(), List.of());
                    String tempoTotalAtendimento = TempoAtendimentoDomainService.calcularTempoTotalFormatado(os, referencia);
                    Map<String, String> tempoPorStatus = TempoAtendimentoDomainService.calcularTempoPorStatusFormatado(os, historico, referencia);

                    RelatorioOrdemServicoResponseDTO base = new RelatorioOrdemServicoResponseDTO(
                            os.getId(),
                            enriquecimentoService.resolverCliente(os.getClienteId()).getNome(),
                            os.getStatus().name(),
                            os.getUrgente(),
                            tempoTotalAtendimento,
                            tempoPorStatus
                    );

                    return relatorioEnriquecimentoService.enriquecer(base, os, expands);
                })
                .toList();
    }
}
