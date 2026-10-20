package com.fiap.tech_challenge_backend.acompanhamento.application.ports.out;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída própria de {@code acompanhamento}. O adapter que a implementa fala com
 * {@code atendimento} através da porta pública dele ({@code OrdemServicoRepositoryPort}),
 * nunca com o {@code JpaRepository} concreto ou entidade carregada fora de uma porta.
 */
public interface AcompanhamentoRepositoryPort {

    List<OrdemServico> buscarPorClienteId(UUID clienteId);

    Optional<OrdemServico> buscarPorId(Long osId);
}
