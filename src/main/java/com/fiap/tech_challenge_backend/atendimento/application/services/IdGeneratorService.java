package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Serviço responsável pela geração de IDs sequenciais para Ordens de Serviço e Orçamentos.
 *
 * Formato:
 * - Ordem de Serviço: YYYYNNNNN (4 dígitos do ano + 5 sequenciais) ex: 20260001
 * - Orçamento: YYNNNNNNN (2 dígitos do ano + 6 sequenciais) ex: 26000001
 *
 * Carrega o máximo ID existente no banco ao inicializar para garantir sequencialidade.
 */
@Service
public class IdGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(IdGeneratorService.class);

    private final AtomicLong sequenciaOrdemServico;
    private final AtomicLong sequenciaOrcamento;
    private final OrdemServicoRepositoryPort ordemServicoRepository;

    public IdGeneratorService(OrdemServicoRepositoryPort ordemServicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.sequenciaOrdemServico = new AtomicLong(inicializarSequenciaOrdemServico());
        this.sequenciaOrcamento = new AtomicLong(inicializarSequenciaOrcamento());
        log.info("IdGeneratorService inicializado | MaxOS: {} | MaxOrcamento: {}",
                sequenciaOrdemServico.get(), sequenciaOrcamento.get());
    }

    /**
     * Inicializa a sequência de Ordem de Serviço consultando o máximo ID no banco
     */
    private long inicializarSequenciaOrdemServico() {
        try {
            Long maxId = ordemServicoRepository.buscarMaiorIdOrdemServico();
            if (maxId != null && maxId > 0) {
                long ultimaSequencia = extrairSequenciaDeOS(maxId);
                log.debug("Última sequência de OS no banco: {}", ultimaSequencia);
                return ultimaSequencia;
            }
        } catch (Exception e) {
            log.warn("Erro ao inicializar sequência de OS, começando em 0", e);
        }
        return 0;
    }

    /**
     * Inicializa a sequência de Orçamento consultando o máximo ID no banco
     */
    private long inicializarSequenciaOrcamento() {
        try {
            Long maxOrcamentoId = ordemServicoRepository.buscarMaiorIdOrcamento();
            if (maxOrcamentoId != null && maxOrcamentoId > 0) {
                long ultimaSequencia = extrairSequenciaDeOrcamento(maxOrcamentoId);
                log.debug("Última sequência de Orçamento no banco: {}", ultimaSequencia);
                return ultimaSequencia;
            }
        } catch (Exception e) {
            log.warn("Erro ao inicializar sequência de Orçamento, começando em 0", e);
        }
        return 0;
    }

    /**
     * Extrai a sequência de uma Ordem de Serviço (últimos 5 dígitos)
     * Ex: 20260001 -> 1
     */
    private long extrairSequenciaDeOS(Long osId) {
        if (osId == null || osId <= 0) return 0;
        return osId % 100000; // Últimos 5 dígitos
    }

    /**
     * Extrai a sequência de um Orçamento (últimos 6 dígitos)
     * Ex: 26000001 -> 1
     */
    private long extrairSequenciaDeOrcamento(Long orcamentoId) {
        if (orcamentoId == null || orcamentoId <= 0) return 0;
        return orcamentoId % 1000000; // Últimos 6 dígitos
    }

    /**
     * Gera um ID sequencial para Ordem de Serviço no formato YYYYNNNNN
     * @return ID sequencial com ano e 5 dígitos (mínimo 9 dígitos)
     */
    public Long gerarIdOrdemServico() {
        int anoAtual = Year.now().getValue();
        long sequencia = sequenciaOrdemServico.incrementAndGet();

        // Formato: YYYYNNNNN (ex: 20260001)
        String id = String.format("%d%05d", anoAtual, sequencia);
        long novoId = Long.parseLong(id);

        log.debug("Novo ID de OS gerado: {} (sequência: {})", novoId, sequencia);
        return novoId;
    }

    /**
     * Gera um ID sequencial para Orçamento no formato YYNNNNNNN
     * @return ID sequencial com últimos 2 dígitos do ano e 6 dígitos sequenciais (8 dígitos totais)
     */
    public Long gerarIdOrcamento() {
        int anoAtual = Year.now().getValue();
        int ultimos2Digitos = anoAtual % 100; // Extrai os 2 últimos dígitos do ano
        long sequencia = sequenciaOrcamento.incrementAndGet();

        // Formato: YYNNNNNNN (ex: 26000001)
        String id = String.format("%02d%06d", ultimos2Digitos, sequencia);
        long novoId = Long.parseLong(id);

        log.debug("Novo ID de Orçamento gerado: {} (sequência: {})", novoId, sequencia);
        return novoId;
    }
}
