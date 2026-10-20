package com.fiap.tech_challenge_backend.atendimento.adapters.in.web.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AtendimentoApiPaths {

    public static final String OS_BASE = "/api/v1/ordens-servico";

    public static final String CRIAR_OS = "/criar";
    public static final String LISTAR_OS = "/listar-os";
    public static final String LISTAR_OS_PRIORIZADAS = "/listar-os-priorizadas";
    public static final String BUSCAR_OS = "/buscar";
    public static final String ATUALIZAR_OS = "/editar";
    public static final String REMOVER_OS = "/deletar";
    public static final String ALTERAR_STATUS = "/alterar-status";

    // Recursos aninhados (usam request param)
    public static final String CRIAR_ORCAMENTO = "/criar-orcamento";
    public static final String BUSCAR_ORCAMENTO = "/buscar-orcamento";

    public static final String RELATORIOS_BASE = "/api/os/atendimento/relatorios";
    public static final String RELATORIO_ORDENS = "/ordens-servico";
    public static final String RELATORIO_ORDENS_POR_STATUS = "/ordens-servico/por-status";
}
