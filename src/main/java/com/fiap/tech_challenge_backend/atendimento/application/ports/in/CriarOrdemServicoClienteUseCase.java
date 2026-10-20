package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrdemServicoClienteRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;

/**
 * Porta para criar Ordem de Serviço através de dados públicos do cliente.
 * O cliente fornece CPF/CNPJ e placa, o sistema busca os dados na base de cadastro.
 * Contexto Delimitado: atendimento
 * Camada: Application (Ports)
 */
public interface CriarOrdemServicoClienteUseCase {

    /**
     * Cria uma nova Ordem de Serviço a partir de dados fornecidos pelo cliente.
     *
     * @param request DTO contendo CPF/CNPJ do cliente, placa do veículo, queixa e observações
     * @return DTO com dados da OS criada
     * @throws IllegalArgumentException se cliente ou veículo não forem encontrados
     */
    OrdemServicoResponseDTO criar(CriarOrdemServicoClienteRequestDTO request);
}
