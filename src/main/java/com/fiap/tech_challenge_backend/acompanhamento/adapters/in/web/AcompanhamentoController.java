package com.fiap.tech_challenge_backend.acompanhamento.adapters.in.web;

import com.fiap.tech_challenge_backend.acompanhamento.application.dto.AcompanhamentoOsResponseDTO;
import com.fiap.tech_challenge_backend.acompanhamento.application.dto.AcompanhamentoRelatorioResponseFactory;
import com.fiap.tech_challenge_backend.acompanhamento.application.ports.in.ConsultarAcompanhamentoUseCase;
import com.fiap.tech_challenge_backend.shared.application.dto.RelatorioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes/{clienteId}/ordens")
@Tag(name = "Acompanhamento", description = "Consulta do status das ordens de servico pelo cliente")
@SecurityRequirement(name = "bearerAuth")
public class AcompanhamentoController {

    private final ConsultarAcompanhamentoUseCase consultarUseCase;

    public AcompanhamentoController(ConsultarAcompanhamentoUseCase consultarUseCase) {
        this.consultarUseCase = consultarUseCase;
    }

    @GetMapping
    @Operation(summary = "Lista todas as ordens de servico do cliente")
    public ResponseEntity<RelatorioResponseDTO<AcompanhamentoOsResponseDTO>> listar(@PathVariable UUID clienteId) {
        List<AcompanhamentoOsResponseDTO> ordens = consultarUseCase.listarPorCliente(clienteId);

        if (ordens.isEmpty()) {
            return ResponseEntity.ok(AcompanhamentoRelatorioResponseFactory.vazio());
        }

        return ResponseEntity.ok(AcompanhamentoRelatorioResponseFactory.sucesso(ordens));
    }

    @GetMapping("/{osId}")
    @Operation(summary = "Consulta o detalhe de uma ordem de servico do cliente")
    public ResponseEntity<RelatorioResponseDTO<AcompanhamentoOsResponseDTO>> detalhe(
            @PathVariable UUID clienteId,
            @PathVariable Long osId) {
        AcompanhamentoOsResponseDTO ordem = consultarUseCase.buscarDetalhe(clienteId, osId);
        return ResponseEntity.ok(AcompanhamentoRelatorioResponseFactory.sucesso(List.of(ordem)));
    }
}
