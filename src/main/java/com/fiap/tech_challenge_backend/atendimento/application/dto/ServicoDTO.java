package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsServico;

import java.math.BigDecimal;
import java.util.UUID;

public record ServicoDTO(
        UUID id,
        UUID servicoId,
        String nome,
        BigDecimal precoMaoDeObraAplicado
) {
    public static ServicoDTO from(OsServico osServico) {
        return new ServicoDTO(
                osServico.getId(),
                osServico.getServico().getId(),
                osServico.getServico().getNome(),
                osServico.getPrecoMaoDeObraAplicado()
        );
    }
}
