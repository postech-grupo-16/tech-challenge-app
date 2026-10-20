package com.fiap.tech_challenge_backend.atendimento.adapters.in.web;

import com.fiap.tech_challenge_backend.atendimento.adapters.in.web.constants.AtendimentoApiPaths;
import com.fiap.tech_challenge_backend.atendimento.application.dto.RelatorioOsEnriquecidoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.RelatorioOrdensServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RelatorioAtendimentoController.class)
@Import(TestSecurityConfig.class)
@DisplayName("RelatorioAtendimentoController Tests")
class RelatorioAtendimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RelatorioOrdensServicoUseCase relatorioUseCase;

    private RelatorioOsEnriquecidoResponseDTO criarRelatorio(Long id, String clienteNome, String status) {
        return new RelatorioOsEnriquecidoResponseDTO(
                id, clienteNome, status, false, "2 dias", new HashMap<>(),
                new BigDecimal("250.00"), null, null, null, null
        );
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /relatorios/ordens - deve listar relatório")
    void testListarOrdensServico() throws Exception {
        RelatorioOsEnriquecidoResponseDTO relatorio = criarRelatorio(1L, "João", "RECEBIDA");

        when(relatorioUseCase.listarRelatorio(any())).thenReturn(Arrays.asList(relatorio));

        mockMvc.perform(get(AtendimentoApiPaths.RELATORIOS_BASE + AtendimentoApiPaths.RELATORIO_ORDENS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists())
                .andExpect(jsonPath("$.dados", hasSize(1)));

        verify(relatorioUseCase, times(1)).listarRelatorio(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /relatorios/ordens - deve listar com expands")
    void testListarOrdensServicoComExpands() throws Exception {
        RelatorioOsEnriquecidoResponseDTO relatorio = criarRelatorio(1L, "João", "RECEBIDA");

        when(relatorioUseCase.listarRelatorio(any())).thenReturn(Arrays.asList(relatorio));

        mockMvc.perform(get(AtendimentoApiPaths.RELATORIOS_BASE + AtendimentoApiPaths.RELATORIO_ORDENS)
                .param("expand", "cliente", "veiculo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        verify(relatorioUseCase, times(1)).listarRelatorio(any());
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /relatorios/ordens - deve listar vazio")
    void testListarOrdensVazio() throws Exception {
        when(relatorioUseCase.listarRelatorio(any())).thenReturn(List.of());

        mockMvc.perform(get(AtendimentoApiPaths.RELATORIOS_BASE + AtendimentoApiPaths.RELATORIO_ORDENS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists())
                .andExpect(jsonPath("$.dados", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /relatorios/ordens/por-status - deve listar por status RECEBIDA")
    void testListarOrdensServicoPorStatusRecebida() throws Exception {
        RelatorioOsEnriquecidoResponseDTO relatorio = criarRelatorio(1L, "João", "RECEBIDA");

        when(relatorioUseCase.listarRelatorioPorStatus(eq(StatusOrdemServico.RECEBIDA), any()))
                .thenReturn(Arrays.asList(relatorio));

        mockMvc.perform(get(AtendimentoApiPaths.RELATORIOS_BASE + AtendimentoApiPaths.RELATORIO_ORDENS_POR_STATUS)
                .param("status", "RECEBIDA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        verify(relatorioUseCase, times(1)).listarRelatorioPorStatus(eq(StatusOrdemServico.RECEBIDA), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /relatorios/ordens/por-status - deve listar por status EM_EXECUCAO")
    void testListarOrdensServicoPorStatusEmExecucao() throws Exception {
        RelatorioOsEnriquecidoResponseDTO relatorio = criarRelatorio(1L, "Maria", "EM_EXECUCAO");

        when(relatorioUseCase.listarRelatorioPorStatus(eq(StatusOrdemServico.EM_EXECUCAO), any()))
                .thenReturn(Arrays.asList(relatorio));

        mockMvc.perform(get(AtendimentoApiPaths.RELATORIOS_BASE + AtendimentoApiPaths.RELATORIO_ORDENS_POR_STATUS)
                .param("status", "EM_EXECUCAO")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        verify(relatorioUseCase, times(1)).listarRelatorioPorStatus(eq(StatusOrdemServico.EM_EXECUCAO), any());
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /relatorios/ordens/por-status - deve retornar vazio")
    void testListarOrdensServicoPorStatusVazio() throws Exception {
        when(relatorioUseCase.listarRelatorioPorStatus(eq(StatusOrdemServico.ENTREGUE), any()))
                .thenReturn(List.of());

        mockMvc.perform(get(AtendimentoApiPaths.RELATORIOS_BASE + AtendimentoApiPaths.RELATORIO_ORDENS_POR_STATUS)
                .param("status", "ENTREGUE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        verify(relatorioUseCase, times(1)).listarRelatorioPorStatus(eq(StatusOrdemServico.ENTREGUE), any());
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /relatorios/ordens/por-status - deve listar com expands")
    void testListarOrdensServicoPorStatusComExpands() throws Exception {
        RelatorioOsEnriquecidoResponseDTO relatorio = criarRelatorio(1L, "João", "RECEBIDA");

        when(relatorioUseCase.listarRelatorioPorStatus(eq(StatusOrdemServico.RECEBIDA), any()))
                .thenReturn(Arrays.asList(relatorio));

        mockMvc.perform(get(AtendimentoApiPaths.RELATORIOS_BASE + AtendimentoApiPaths.RELATORIO_ORDENS_POR_STATUS)
                .param("status", "RECEBIDA")
                .param("expand", "orcamentos", "historico")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        verify(relatorioUseCase, times(1)).listarRelatorioPorStatus(eq(StatusOrdemServico.RECEBIDA), any());
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /relatorios/ordens/por-status - deve listar por status FINALIZADA")
    void testListarOrdensServicoPorStatusFinalizada() throws Exception {
        RelatorioOsEnriquecidoResponseDTO relatorio = criarRelatorio(1L, "João", "FINALIZADA");

        when(relatorioUseCase.listarRelatorioPorStatus(eq(StatusOrdemServico.FINALIZADA), any()))
                .thenReturn(Arrays.asList(relatorio));

        mockMvc.perform(get(AtendimentoApiPaths.RELATORIOS_BASE + AtendimentoApiPaths.RELATORIO_ORDENS_POR_STATUS)
                .param("status", "FINALIZADA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        verify(relatorioUseCase, times(1)).listarRelatorioPorStatus(eq(StatusOrdemServico.FINALIZADA), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /relatorios/ordens - deve listar com múltiplos expands")
    void testListarOrdensServicoComMultiplosExpands() throws Exception {
        RelatorioOsEnriquecidoResponseDTO relatorio = criarRelatorio(1L, "João", "RECEBIDA");

        when(relatorioUseCase.listarRelatorio(any())).thenReturn(Arrays.asList(relatorio));

        mockMvc.perform(get(AtendimentoApiPaths.RELATORIOS_BASE + AtendimentoApiPaths.RELATORIO_ORDENS)
                .param("expand", "cliente", "veiculo", "orcamentos", "historico", "servicos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        verify(relatorioUseCase, times(1)).listarRelatorio(any());
    }
}
