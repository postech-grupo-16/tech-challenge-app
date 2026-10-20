package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;

public record VeiculoInfoDTO(
        String placa,
        String modelo,
        String cor
) {
    public static VeiculoInfoDTO from(Veiculo veiculo) {
        return new VeiculoInfoDTO(
                veiculo.getPlaca() != null ? veiculo.getPlaca().valor() : null,
                veiculo.getModelo(),
                veiculo.getCor()
        );
    }
}
