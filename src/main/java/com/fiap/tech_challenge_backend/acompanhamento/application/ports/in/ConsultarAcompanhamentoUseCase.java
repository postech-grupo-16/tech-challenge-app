package com.fiap.tech_challenge_backend.acompanhamento.application.ports.in;

import com.fiap.tech_challenge_backend.acompanhamento.application.dto.AcompanhamentoOsResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ConsultarAcompanhamentoUseCase {

    List<AcompanhamentoOsResponseDTO> listarPorCliente(UUID clienteId);

    AcompanhamentoOsResponseDTO buscarDetalhe(UUID clienteId, Long osId);
}
