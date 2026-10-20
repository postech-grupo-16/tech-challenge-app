package com.fiap.tech_challenge_backend.estoque.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.NotFoundException;

import java.util.UUID;

/**
 * Lançada quando a peça/insumo consultado não existe.
 * Contexto Delimitado: estoque
 */
public class PecaInsumoNaoEncontradoException extends NotFoundException {

    public PecaInsumoNaoEncontradoException(UUID id) {
        super("PECA_INSUMO_NAO_ENCONTRADO", "Peça/insumo não encontrado: " + id);
    }
}
