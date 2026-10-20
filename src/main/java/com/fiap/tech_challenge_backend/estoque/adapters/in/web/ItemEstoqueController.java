package com.fiap.tech_challenge_backend.estoque.adapters.in.web;

import com.fiap.tech_challenge_backend.shared.application.dto.RelatorioResponseDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.EntradaEstoqueRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.EstoqueRelatorioResponseFactory;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoResponseDTO;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.AtualizarPecaInsumoUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.BuscarPecaInsumoUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.CadastrarPecaInsumoUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.DarEntradaEstoqueUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.MovimentarEstoqueUseCase;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estoque/itens")
@RequiredArgsConstructor
@Tag(name = "Estoque - Itens", description = "Gerenciamento de peças e insumos do estoque")
@SecurityRequirement(name = "bearerAuth")
public class ItemEstoqueController {

    private static final Logger log = LoggerFactory.getLogger(ItemEstoqueController.class);
    private final CadastrarPecaInsumoUseCase cadastrarUseCase;
    private final AtualizarPecaInsumoUseCase atualizarUseCase;
    private final BuscarPecaInsumoUseCase buscarUseCase;
    private final DarEntradaEstoqueUseCase darEntradaUseCase;
    private final MovimentarEstoqueUseCase movimentarUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar peça ou insumo")
    public PecaInsumoResponseDTO cadastrar(@Valid @RequestBody PecaInsumoRequestDTO request) {
        return cadastrarUseCase.cadastrar(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar peça ou insumo")
    public PecaInsumoResponseDTO atualizar(@PathVariable UUID id, @Valid @RequestBody PecaInsumoRequestDTO request) {
        return atualizarUseCase.atualizar(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID")
    public PecaInsumoResponseDTO buscarPorId(@PathVariable UUID id) {
        return buscarUseCase.buscarPorId(id);
    }

    @GetMapping("/abaixo-do-minimo")
    @Operation(summary = "Listar itens com estoque abaixo do mínimo")
    public RelatorioResponseDTO<PecaInsumoResponseDTO> listarAbaixoDoMinimo() {
        log.debug("Requisição recebida: Listar itens abaixo do mínimo");

        try {
            List<PecaInsumoResponseDTO> itens = buscarUseCase.listarAbaixoDoMinimo();
            log.debug("Itens abaixo do mínimo listados | Total: {}", itens.size());

            if (itens.isEmpty()) {
                log.info("Nenhum item abaixo do mínimo encontrado");
                return EstoqueRelatorioResponseFactory.vazio("ITENS ABAIXO DO MINIMO");
            }

            log.info("Itens abaixo do mínimo retornados com sucesso | Total: {}", itens.size());
            return EstoqueRelatorioResponseFactory.sucesso(itens);

        } catch (Exception e) {
            log.error("Erro ao listar itens abaixo do mínimo", e);
            throw e;
        }
    }

    @GetMapping
    @Operation(summary = "Listar itens do estoque. Filtra por tipo (PECA ou INSUMO) quando informado.")
    public List<PecaInsumoResponseDTO> listar(@RequestParam(required = false) TipoPecaInsumo tipo) {
        return buscarUseCase.listarTodos(tipo);
    }

    @PostMapping("/entrada")
    @Operation(summary = "Dar entrada no estoque: cadastra a peça/insumo se não existir ou repõe o estoque se já existir")
    public PecaInsumoResponseDTO darEntrada(@Valid @RequestBody EntradaEstoqueRequestDTO request) {
        return darEntradaUseCase.darEntrada(request);
    }

    @PatchMapping("/{id}/entrada")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Registrar entrada de estoque")
    public RelatorioResponseDTO<PecaInsumoResponseDTO> registrarEntrada(
            @PathVariable UUID id,
            @RequestParam @Positive Integer quantidade,
            @RequestParam(required = false) String observacao) {
        log.debug("Requisição recebida: Registrar entrada de estoque | ID: {} | Quantidade: {} | Observação: {}",
                id, quantidade, observacao != null ? observacao : "Sem observação");

        try {
            movimentarUseCase.registrarEntrada(id, quantidade, observacao);
            PecaInsumoResponseDTO item = buscarUseCase.buscarPorId(id);

            log.info("Entrada de estoque registrada com sucesso | ID: {} | Quantidade: {}", id, quantidade);

            return EstoqueRelatorioResponseFactory.entradaSucesso(List.of(item), quantidade);

        } catch (Exception e) {
            log.error("Erro ao registrar entrada de estoque | ID: {} | Quantidade: {} | Erro: {}",
                    id, quantidade, e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping("/{id}/saida")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Registrar saída de estoque")
    public RelatorioResponseDTO<PecaInsumoResponseDTO> registrarSaida(
            @PathVariable UUID id,
            @RequestParam @Positive Integer quantidade,
            @RequestParam(required = false) String observacao) {
        log.debug("Requisição recebida: Registrar saída de estoque | ID: {} | Quantidade: {} | Observação: {}",
                id, quantidade, observacao != null ? observacao : "Sem observação");

        try {
            movimentarUseCase.registrarSaida(id, quantidade, observacao);
            PecaInsumoResponseDTO item = buscarUseCase.buscarPorId(id);

            log.info("Saída de estoque registrada com sucesso | ID: {} | Quantidade: {}", id, quantidade);

            return EstoqueRelatorioResponseFactory.saidaSucesso(List.of(item), quantidade);

        } catch (Exception e) {
            log.error("Erro ao registrar saída de estoque | ID: {} | Quantidade: {} | Erro: {}",
                    id, quantidade, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Remover item")
    public RelatorioResponseDTO<PecaInsumoResponseDTO> remover(@PathVariable UUID id) {
        atualizarUseCase.remover(id);
        return EstoqueRelatorioResponseFactory.deleteItemSucesso();
    }
}
