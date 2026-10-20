package com.fiap.tech_challenge_backend.cadastro.adapters.in.web;

import com.fiap.tech_challenge_backend.cadastro.application.dto.AtualizarVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.VeiculoNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.*;
import com.fiap.tech_challenge_backend.config.TestSecurityConfigWithAuth;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VeiculoController.class)
@Import(TestSecurityConfigWithAuth.class)
@DisplayName("VeiculoController - Testes Unitários")
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CadastrarVeiculoUseCase cadastroVeiculoUseCase;

    @MockBean
    private ListarVeiculosUseCase listarVeiculosUseCase;

    @MockBean
    private BuscarVeiculoUseCase buscarVeiculoUseCase;

    @MockBean
    private AtualizarVeiculoUseCase atualizarVeiculoUseCase;

    @MockBean
    private DeletarVeiculoUseCase deletarVeiculoUseCase;

    private CadastroVeiculoRequest cadastroRequest;
    private CadastroVeiculoResponse cadastroResponse;
    private BuscarVeiculoResponse buscarResponse;

    @BeforeEach
    void setUp() {
        cadastroRequest = new CadastroVeiculoRequest("ABC1234", "Ford", "Fiesta", 2020, "Branco", "12345678901");
        cadastroResponse = new CadastroVeiculoResponse(UUID.randomUUID(), "ABC1234", "Ford", "Fiesta", 2020, "Branco", "12345678901");
        buscarResponse = new BuscarVeiculoResponse(UUID.randomUUID(), "ABC1234", "Ford", "Fiesta", 2020, "Branco");
    }

    @Test
    @WithMockUser
    @DisplayName("POST /veiculos - deve cadastrar veículo com sucesso")
    void testCadastrarVeiculoComSucesso() throws Exception {
        when(cadastroVeiculoUseCase.execute(any(CadastroVeiculoRequest.class)))
                .thenReturn(cadastroResponse);

        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cadastroRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Ford"))
                .andExpect(jsonPath("$.modelo").value("Fiesta"))
                .andExpect(jsonPath("$.ano").value(2020))
                .andExpect(jsonPath("$.cor").value("Branco"));

        verify(cadastroVeiculoUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /veiculos - deve listar todos os veículos")
    void testListarTodosVeiculos() throws Exception {
        List<BuscarVeiculoResponse> veiculos = Arrays.asList(
                new BuscarVeiculoResponse(UUID.randomUUID(), "ABC1234", "Ford", "Fiesta", 2020, "Branco"),
                new BuscarVeiculoResponse(UUID.randomUUID(), "XYZ5678", "Chevrolet", "Onix", 2021, "Preto")
        );

        when(listarVeiculosUseCase.execute()).thenReturn(veiculos);

        mockMvc.perform(get("/veiculos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].placa").value("ABC1234"))
                .andExpect(jsonPath("$[1].placa").value("XYZ5678"));

        verify(listarVeiculosUseCase, times(1)).execute();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /veiculos - deve retornar lista vazia quando não há veículos")
    void testListarVeiculosVazio() throws Exception {
        when(listarVeiculosUseCase.execute()).thenReturn(List.of());

        mockMvc.perform(get("/veiculos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(listarVeiculosUseCase, times(1)).execute();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /veiculos/{placa} - deve buscar veículo por placa")
    void testBuscarVeiculoPorPlaca() throws Exception {
        when(buscarVeiculoUseCase.execute("ABC1234")).thenReturn(buscarResponse);

        mockMvc.perform(get("/veiculos/ABC1234")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Ford"))
                .andExpect(jsonPath("$.modelo").value("Fiesta"));

        verify(buscarVeiculoUseCase, times(1)).execute("ABC1234");
    }

    @Test
    @WithMockUser
    @DisplayName("GET /veiculos/{placa} - deve retornar 404 quando veículo não encontrado")
    void testBuscarVeiculoNaoEncontrado() throws Exception {
        when(buscarVeiculoUseCase.execute("INVALID"))
                .thenThrow(new VeiculoNaoEncontradoException("INVALID"));

        mockMvc.perform(get("/veiculos/INVALID")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(buscarVeiculoUseCase, times(1)).execute("INVALID");
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /veiculos/{placa} - deve atualizar veículo com sucesso")
    void testAtualizarVeiculoComSucesso() throws Exception {
        BuscarVeiculoResponse atualizado = new BuscarVeiculoResponse(UUID.randomUUID(), "ABC1234", "Ford", "Fiesta Atualizado", 2020, "Branco");
        AtualizarVeiculoRequest atualizarRequest = new AtualizarVeiculoRequest("Fiesta Atualizado");

        when(atualizarVeiculoUseCase.execute(eq("ABC1234"), any(AtualizarVeiculoRequest.class)))
                .thenReturn(atualizado);

        mockMvc.perform(put("/veiculos/ABC1234")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Fiesta Atualizado"));

        verify(atualizarVeiculoUseCase, times(1)).execute(eq("ABC1234"), any());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /veiculos/{placa} - deve deletar veículo com sucesso")
    void testDeletarVeiculoComSucesso() throws Exception {
        doNothing().when(deletarVeiculoUseCase).execute("ABC1234");

        mockMvc.perform(delete("/veiculos/ABC1234")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Veículo removido com sucesso. Todas as ordens de serviço associadas ao veículo foram preservadas no histórico."));

        verify(deletarVeiculoUseCase, times(1)).execute("ABC1234");
    }

    @Test
    @WithMockUser
    @DisplayName("POST /veiculos - deve retornar 400 para dados inválidos")
    void testCadastrarVeiculoComDadosInvalidos() throws Exception {
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"placa\":\"\",\"marca\":\"\",\"modelo\":\"\",\"ano\":0,\"cor\":\"\",\"cpfCnpj\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /veiculos - deve retornar 400 quando ano inválido")
    void testCadastrarVeiculoComAnoInvalido() throws Exception {
        CadastroVeiculoRequest request = new CadastroVeiculoRequest("ABC1234", "Ford", "Fiesta", 1800, "Branco", "12345678901");

        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /veiculos - deve retornar 401 sem autenticação")
    void testCadastrarVeiculoSemAutenticacao() throws Exception {
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cadastroRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /veiculos - deve retornar 401 sem autenticação")
    void testListarVeiculosSemAutenticacao() throws Exception {
        mockMvc.perform(get("/veiculos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
