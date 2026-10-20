package com.fiap.tech_challenge_backend.acompanhamento.adapters.in.web;

import com.fiap.tech_challenge_backend.acompanhamento.application.dto.AcompanhamentoOsResponseDTO;
import com.fiap.tech_challenge_backend.acompanhamento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.acompanhamento.application.ports.in.ConsultarAcompanhamentoUseCase;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.config.TestSecurityConfig;
import com.fiap.tech_challenge_backend.shared.application.dto.RelatorioResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AcompanhamentoController.class)
@DisplayName("AcompanhamentoController - Testes Unitários")
class AcompanhamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConsultarAcompanhamentoUseCase consultarUseCase;

    private UUID clienteId;
    private Long osId;
    private AcompanhamentoOsResponseDTO acompanhamentoDto;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        osId = 1L;

        acompanhamentoDto = new AcompanhamentoOsResponseDTO(
                osId,
                StatusOrdemServico.EM_EXECUCAO,
                "Em execução",
                "ABC1234",
                "Fiesta 2020",
                "João Mecânico",
                List.of(
                        new AcompanhamentoOsResponseDTO.ItemServicoDTO("Troca de óleo", BigDecimal.valueOf(50.00))
                ),
                List.of(
                        new AcompanhamentoOsResponseDTO.ItemPecaDTO("Filtro de óleo", 1, BigDecimal.valueOf(30.00))
                ),
                BigDecimal.valueOf(80.00),
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusHours(1),
                null
        );
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens - deve listar ordens com sucesso")
    void testListarOrdensComSucesso() throws Exception {
        List<AcompanhamentoOsResponseDTO> ordens = List.of(acompanhamentoDto);
        when(consultarUseCase.listarPorCliente(clienteId))
                .thenReturn(ordens);

        mockMvc.perform(get("/clientes/{clienteId}/ordens", clienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados", hasSize(1)))
                .andExpect(jsonPath("$.dados[0].id").value(osId))
                .andExpect(jsonPath("$.dados[0].veiculoPlaca").value("ABC1234"))
                .andExpect(jsonPath("$.dados[0].status").value("EM_EXECUCAO"));

        verify(consultarUseCase, times(1)).listarPorCliente(clienteId);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens - deve retornar vazio quando não há ordens")
    void testListarOrdensVazio() throws Exception {
        when(consultarUseCase.listarPorCliente(clienteId))
                .thenReturn(List.of());

        mockMvc.perform(get("/clientes/{clienteId}/ordens", clienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados", hasSize(0)));

        verify(consultarUseCase, times(1)).listarPorCliente(clienteId);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens/{osId} - deve buscar detalhe com sucesso")
    void testBuscarDetalheComSucesso() throws Exception {
        when(consultarUseCase.buscarDetalhe(clienteId, osId))
                .thenReturn(acompanhamentoDto);

        mockMvc.perform(get("/clientes/{clienteId}/ordens/{osId}", clienteId, osId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados[0].id").value(osId))
                .andExpect(jsonPath("$.dados[0].veiculoModelo").value("Fiesta 2020"))
                .andExpect(jsonPath("$.dados[0].mecanicoNome").value("João Mecânico"))
                .andExpect(jsonPath("$.dados[0].valorTotal").value(80.00));

        verify(consultarUseCase, times(1)).buscarDetalhe(clienteId, osId);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens/{osId} - deve retornar 404 quando OS não encontrada")
    void testBuscarDetalheNaoEncontrado() throws Exception {
        when(consultarUseCase.buscarDetalhe(clienteId, osId))
                .thenThrow(new OrdemServicoNaoEncontradaException("Ordem de servico nao encontrada"));

        mockMvc.perform(get("/clientes/{clienteId}/ordens/{osId}", clienteId, osId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(consultarUseCase, times(1)).buscarDetalhe(clienteId, osId);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens - deve validar UUID do cliente")
    void testListarOrdensComUuidInvalido() throws Exception {
        mockMvc.perform(get("/clientes/uuid-invalido/ordens")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /clientes/{clienteId}/ordens - deve retornar 401 sem autenticação")
    void testListarOrdensesSemAutenticacao() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens", clienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens/{osId} - deve validar osId como Long")
    void testBuscarDetalheComOsIdInvalido() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens/invalido", clienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
