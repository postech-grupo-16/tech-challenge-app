package com.fiap.tech_challenge_backend.atendimento.domain.services;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço de domínio para cálculos de tempo de atendimento baseados no histórico de status.
 */
public final class TempoAtendimentoDomainService {

    private TempoAtendimentoDomainService() {
    }

    public static String calcularTempoTotalFormatado(OrdemServico ordemServico, LocalDateTime referencia) {
        LocalDateTime fim = dataFim(ordemServico, referencia);
        return formatarDuracao(Duration.between(ordemServico.getDataCriacao(), fim), ordemServico.getDataFinalizacao() == null);
    }

    public static Map<String, String> calcularTempoPorStatusFormatado(OrdemServico ordemServico,
                                                                       List<OsHistoricoStatus> historico,
                                                                       LocalDateTime referencia) {
        List<OsHistoricoStatus> eventos = new ArrayList<>(historico);
        eventos.sort(Comparator.comparing(OsHistoricoStatus::getDataMudanca));

        LinkedHashMap<StatusOrdemServico, Duration> tempos = new LinkedHashMap<>();
        LinkedHashMap<StatusOrdemServico, Boolean> emAndamento = new LinkedHashMap<>();

        LocalDateTime fim = dataFim(ordemServico, referencia);

        if (eventos.isEmpty()) {
            acumular(tempos, ordemServico.getStatus(), Duration.between(ordemServico.getDataCriacao(), fim));
            emAndamento.put(ordemServico.getStatus(), ordemServico.getDataFinalizacao() == null);
            return formatarMapa(tempos, emAndamento);
        }

        OsHistoricoStatus primeiroEvento = eventos.get(0);
        if (primeiroEvento.getStatusOrigem() != null && ordemServico.getDataCriacao().isBefore(primeiroEvento.getDataMudanca())) {
            acumular(tempos, primeiroEvento.getStatusOrigem(),
                    Duration.between(ordemServico.getDataCriacao(), primeiroEvento.getDataMudanca()));
            emAndamento.putIfAbsent(primeiroEvento.getStatusOrigem(), false);
        }

        for (int i = 0; i < eventos.size(); i++) {
            OsHistoricoStatus atual = eventos.get(i);
            LocalDateTime inicioStatus = atual.getDataMudanca();
            LocalDateTime fimStatus = (i + 1 < eventos.size()) ? eventos.get(i + 1).getDataMudanca() : fim;

            Duration duracao = Duration.between(inicioStatus, fimStatus);
            acumular(tempos, atual.getStatusDestino(), duracao);

            boolean statusEmAndamento = (i == eventos.size() - 1) && ordemServico.getDataFinalizacao() == null;
            emAndamento.put(atual.getStatusDestino(), statusEmAndamento);
        }

        return formatarMapa(tempos, emAndamento);
    }

    private static void acumular(Map<StatusOrdemServico, Duration> tempos,
                                 StatusOrdemServico status,
                                 Duration duracao) {
        Duration normalizada = duracao.isNegative() ? Duration.ZERO : duracao;
        tempos.merge(status, normalizada, Duration::plus);
    }

    private static LinkedHashMap<String, String> formatarMapa(Map<StatusOrdemServico, Duration> tempos,
                                                               Map<StatusOrdemServico, Boolean> emAndamento) {
        LinkedHashMap<String, String> resposta = new LinkedHashMap<>();
        tempos.forEach((status, duracao) ->
                resposta.put(status.name(), formatarDuracao(duracao, emAndamento.getOrDefault(status, false))));
        return resposta;
    }

    private static LocalDateTime dataFim(OrdemServico ordemServico, LocalDateTime referencia) {
        return ordemServico.getDataFinalizacao() != null ? ordemServico.getDataFinalizacao() : referencia;
    }

    private static String formatarDuracao(Duration duracao, boolean emAndamento) {
        long totalMinutos = Math.max(duracao.toMinutes(), 0);

        long dias = totalMinutos / (24 * 60);
        long restoMinutos = totalMinutos % (24 * 60);
        long horas = restoMinutos / 60;
        long minutos = restoMinutos % 60;

        List<String> partes = new ArrayList<>();
        if (dias > 0) {
            partes.add(dias + (dias == 1 ? " dia" : " dias"));
        }
        if (horas > 0) {
            partes.add(horas + (horas == 1 ? " hora" : " horas"));
        }
        if (minutos > 0 || partes.isEmpty()) {
            partes.add(minutos + (minutos == 1 ? " minuto" : " minutos"));
        }

        String texto = String.join(" e ", partes);
        return emAndamento ? texto + " (em andamento)" : texto;
    }
}

