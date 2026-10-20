package com.fiap.tech_challenge_backend.atendimento.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para criação de Ordem de Serviço pelo cliente.
 * Recebe dados de identificação do cliente e veículo através de valores públicos (CPF/CNPJ e placa).
 * Contexto Delimitado: atendimento
 * Camada: Application
 */
public record CriarOrdemServicoClienteRequestDTO(

        @NotBlank(message = "O CPF/CNPJ do cliente é obrigatório")
        String cpfCnpjCliente,

        @NotBlank(message = "A placa do veículo é obrigatória")
        String placaVeiculo,

        @NotBlank(message = "A queixa do cliente é obrigatória")
        String queixaCliente,

        String observacoes,

        Boolean urgente
) {
}
