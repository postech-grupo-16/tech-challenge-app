package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrdemServicoClienteRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.CriarOrdemServicoClienteUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.VeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Caso de uso: criar uma Ordem de Serviço a partir de dados públicos do cliente
 * (CPF/CNPJ e placa do veículo).
 * Contexto Delimitado: atendimento
 */
@Service
public class CriarOrdemServicoClienteService implements CriarOrdemServicoClienteUseCase {

    private static final Logger log = LoggerFactory.getLogger(CriarOrdemServicoClienteService.class);

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final VeiculoRepositoryPort veiculoRepository;
    private final IdGeneratorService idGeneratorService;
    private final OrdemServicoEnriquecimentoService enriquecimentoService;

    public CriarOrdemServicoClienteService(OrdemServicoRepositoryPort ordemServicoRepository,
                                            OsHistoricoStatusRepositoryPort osHistoricoStatusRepository,
                                            ClienteRepositoryPort clienteRepository,
                                            VeiculoRepositoryPort veiculoRepository,
                                            IdGeneratorService idGeneratorService,
                                            OrdemServicoEnriquecimentoService enriquecimentoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.osHistoricoStatusRepository = osHistoricoStatusRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.idGeneratorService = idGeneratorService;
        this.enriquecimentoService = enriquecimentoService;
    }

    @Override
    @Transactional
    public OrdemServicoResponseDTO criar(CriarOrdemServicoClienteRequestDTO request) {
        log.info("Criando nova Ordem de Serviço por Cliente | CPF/CNPJ: {} | Placa: {}",
                request.cpfCnpjCliente(), request.placaVeiculo());

        CpfCnpj cpfCnpj = new CpfCnpj(request.cpfCnpjCliente());
        Placa placa = new Placa(request.placaVeiculo());

        Cliente cliente = clienteRepository.buscarPorCpfCnpj(cpfCnpj)
                .orElseThrow(() -> {
                    log.error("Cliente não encontrado com CPF/CNPJ: {}", request.cpfCnpjCliente());
                    return new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + request.cpfCnpjCliente());
                });

        Veiculo veiculo = veiculoRepository.buscarPorPlaca(placa)
                .orElseThrow(() -> {
                    log.error("Veículo não encontrado com placa: {}", request.placaVeiculo());
                    return new IllegalArgumentException("Veículo não encontrado com placa: " + request.placaVeiculo());
                });

        log.debug("Cliente localizado: {} | Veiculo localizado: {}", cliente.getNome(), veiculo.getModelo());

        Long novoIdOS = idGeneratorService.gerarIdOrdemServico();

        OrdemServico os = OrdemServico.builder()
                .id(novoIdOS)
                .clienteId(cliente.getId())
                .veiculoId(veiculo.getId())
                .mecanicoId(null)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotal(BigDecimal.ZERO)
                .queixaCliente(request.queixaCliente())
                .observacoes(request.observacoes())
                .dataCriacao(LocalDateTime.now())
                .urgente(Boolean.FALSE)
                .build();

        os.definirUrgente(request.urgente());

        OrdemServico saved = ordemServicoRepository.salvar(os);

        log.info("Ordem de Serviço criada por Cliente com sucesso | ID: {} | Status: RECEBIDA", saved.getId());

        osHistoricoStatusRepository.salvar(OsHistoricoStatus.builder()
                .ordemServico(saved)
                .statusOrigem(null)
                .statusDestino(StatusOrdemServico.RECEBIDA)
                .build());

        return enriquecimentoService.montar(saved);
    }
}
