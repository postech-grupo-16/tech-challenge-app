package com.fiap.tech_challenge_backend.atendimento.adapters.in.web;

import com.fiap.tech_challenge_backend.atendimento.application.dto.AprovarRejeitarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.AutorizarOrdemServicoUseCase;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.ResponderOrcamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.exceptions.OrdemServicoStatusException;
import com.fiap.tech_challenge_backend.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteOrdemServicoController.class)
@Import(TestSecurityConfig.class)
@DisplayName("ClienteOrdemServicoController Tests")
class ClienteOrdemServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AutorizarOrdemServicoUseCase autorizarOrdemServicoUseCase;

    @MockBean
    private ResponderOrcamentoUseCase responderOrcamentoUseCase;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("GET /api/public/atendimento/ordens/{id}/autorizar - deve autorizar orçamento com sucesso")
    void testAutorizarOrcamentoComSucesso() throws Exception {
        Long osId = 1L;
        doNothing().when(autorizarOrdemServicoUseCase).autorizar(osId);

        mockMvc.perform(get("/api/public/atendimento/ordens/{id}/autorizar", osId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem").value("Seu orçamento foi autorizado com sucesso!"))
                .andExpect(jsonPath("$.ordemServicoId").value(osId))
                .andExpect(jsonPath("$.statusAtual").value("EM_EXECUCAO"));

        verify(autorizarOrdemServicoUseCase, times(1)).autorizar(osId);
    }

    @Test
    @DisplayName("GET /api/public/atendimento/ordens/{id}/autorizar - deve retornar 404 quando OS não encontrada")
    void testAutorizarOrcamentoNaoEncontrada() throws Exception {
        Long osId = 1L;
        doThrow(new OrdemServicoNaoEncontradaException("Ordem de Serviço não encontrada"))
                .when(autorizarOrdemServicoUseCase).autorizar(osId);

        mockMvc.perform(get("/api/public/atendimento/ordens/{id}/autorizar", osId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.erro").value("Ordem de Serviço não encontrada"))
                .andExpect(jsonPath("$.mensagem").value(containsString("não existe")));

        verify(autorizarOrdemServicoUseCase, times(1)).autorizar(osId);
    }

    @Test
    @DisplayName("GET /api/public/atendimento/ordens/{id}/autorizar - deve retornar 422 em erro de runtime")
    void testAutorizarOrcamentoErroRuntime() throws Exception {
        Long osId = 1L;
        doThrow(new RuntimeException("Erro ao processar autorização"))
                .when(autorizarOrdemServicoUseCase).autorizar(osId);

        mockMvc.perform(get("/api/public/atendimento/ordens/{id}/autorizar", osId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.erro").value("Erro ao processar autorização"));

        verify(autorizarOrdemServicoUseCase, times(1)).autorizar(osId);
    }

    @Test
    @DisplayName("PATCH /api/public/atendimento/ordens/{id}/orcamentos/{orcamentoId}/status - deve responder orçamento com aprovação")
    void testResponderOrcamentoAprovado() throws Exception {
        Long osId = 1L;
        Long orcamentoId = 1L;
        OrcamentoResponseDTO orcamentoResponse = new OrcamentoResponseDTO(
                orcamentoId, null, StatusOrcamento.APROVADO, new BigDecimal("100.00"), null, null, null, null
        );
        AprovarRejeitarOrcamentoRequestDTO request = new AprovarRejeitarOrcamentoRequestDTO(StatusOrcamento.APROVADO);

        when(responderOrcamentoUseCase.responder(eq(osId), eq(orcamentoId), any(AprovarRejeitarOrcamentoRequestDTO.class)))
                .thenReturn(orcamentoResponse);

        mockMvc.perform(patch("/api/public/atendimento/ordens/{id}/orcamentos/{orcamentoId}/status", osId, orcamentoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem").value("Sua resposta foi registrada com sucesso!"))
                .andExpect(jsonPath("$.proximoPasso").value(containsString("aprovado")));

        verify(responderOrcamentoUseCase, times(1)).responder(eq(osId), eq(orcamentoId), any());
    }

    @Test
    @DisplayName("PATCH /api/public/atendimento/ordens/{id}/orcamentos/{orcamentoId}/status - deve responder orçamento com rejeição")
    void testResponderOrcamentoRejeitado() throws Exception {
        Long osId = 1L;
        Long orcamentoId = 1L;
        OrcamentoResponseDTO orcamentoResponse = new OrcamentoResponseDTO(
                orcamentoId, null, StatusOrcamento.REJEITADO, new BigDecimal("100.00"), null, null, null, null
        );
        AprovarRejeitarOrcamentoRequestDTO request = new AprovarRejeitarOrcamentoRequestDTO(StatusOrcamento.REJEITADO);

        when(responderOrcamentoUseCase.responder(eq(osId), eq(orcamentoId), any(AprovarRejeitarOrcamentoRequestDTO.class)))
                .thenReturn(orcamentoResponse);

        mockMvc.perform(patch("/api/public/atendimento/ordens/{id}/orcamentos/{orcamentoId}/status", osId, orcamentoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.proximoPasso").value(containsString("rejeitado")));

        verify(responderOrcamentoUseCase, times(1)).responder(eq(osId), eq(orcamentoId), any());
    }

    @Test
    @DisplayName("PATCH /api/public/atendimento/ordens/{id}/orcamentos/{orcamentoId}/status - deve retornar 404 quando orçamento não encontrado")
    void testResponderOrcamentoNaoEncontrado() throws Exception {
        Long osId = 1L;
        Long orcamentoId = 999L;
        AprovarRejeitarOrcamentoRequestDTO request = new AprovarRejeitarOrcamentoRequestDTO(StatusOrcamento.APROVADO);

        when(responderOrcamentoUseCase.responder(eq(osId), eq(orcamentoId), any(AprovarRejeitarOrcamentoRequestDTO.class)))
                .thenThrow(new OrdemServicoNaoEncontradaException("Orçamento não encontrado"));

        mockMvc.perform(patch("/api/public/atendimento/ordens/{id}/orcamentos/{orcamentoId}/status", osId, orcamentoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.erro").value("Orçamento não encontrado"));

        verify(responderOrcamentoUseCase, times(1)).responder(eq(osId), eq(orcamentoId), any());
    }

    @Test
    @DisplayName("PATCH /api/public/atendimento/ordens/{id}/orcamentos/{orcamentoId}/status - deve retornar 422 com status inválido")
    void testResponderOrcamentoComStatusInvalido() throws Exception {
        Long osId = 1L;
        Long orcamentoId = 1L;
        AprovarRejeitarOrcamentoRequestDTO request = new AprovarRejeitarOrcamentoRequestDTO(StatusOrcamento.APROVADO);

        when(responderOrcamentoUseCase.responder(eq(osId), eq(orcamentoId), any(AprovarRejeitarOrcamentoRequestDTO.class)))
                .thenThrow(new OrdemServicoStatusException("Status inválido para esta operação"));

        mockMvc.perform(patch("/api/public/atendimento/ordens/{id}/orcamentos/{orcamentoId}/status", osId, orcamentoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.erro").value("Status da Ordem de Serviço inválido"));

        verify(responderOrcamentoUseCase, times(1)).responder(eq(osId), eq(orcamentoId), any());
    }

    @Test
    @DisplayName("PATCH /api/public/atendimento/ordens/{id}/orcamentos/{orcamentoId}/status - deve retornar 422 com argumento inválido")
    void testResponderOrcamentoComArgumentoInvalido() throws Exception {
        Long osId = 1L;
        Long orcamentoId = 1L;
        AprovarRejeitarOrcamentoRequestDTO request = new AprovarRejeitarOrcamentoRequestDTO(StatusOrcamento.APROVADO);

        when(responderOrcamentoUseCase.responder(eq(osId), eq(orcamentoId), any(AprovarRejeitarOrcamentoRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Argumento inválido fornecido"));

        mockMvc.perform(patch("/api/public/atendimento/ordens/{id}/orcamentos/{orcamentoId}/status", osId, orcamentoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.erro").value("Operação inválida"));

        verify(responderOrcamentoUseCase, times(1)).responder(eq(osId), eq(orcamentoId), any());
    }
}
