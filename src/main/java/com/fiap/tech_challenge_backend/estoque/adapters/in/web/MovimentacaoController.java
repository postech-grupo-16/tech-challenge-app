package com.fiap.tech_challenge_backend.estoque.adapters.in.web;

import com.fiap.tech_challenge_backend.estoque.application.dto.MovimentacaoResponseDTO;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.ListarMovimentacoesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelo histórico de movimentações do estoque.
 * Contexto Delimitado: estoque
 * Camada: Presentation
 */
@RestController
@RequestMapping("/estoque/movimentacoes")
@RequiredArgsConstructor
@Tag(name = "Estoque - Movimentações", description = "Histórico de movimentações de estoque")
@SecurityRequirement(name = "bearerAuth")
public class MovimentacaoController {

    private final ListarMovimentacoesUseCase listarMovimentacoesUseCase;

    @GetMapping("/item/{pecaInsumoId}")
    @Operation(summary = "Listar movimentações de uma peça ou insumo")
    public List<MovimentacaoResponseDTO> listarPorItem(@PathVariable UUID pecaInsumoId) {
        return listarMovimentacoesUseCase.listarMovimentacoes(pecaInsumoId);
    }
}
