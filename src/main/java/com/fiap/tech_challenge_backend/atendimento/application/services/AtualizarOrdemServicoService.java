package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.application.dto.DeletarOrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoAtualizarRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AtualizarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: atualizar dados completos de uma Ordem de Serviço, e removê-la.
 * Contexto Delimitado: atendimento
 */
@Service
public class AtualizarOrdemServicoService implements AtualizarOrdemServicoUseCase {

    private static final Logger log = LoggerFactory.getLogger(AtualizarOrdemServicoService.class);

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final OrdemServicoEnriquecimentoService enriquecimentoService;

    public AtualizarOrdemServicoService(OrdemServicoRepositoryPort ordemServicoRepository,
                                         OrdemServicoEnriquecimentoService enriquecimentoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.enriquecimentoService = enriquecimentoService;
    }

    @Override
    @Transactional
    public OrdemServicoResponseDTO atualizar(Long id, OrdemServicoAtualizarRequestDTO request) {
        OrdemServico os = buscarEntidade(id);

        Cliente cliente = enriquecimentoService.resolverCliente(request.clienteId());
        Veiculo veiculo = enriquecimentoService.resolverVeiculo(request.veiculoId());

        Usuario mecanico = request.mecanicoId() != null
                ? enriquecimentoService.resolverMecanicoObrigatorio(request.mecanicoId())
                : null;

        os.setClienteId(cliente.getId());
        os.setVeiculoId(veiculo.getId());
        os.setMecanicoId(mecanico != null ? mecanico.getId() : null);
        os.setStatus(request.status());
        os.setDataInicioExecucao(request.dataInicioExecucao());
        os.setDataFinalizacao(request.dataFinalizacao());
        os.definirUrgente(request.urgente());

        return enriquecimentoService.montar(ordemServicoRepository.salvar(os));
    }

    @Override
    @Transactional
    public DeletarOrdemServicoResponseDTO remover(Long id) {
        log.info("Iniciando remoção de Ordem de Serviço | ID: {}", id);

        if (!ordemServicoRepository.existePorId(id)) {
            log.error("Tentativa de remover Ordem de Serviço inexistente | ID: {}", id);
            throw new OrdemServicoNaoEncontradaException("Ordem de serviço não encontrada: " + id);
        }

        ordemServicoRepository.remover(id);
        log.info("Ordem de Serviço removida com sucesso | ID: {}", id);

        return DeletarOrdemServicoResponseDTO.sucesso(id);
    }

    private OrdemServico buscarEntidade(Long id) {
        return ordemServicoRepository.buscarPorId(id)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(
                        "Ordem de serviço não encontrada: " + id));
    }
}
