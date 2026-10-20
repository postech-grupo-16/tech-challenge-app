package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;


public record OrdemServicoResponseDTO(
        Long id,
        ClienteInfoDTO cliente,
        VeiculoInfoDTO veiculo,
        UUID mecanicoId,
        String mecanicoNome,
        StatusOrdemServico status,
        BigDecimal valorTotal,
        String queixaCliente,
        String observacoes,
        LocalDateTime dataCriacao,
        LocalDateTime dataInicioExecucao,
        LocalDateTime dataFinalizacao,
        Boolean urgente,
        List<OrcamentoDTO> orcamentos
) {
    /**
     * @param cliente     cliente da OS, já resolvido pelo chamador via
     *                    {@code OrdemServicoEnriquecimentoService} a partir de
     *                    {@code os.getClienteId()} — este DTO não tem acesso a portas.
     * @param clienteEmail e-mail do usuário associado ao cliente, já resolvido pelo chamador.
     * @param veiculo     veículo da OS, já resolvido a partir de {@code os.getVeiculoId()}.
     * @param mecanico    mecânico da OS (opcional), já resolvido a partir de
     *                    {@code os.getMecanicoId()}.
     * @param nomePeca    função que resolve o nome de uma peça/insumo a partir do seu ID —
     *                    threading até {@link OrcamentoDTO#from(com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento, Function)}.
     */
    public static OrdemServicoResponseDTO from(OrdemServico os,
                                                Cliente cliente,
                                                String clienteEmail,
                                                Veiculo veiculo,
                                                Usuario mecanico,
                                                Function<UUID, String> nomePeca) {
        // Calcula valorTotal a partir da soma de todos os orçamentos
        BigDecimal valorTotalCalculado = os.getOrcamentos() != null && !os.getOrcamentos().isEmpty()
                ? os.getOrcamentos().stream()
                    .map(orc -> orc.getValorTotal() != null ? orc.getValorTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        return new OrdemServicoResponseDTO(
                os.getId(),
                ClienteInfoDTO.from(cliente, clienteEmail),
                VeiculoInfoDTO.from(veiculo),
                mecanico != null ? mecanico.getId() : null,
                mecanico != null ? mecanico.getNome() : null,
                os.getStatus(),
                valorTotalCalculado,
                os.getQueixaCliente(),
                os.getObservacoes(),
                os.getDataCriacao(),
                os.getDataInicioExecucao(),
                os.getDataFinalizacao(),
                os.getUrgente(),
                os.getOrcamentos() != null
                        ? os.getOrcamentos().stream()
                        .map(orc -> OrcamentoDTO.from(orc, nomePeca))
                        .toList()
                        : List.of()
        );
    }
}
