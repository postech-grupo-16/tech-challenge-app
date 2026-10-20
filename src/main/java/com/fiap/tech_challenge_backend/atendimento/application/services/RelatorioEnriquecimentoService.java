package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.application.dto.*;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RelatorioEnriquecimentoService {

    private final OrdemServicoEnriquecimentoService enriquecimentoService;

    public RelatorioEnriquecimentoService(OrdemServicoEnriquecimentoService enriquecimentoService) {
        this.enriquecimentoService = enriquecimentoService;
    }

    public RelatorioOsEnriquecidoResponseDTO enriquecer(
            RelatorioOrdemServicoResponseDTO base,
            OrdemServico os,
            String[] expands) {

        // Calcula valorTotal da OS (soma dos orçamentos)
        BigDecimal valorTotal = os.getOrcamentos() != null && !os.getOrcamentos().isEmpty()
                ? os.getOrcamentos().stream()
                    .map(orc -> orc.getValorTotal() != null ? orc.getValorTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        // Cria DTO enriquecido base
        RelatorioOsEnriquecidoResponseDTO dto = RelatorioOsEnriquecidoResponseDTO.from(base, valorTotal);

        // Se não há expands, retorna base enriquecido
        if (expands == null || expands.length == 0) {
            return dto;
        }

        // Converte array de expands para Set para facilitar verificação
        Set<String> expandSet = new HashSet<>(Arrays.asList(expands));

        // Aplica expands
        if (expandSet.contains("Cliente")) {
            Cliente cliente = enriquecimentoService.resolverCliente(os.getClienteId());
            String email = enriquecimentoService.resolverEmailCliente(cliente);
            dto = dto.comCliente(ClienteInfoDTO.from(cliente, email));
        }

        if (expandSet.contains("Veiculo")) {
            dto = dto.comVeiculo(VeiculoInfoDTO.from(enriquecimentoService.resolverVeiculo(os.getVeiculoId())));
        }

        if (expandSet.contains("Mecanico")) {
            Usuario mecanico = enriquecimentoService.resolverMecanico(os.getMecanicoId());
            if (mecanico != null) {
                dto = dto.comMecanico(MecanicoInfoDTO.from(mecanico));
            }
        }

        if (expandSet.contains("Orcamentos")) {
            List<OrcamentoDTO> orcamentos = os.getOrcamentos() != null
                    ? os.getOrcamentos().stream()
                        .map(orc -> OrcamentoDTO.from(orc, enriquecimentoService::resolverNomePeca))
                        .toList()
                    : List.of();
            dto = dto.comOrcamentos(orcamentos);
        }

        return dto;
    }
}
