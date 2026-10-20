package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsPeca;

import java.math.BigDecimal;
import java.util.UUID;

public record PecaDTO(
        UUID id,
        UUID pecaId,
        String nome,
        Integer quantidade,
        BigDecimal precoVendaAplicado
) {
    /**
     * @param nome nome da peça/insumo, já resolvido pelo chamador a partir de
     *             {@code osPeca.getPecaInsumoId()} — este DTO não tem acesso a portas.
     */
    public static PecaDTO from(OsPeca osPeca, String nome) {
        return new PecaDTO(
                osPeca.getId(),
                osPeca.getPecaInsumoId(),
                nome,
                osPeca.getQuantidade(),
                osPeca.getPrecoVendaAplicado()
        );
    }
}
