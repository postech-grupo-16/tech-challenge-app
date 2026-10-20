package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.PecaOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.ServicoOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrcamentoNaoEncontradoException;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.ReferenciaNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.CriarOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PecaInsumoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.ServicoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsPeca;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: criar um orçamento (inicial ou adicional) para uma Ordem de Serviço,
 * buscando preços atualizados de serviços do catálogo e peças do estoque, e enviando
 * o orçamento por e-mail ao cliente.
 * Contexto Delimitado: atendimento
 */
@Service
public class CriarOrcamentoService implements CriarOrcamentoUseCase {

    private static final Logger log = LoggerFactory.getLogger(CriarOrcamentoService.class);

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final ServicoCatalogoRepositoryPort servicoCatalogoRepository;
    private final PecaInsumoCatalogoRepositoryPort pecaInsumoRepository;
    private final IdGeneratorService idGeneratorService;
    private final OrdemServicoNotificacaoService notificacaoService;

    public CriarOrcamentoService(OrdemServicoRepositoryPort ordemServicoRepository,
                                  ServicoCatalogoRepositoryPort servicoCatalogoRepository,
                                  PecaInsumoCatalogoRepositoryPort pecaInsumoRepository,
                                  IdGeneratorService idGeneratorService,
                                  OrdemServicoNotificacaoService notificacaoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.servicoCatalogoRepository = servicoCatalogoRepository;
        this.pecaInsumoRepository = pecaInsumoRepository;
        this.idGeneratorService = idGeneratorService;
        this.notificacaoService = notificacaoService;
    }

    @Override
    @Transactional
    public OrcamentoResponseDTO criar(Long ordemServicoId, CriarOrcamentoRequestDTO request) {
        log.info("Criando orçamento | OS: {} | Tipo: {} | Prazo: {} | Serviços: {} | Peças: {}",
                ordemServicoId, request.tipo(), request.prazoEstipulado(),
                request.servicos().size(), request.pecas().size());

        OsOrcamento orcamentoCriado = criarOrcamentoInterno(ordemServicoId, request);

        log.info("Orçamento criado com sucesso | ID: {} | Tipo: {} | Valor: {} | Data Criação: {} | OS Status: {}",
                orcamentoCriado.getId(), orcamentoCriado.getTipo(), orcamentoCriado.getValorTotal(),
                orcamentoCriado.getDataCriacao());

        // Enviar email fora da transação - em um contexto separado
        try {
            enviarEmailOrcamentoAssincrono(ordemServicoId, orcamentoCriado.getId());
        } catch (Exception e) {
            log.error("Erro ao enviar email de orçamento (não crítico) | OS: {} | Erro: {}",
                    ordemServicoId, e.getMessage());
        }

        return OrcamentoResponseDTO.from(orcamentoCriado,
                id -> pecaInsumoRepository.buscarPorId(id).map(PecaInsumo::getNome).orElse(null));
    }

    @Transactional
    private OsOrcamento criarOrcamentoInterno(Long ordemServicoId, CriarOrcamentoRequestDTO request) {
        OrdemServico os = buscarEntidade(ordemServicoId);

        Long novoIdOrcamento = idGeneratorService.gerarIdOrcamento();

        OsOrcamento novoOrcamento = OsOrcamento.builder()
                .id(novoIdOrcamento)
                .ordemServico(os)
                .tipo(request.tipo())
                .status(StatusOrcamento.PENDENTE)
                .prazoEstipulado(request.prazoEstipulado() != null
                        ? java.time.LocalDateTime.now().plusDays(5)
                        : null)
                .build();

        // Buscar e adicionar serviços com preços atualizados do catálogo
        for (ServicoOrcamentoRequestDTO servicoReq : request.servicos()) {
            ServicoCatalogo servico = servicoCatalogoRepository.buscarPorId(servicoReq.servicoId())
                    .orElseThrow(() -> {
                        log.error("Serviço não encontrado: {}", servicoReq.servicoId());
                        return new ReferenciaNaoEncontradaException("Serviço não encontrado: " + servicoReq.servicoId());
                    });

            log.debug("Adicionando serviço ao orçamento | Serviço: {} | Preço Atual: {}",
                    servico.getNome(), servico.getPrecoMaoDeObra());

            OsServico osServico = OsServico.builder()
                    .orcamento(novoOrcamento)
                    .ordemServicoId(os.getId())
                    .servico(servico)
                    .precoMaoDeObraAplicado(servico.getPrecoMaoDeObra())
                    .build();

            novoOrcamento.adicionarServico(osServico);
        }

        // Buscar e adicionar peças com preços atualizados do estoque
        for (PecaOrcamentoRequestDTO pecaReq : request.pecas()) {
            PecaInsumo peca = pecaInsumoRepository.buscarPorId(pecaReq.pecaId())
                    .orElseThrow(() -> {
                        log.error("Peça não encontrada: {}", pecaReq.pecaId());
                        return new ReferenciaNaoEncontradaException("Peça não encontrada: " + pecaReq.pecaId());
                    });

            log.debug("Adicionando peça ao orçamento | Peça: {} | Qtd: {} | Preço Unit: {}",
                    peca.getNome(), pecaReq.quantidade(), peca.getPrecoVenda());

            OsPeca osPeca = OsPeca.builder()
                    .orcamento(novoOrcamento)
                    .ordemServicoId(os.getId())
                    .pecaInsumoId(peca.getId())
                    .quantidade(pecaReq.quantidade())
                    .precoVendaAplicado(peca.getPrecoVenda())
                    .build();

            novoOrcamento.adicionarPeca(osPeca);
        }

        os.adicionarOrcamento(novoOrcamento);
        OrdemServico salva = ordemServicoRepository.salvar(os);

        return salva.getOrcamentos().get(salva.getOrcamentos().size() - 1);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    private void enviarEmailOrcamentoAssincrono(Long ordemServicoId, Long orcamentoId) {
        OrdemServico os = buscarEntidade(ordemServicoId);

        OsOrcamento orcamento = os.getOrcamentos().stream()
                .filter(orc -> orc.getId().equals(orcamentoId))
                .findFirst()
                .orElseThrow(() -> new OrcamentoNaoEncontradoException(
                        "Orçamento não encontrado: " + orcamentoId));

        notificacaoService.enviarEmailOrcamento(os, orcamento);
    }

    private OrdemServico buscarEntidade(Long id) {
        return ordemServicoRepository.buscarPorId(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(
                        "Ordem de serviço não encontrada: " + id));
    }
}
