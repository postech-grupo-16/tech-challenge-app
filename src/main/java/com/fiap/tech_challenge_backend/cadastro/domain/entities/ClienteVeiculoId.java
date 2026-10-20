package com.fiap.tech_challenge_backend.cadastro.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteVeiculoId implements Serializable {

    private UUID clienteId;

    private UUID veiculoId;
}
