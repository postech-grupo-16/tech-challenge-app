package com.fiap.tech_challenge_backend.acompanhamento.application.services;

import com.fiap.tech_challenge_backend.acompanhamento.application.dto.AcompanhamentoOsResponseDTO;
import com.fiap.tech_challenge_backend.acompanhamento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.acompanhamento.application.ports.in.ConsultarAcompanhamentoUseCase;
import com.fiap.tech_challenge_backend.acompanhamento.application.ports.out.AcompanhamentoRepositoryPort;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.application.services.OrdemServicoEnriquecimentoService;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AcompanhamentoService implements ConsultarAcompanhamentoUseCase {

    private final AcompanhamentoRepositoryPort acompanhamentoRepository;
    private final OrdemServicoEnriquecimentoService enriquecimentoService;

    public AcompanhamentoService(AcompanhamentoRepositoryPort acompanhamentoRepository,
                                  OrdemServicoEnriquecimentoService enriquecimentoService) {
        this.acompanhamentoRepository = acompanhamentoRepository;
        this.enriquecimentoService = enriquecimentoService;
    }

    @Override
    public List<AcompanhamentoOsResponseDTO> listarPorCliente(UUID clienteId) {
        return acompanhamentoRepository.buscarPorClienteId(clienteId).stream()
                .map(this::montar)
                .toList();
    }

    @Override
    public AcompanhamentoOsResponseDTO buscarDetalhe(UUID clienteId, Long osId) {
        OrdemServico ordem = acompanhamentoRepository.buscarPorId(osId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException("Ordem de servico nao encontrada"));

        if (!ordem.getClienteId().equals(clienteId)) {
            throw new OrdemServicoNaoEncontradaException("Ordem de servico nao encontrada para o cliente informado");
        }

        return montar(ordem);
    }

    private AcompanhamentoOsResponseDTO montar(OrdemServico os) {
        Veiculo veiculo = enriquecimentoService.resolverVeiculo(os.getVeiculoId());
        Usuario mecanico = enriquecimentoService.resolverMecanico(os.getMecanicoId());
        return AcompanhamentoOsResponseDTO.from(
                os, veiculo, mecanico != null ? mecanico.getNome() : null, enriquecimentoService::resolverNomePeca);
    }
}
