package com.fiap.tech_challenge_backend.atendimento.adapters.in.web;

import com.fiap.tech_challenge_backend.atendimento.adapters.in.web.constants.AtendimentoApiPaths;
import com.fiap.tech_challenge_backend.atendimento.application.dto.AlterarStatusRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrdemServicoClienteRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.DeletarOrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoAtualizarRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;
import com.fiap.tech_challenge_backend.shared.application.dto.RelatorioResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AlterarStatusOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AtualizarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.BuscarOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.BuscarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.CriarOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.CriarOrdemServicoClienteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(AtendimentoApiPaths.OS_BASE)
@Tag(name = "Atendimento - Ordens de Serviço", description = "CRUD e gestão do ciclo de vida das Ordens de Serviço")
@SecurityRequirement(name = "bearerAuth")
public class OrdemServicoController {

    private final CriarOrdemServicoClienteUseCase criarClienteUseCase;
    private final BuscarOrdemServicoUseCase buscarUseCase;
    private final AtualizarOrdemServicoUseCase atualizarUseCase;
    private final AlterarStatusOrdemServicoUseCase alterarStatusUseCase;
    private final CriarOrcamentoUseCase criarOrcamentoUseCase;
    private final BuscarOrcamentoUseCase buscarOrcamentoUseCase;

    public OrdemServicoController(CriarOrdemServicoClienteUseCase criarClienteUseCase,
                                  BuscarOrdemServicoUseCase buscarUseCase,
                                  AtualizarOrdemServicoUseCase atualizarUseCase,
                                  AlterarStatusOrdemServicoUseCase alterarStatusUseCase,
                                  CriarOrcamentoUseCase criarOrcamentoUseCase,
                                  BuscarOrcamentoUseCase buscarOrcamentoUseCase) {
        this.criarClienteUseCase = criarClienteUseCase;
        this.buscarUseCase = buscarUseCase;
        this.atualizarUseCase = atualizarUseCase;
        this.alterarStatusUseCase = alterarStatusUseCase;
        this.criarOrcamentoUseCase = criarOrcamentoUseCase;
        this.buscarOrcamentoUseCase = buscarOrcamentoUseCase;
    }

    @PostMapping(AtendimentoApiPaths.CRIAR_OS)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO', 'CLIENTE')")
    @Operation(summary = "Criar Ordem de Serviço (CPF/CNPJ e Placa)")
    public OrdemServicoResponseDTO criar(@Valid @RequestBody CriarOrdemServicoClienteRequestDTO request) {
        return criarClienteUseCase.criar(request);
    }

    @GetMapping(AtendimentoApiPaths.LISTAR_OS)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Listar todas as Ordens de Serviço")
    public List<OrdemServicoResponseDTO> listarTodos() {
        return buscarUseCase.listarTodos();
    }

    @GetMapping(AtendimentoApiPaths.LISTAR_OS_PRIORIZADAS)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Listar Ordens de Serviço priorizadas por status e data")
    public List<OrdemServicoResponseDTO> listarPriorizadas() {
        return buscarUseCase.listarAtivasPriorizadas();
    }

    @GetMapping(AtendimentoApiPaths.BUSCAR_OS)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Buscar Ordem de Serviço por ID")
    public OrdemServicoResponseDTO buscarPorId(@RequestParam Long id) {
        return buscarUseCase.buscarPorId(id);
    }

    @PutMapping(AtendimentoApiPaths.ATUALIZAR_OS)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Atualizar dados completos da Ordem de Serviço (exceto data de abertura)")
    public OrdemServicoResponseDTO atualizar(@RequestParam Long id,
                                             @Valid @RequestBody OrdemServicoAtualizarRequestDTO request) {
        return atualizarUseCase.atualizar(id, request);
    }

    @DeleteMapping(AtendimentoApiPaths.REMOVER_OS)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover Ordem de Serviço (somente ADMIN)")
    public RelatorioResponseDTO<DeletarOrdemServicoResponseDTO> remover(@RequestParam Long id) {
        DeletarOrdemServicoResponseDTO resultado = atualizarUseCase.remover(id);
        return RelatorioResponseDTO.oSsucesso(List.of(resultado));
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover Ordem de Serviço (somente ADMIN) - rota alternativa")
    public RelatorioResponseDTO<DeletarOrdemServicoResponseDTO> removerAlt(@RequestParam Long id) {
        DeletarOrdemServicoResponseDTO resultado = atualizarUseCase.remover(id);
        return RelatorioResponseDTO.oSsucesso(List.of(resultado));
    }

    @PatchMapping(AtendimentoApiPaths.ALTERAR_STATUS)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Alterar status da Ordem de Serviço (registra histórico automaticamente)")
    public RelatorioResponseDTO<OrdemServicoResponseDTO> alterarStatus(@RequestParam Long id,
                                                 @Valid @RequestBody AlterarStatusRequestDTO request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        OrdemServicoResponseDTO resultado = alterarStatusUseCase.alterarStatus(id, request.novoStatus(), request.mecanicoId(), jwt.getSubject());
        return RelatorioResponseDTO.alterarSucesso(List.of(resultado));
    }

    @PatchMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Alterar status da Ordem de Serviço (rota alternativa)")
    public RelatorioResponseDTO<OrdemServicoResponseDTO> alterarStatusAlt(@RequestParam Long id,
                                                 @Valid @RequestBody AlterarStatusRequestDTO request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        OrdemServicoResponseDTO resultado = alterarStatusUseCase.alterarStatus(id, request.novoStatus(), request.mecanicoId(), jwt.getSubject());
        return RelatorioResponseDTO.alterarSucesso(List.of(resultado));
    }

    @PostMapping(AtendimentoApiPaths.CRIAR_ORCAMENTO)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Criar novo orçamento para a Ordem de Serviço")
    public OrcamentoResponseDTO criarOrcamento(@RequestParam Long id,
                                               @Valid @RequestBody CriarOrcamentoRequestDTO request) {
        return criarOrcamentoUseCase.criar(id, request);
    }

    @GetMapping(AtendimentoApiPaths.BUSCAR_ORCAMENTO)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @Operation(summary = "Buscar orçamento por ID dentro de uma Ordem de Serviço")
    public OrcamentoResponseDTO buscarOrcamento(@RequestParam(name = "idOS") Long id,
                                                @RequestParam(name = "idOrcamento") Long orcamentoId) {
        return buscarOrcamentoUseCase.buscarPorId(id, orcamentoId);
    }
}

