package com.fiap.tech_challenge_backend.acompanhamento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public record AcompanhamentoOsResponseDTO(
        Long id,
        StatusOrdemServico status,
        String descricaoStatus,
        String veiculoPlaca,
        String veiculoModelo,
        String mecanicoNome,
        List<ItemServicoDTO> servicos,
        List<ItemPecaDTO> pecas,
        BigDecimal valorTotal,
        LocalDateTime dataCriacao,
        LocalDateTime dataInicioExecucao,
        LocalDateTime dataFinalizacao
) {

    public record ItemServicoDTO(String nome, BigDecimal precoMaoDeObra) {}

    public record ItemPecaDTO(String nome, int quantidade, BigDecimal subtotal) {}

    /**
     * @param veiculo      veículo da OS, já resolvido pelo chamador a partir de
     *                     {@code os.getVeiculoId()}.
     * @param mecanicoNome nome do mecânico da OS (opcional), já resolvido pelo chamador.
     * @param nomePeca     resolve o nome de uma peça/insumo a partir do seu ID.
     */
    public static AcompanhamentoOsResponseDTO from(OrdemServico os,
                                                    Veiculo veiculo,
                                                    String mecanicoNome,
                                                    Function<UUID, String> nomePeca) {
        var servicos = os.getOrcamentos().stream()
                .flatMap(orc -> orc.getServicos().stream())
                .map(s -> new ItemServicoDTO(
                        s.getServico().getNome(),
                        s.getPrecoMaoDeObraAplicado()))
                .toList();

        var pecas = os.getOrcamentos().stream()
                .flatMap(orc -> orc.getPecas().stream())
                .map(p -> new ItemPecaDTO(
                        nomePeca.apply(p.getPecaInsumoId()),
                        p.getQuantidade(),
                        p.getPrecoVendaAplicado().multiply(BigDecimal.valueOf(p.getQuantidade()))))
                .toList();

        return new AcompanhamentoOsResponseDTO(
                os.getId(),
                os.getStatus(),
                descreverStatus(os.getStatus()),
                veiculo.getPlaca().valor(),
                veiculo.getModelo(),
                mecanicoNome,
                servicos,
                pecas,
                os.getValorTotal(),
                os.getDataCriacao(),
                os.getDataInicioExecucao(),
                os.getDataFinalizacao()
        );
    }

    private static String descreverStatus(StatusOrdemServico status) {
        return switch (status) {
            case RECEBIDA             -> "Recebida - aguardando diagnostico";
            case EM_DIAGNOSTICO       -> "Em diagnostico";
            case AGUARDANDO_APROVACAO -> "Aguardando aprovacao do orcamento";
            case EM_EXECUCAO          -> "Em execucao";
            case FINALIZADA           -> "Finalizada - aguardando retirada";
            case ENTREGUE             -> "Entregue";
        };
    }
}
