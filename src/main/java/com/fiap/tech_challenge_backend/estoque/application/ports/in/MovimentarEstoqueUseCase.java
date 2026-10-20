package com.fiap.tech_challenge_backend.estoque.application.ports.in;

import java.util.UUID;

public interface MovimentarEstoqueUseCase {

    void registrarEntrada(UUID id, Integer quantidade, String observacao);

    void registrarSaida(UUID id, Integer quantidade, String observacao);

    void registrarVenda(UUID id, Integer quantidade, String observacao);

    void cancelarReserva(UUID id, Integer quantidade, String observacao);
}
