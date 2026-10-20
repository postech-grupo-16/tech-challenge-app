package com.fiap.tech_challenge_backend.atendimento.adapters.in.web;

import com.fiap.tech_challenge_backend.atendimento.adapters.in.web.constants.AtendimentoApiPaths;
import com.fiap.tech_challenge_backend.atendimento.application.dto.*;
import com.fiap.tech_challenge_backend.atendimento.application.ports.in.*;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdemServicoController.class)
@Import(TestSecurityConfig.class)
@DisplayName("OrdemServicoController Tests")
class OrdemServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CriarOrdemServicoClienteUseCase criarClienteUseCase;

    @MockBean
    private BuscarOrdemServicoUseCase buscarUseCase;

    @MockBean
    private AtualizarOrdemServicoUseCase atualizarUseCase;

    @MockBean
    private AlterarStatusOrdemServicoUseCase alterarStatusUseCase;

    @MockBean
    private CriarOrcamentoUseCase criarOrcamentoUseCase;

    @MockBean
    private BuscarOrcamentoUseCase buscarOrcamentoUseCase;

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("POST /os - deve criar ordem de serviço com sucesso")
    void testCriarOrdemServicoComSucesso() throws Exception {
        ClienteInfoDTO cliente = new ClienteInfoDTO(UUID.randomUUID(), "João", "11999999999", "joao@test.com");
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("ABC1234", "Fusca", "Azul");
        OrdemServicoResponseDTO response = new OrdemServicoResponseDTO(
                1L, cliente, veiculo, UUID.randomUUID(), "Mecânico", StatusOrdemServico.RECEBIDA,
                BigDecimal.ZERO, "Revisão", null, LocalDateTime.now(), null, null, false, List.of()
        );
        CriarOrdemServicoClienteRequestDTO request = new CriarOrdemServicoClienteRequestDTO(
                "12345678901", "ABC1234", "Revisão", null, false
        );

        when(criarClienteUseCase.criar(any())).thenReturn(response);

        mockMvc.perform(post(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.CRIAR_OS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(criarClienteUseCase, times(1)).criar(any());
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /os - deve listar todas as ordens")
    void testListarTodasAsOrdensServico() throws Exception {
        ClienteInfoDTO cliente = new ClienteInfoDTO(UUID.randomUUID(), "João", "11999999999", "joao@test.com");
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("ABC1234", "Fusca", "Azul");
        OrdemServicoResponseDTO response = new OrdemServicoResponseDTO(
                1L, cliente, veiculo, UUID.randomUUID(), "Mecânico", StatusOrdemServico.RECEBIDA,
                BigDecimal.ZERO, "Revisão", null, LocalDateTime.now(), null, null, false, List.of()
        );

        when(buscarUseCase.listarTodos()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.LISTAR_OS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(buscarUseCase, times(1)).listarTodos();
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /os/listar-os-priorizadas - deve listar OS ativas priorizadas")
    void testListarOrdensServicoPriorizadas() throws Exception {
        ClienteInfoDTO cliente = new ClienteInfoDTO(UUID.randomUUID(), "João", "11999999999", "joao@test.com");
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("ABC1234", "Fusca", "Azul");
        OrdemServicoResponseDTO response = new OrdemServicoResponseDTO(
                1L, cliente, veiculo, UUID.randomUUID(), "Mecânico", StatusOrdemServico.EM_EXECUCAO,
                BigDecimal.ZERO, "Revisão", null, LocalDateTime.now(), null, null, false, List.of()
        );

        when(buscarUseCase.listarAtivasPriorizadas()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.LISTAR_OS_PRIORIZADAS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("EM_EXECUCAO"));

        verify(buscarUseCase, times(1)).listarAtivasPriorizadas();
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /os?id={id} - deve buscar por ID")
    void testBuscarOrdemServicoPorId() throws Exception {
        ClienteInfoDTO cliente = new ClienteInfoDTO(UUID.randomUUID(), "João", "11999999999", "joao@test.com");
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("ABC1234", "Fusca", "Azul");
        OrdemServicoResponseDTO response = new OrdemServicoResponseDTO(
                1L, cliente, veiculo, UUID.randomUUID(), "Mecânico", StatusOrdemServico.RECEBIDA,
                BigDecimal.ZERO, "Revisão", null, LocalDateTime.now(), null, null, false, List.of()
        );

        when(buscarUseCase.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.BUSCAR_OS).param("id", "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(buscarUseCase, times(1)).buscarPorId(1L);
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("PUT /os?id={id} - deve atualizar")
    void testAtualizarOrdemServico() throws Exception {
        ClienteInfoDTO cliente = new ClienteInfoDTO(UUID.randomUUID(), "João", "11999999999", "joao@test.com");
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("ABC1234", "Fusca", "Azul");
        OrdemServicoResponseDTO response = new OrdemServicoResponseDTO(
                1L, cliente, veiculo, UUID.randomUUID(), "Mecânico", StatusOrdemServico.RECEBIDA,
                BigDecimal.ZERO, "Revisão atualizada", null, LocalDateTime.now(), null, null, false, List.of()
        );
        OrdemServicoAtualizarRequestDTO request = new OrdemServicoAtualizarRequestDTO(
                UUID.randomUUID(), UUID.randomUUID(), null, StatusOrdemServico.RECEBIDA, null, null, false
        );

        when(atualizarUseCase.atualizar(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.ATUALIZAR_OS).param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(atualizarUseCase, times(1)).atualizar(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /os?id={id} - deve deletar")
    void testDeletarOrdemServico() throws Exception {
        DeletarOrdemServicoResponseDTO response = DeletarOrdemServicoResponseDTO.sucesso(1L);

        when(atualizarUseCase.remover(1L)).thenReturn(response);

        mockMvc.perform(delete(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.REMOVER_OS).param("id", "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(atualizarUseCase, times(1)).remover(1L);
    }


    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("POST /os/orcamento?id={id} - deve criar orçamento")
    void testCriarOrcamento() throws Exception {
        ServicoOrcamentoRequestDTO servicoRequest = new ServicoOrcamentoRequestDTO(UUID.randomUUID());
        CriarOrcamentoRequestDTO request = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL, "2026-07-29", Arrays.asList(servicoRequest), null
        );
        OrcamentoResponseDTO response = new OrcamentoResponseDTO(
                1L, TipoOrcamento.INICIAL, null, new BigDecimal("250.00"), null, LocalDateTime.now(), null, null
        );

        when(criarOrcamentoUseCase.criar(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.CRIAR_ORCAMENTO).param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(criarOrcamentoUseCase, times(1)).criar(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /os/orcamento?idOS={id}&idOrcamento={orcId} - deve buscar orçamento")
    void testBuscarOrcamento() throws Exception {
        OrcamentoResponseDTO response = new OrcamentoResponseDTO(
                1L, TipoOrcamento.INICIAL, null, new BigDecimal("250.00"), null, LocalDateTime.now(), null, null
        );

        when(buscarOrcamentoUseCase.buscarPorId(1L, 1L)).thenReturn(response);

        mockMvc.perform(get(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.BUSCAR_ORCAMENTO).param("idOS", "1").param("idOrcamento", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(buscarOrcamentoUseCase, times(1)).buscarPorId(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /os - deve listar vazio")
    void testListarOrdensSemResultados() throws Exception {
        when(buscarUseCase.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.LISTAR_OS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /os/listar-os-priorizadas - deve retornar lista vazia")
    void testListarOrdensPriorizadasSemResultados() throws Exception {
        when(buscarUseCase.listarAtivasPriorizadas()).thenReturn(List.of());

        mockMvc.perform(get(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.LISTAR_OS_PRIORIZADAS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("POST /os - deve retornar 400 para dados inválidos")
    void testCriarOrdemServicoComDadosInvalidos() throws Exception {
        mockMvc.perform(post(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.CRIAR_OS)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cpfCnpj\":\"\",\"placa\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE (rota alternativa) - deve deletar")
    void testDeletarOrdemServicoRotaAlternativa() throws Exception {
        DeletarOrdemServicoResponseDTO response = DeletarOrdemServicoResponseDTO.sucesso(1L);

        when(atualizarUseCase.remover(1L)).thenReturn(response);

        mockMvc.perform(delete(AtendimentoApiPaths.OS_BASE + AtendimentoApiPaths.REMOVER_OS).param("id", "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(atualizarUseCase, times(1)).remover(1L);
    }

}
