package com.fiap.tech_challenge_backend.cadastro.adapters.in.web;

import com.fiap.tech_challenge_backend.cadastro.application.dto.AtualizarClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarClienteResponse;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteResponse;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.*;
import com.fiap.tech_challenge_backend.config.TestSecurityConfig;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@Import(TestSecurityConfig.class)
@DisplayName("ClienteController Tests")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CadastrarClienteUseCase cadastroClienteUseCase;

    @MockBean
    private BuscarClienteUseCase buscarClienteUseCase;

    @MockBean
    private ListarClientesUseCase listarClientesUseCase;

    @MockBean
    private AtualizarClienteUseCase atualizarClienteUseCase;

    @MockBean
    private DeletarClienteUseCase deletarClienteUseCase;

    private CadastroClienteRequest cadastroRequest;
    private CadastroClienteResponse cadastroResponse;
    private BuscarClienteResponse buscarResponse;

    @BeforeEach
    void setUp() {
        cadastroRequest = new CadastroClienteRequest("João Silva", "joao@test.com", "senha123", "12345678901", "11999999999", "12345678", "Rua A", "123", "", "São Paulo", "SP");
        cadastroResponse = new CadastroClienteResponse(UUID.randomUUID(), "João Silva", "12345678901", "11999999999", "12345678", "Rua A", "123", "", "São Paulo", "SP", "ATIVO");
        buscarResponse = new BuscarClienteResponse(UUID.randomUUID(), "João Silva", "12345678901", "11999999999", "12345678", "Rua A", "123", "", "São Paulo", "SP", "ATIVO");
    }

    @Test
    @WithMockUser
    @DisplayName("POST /clientes - deve cadastrar cliente com sucesso")
    void testCadastrarClienteComSucesso() throws Exception {
        when(cadastroClienteUseCase.execute(any(CadastroClienteRequest.class)))
                .thenReturn(cadastroResponse);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cadastroRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpfCnpj").value("12345678901"))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(cadastroClienteUseCase, times(1)).execute(any());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes - deve listar todos os clientes")
    void testListarTodosClientes() throws Exception {
        List<BuscarClienteResponse> clientes = Arrays.asList(
                new BuscarClienteResponse(UUID.randomUUID(), "João", "12345678901", "11999999999", "12345678", "Rua A", "123", "", "São Paulo", "SP", "ATIVO"),
                new BuscarClienteResponse(UUID.randomUUID(), "Maria", "98765432100", "11988888888", "12345678", "Rua B", "456", "", "Rio de Janeiro", "RJ", "ATIVO")
        );

        when(listarClientesUseCase.execute()).thenReturn(clientes);

        mockMvc.perform(get("/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].cpfCnpj").value("12345678901"))
                .andExpect(jsonPath("$[1].cpfCnpj").value("98765432100"));

        verify(listarClientesUseCase, times(1)).execute();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{cpfCnpj} - deve buscar cliente por CPF")
    void testBuscarClientePorCpf() throws Exception {
        BuscarClienteResponse response = new BuscarClienteResponse(UUID.randomUUID(), "João Silva", "12345678901", "11999999999", "12345678", "Rua A", "123", "", "São Paulo", "SP", "ATIVO");
        when(buscarClienteUseCase.execute("12345678901")).thenReturn(response);

        mockMvc.perform(get("/clientes/12345678901")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpfCnpj").value("12345678901"))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(buscarClienteUseCase, times(1)).execute("12345678901");
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /clientes/{cpfCnpj} - deve atualizar cliente com sucesso")
    void testAtualizarClienteComSucesso() throws Exception {
        BuscarClienteResponse atualizado = new BuscarClienteResponse(UUID.randomUUID(), "João Silva Santos", "12345678901", "11999999999", "12345678", "Rua B", "456", "", "Rio de Janeiro", "RJ", "ATIVO");
        AtualizarClienteRequest atualizarRequest = new AtualizarClienteRequest("João Silva Santos", "11999999999", "12345678", "Rua B", "456", "", "Rio de Janeiro", "RJ");

        when(atualizarClienteUseCase.execute(eq("12345678901"), any(AtualizarClienteRequest.class)))
                .thenReturn(atualizado);

        mockMvc.perform(put("/clientes/12345678901")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atualizarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Santos"));

        verify(atualizarClienteUseCase, times(1)).execute(eq("12345678901"), any());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /clientes/{cpfCnpj} - deve deletar cliente com sucesso")
    void testDeletarClienteComSucesso() throws Exception {
        doNothing().when(deletarClienteUseCase).execute("12345678901");

        mockMvc.perform(delete("/clientes/12345678901")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem").value("Cliente deletado com sucesso"));

        verify(deletarClienteUseCase, times(1)).execute("12345678901");
    }

    @Test
    @WithMockUser
    @DisplayName("POST /clientes - deve retornar 400 para dados inválidos")
    void testCadastrarClienteComDadosInvalidos() throws Exception {
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cpfCnpj\":\"\",\"nome\":\"\",\"email\":\"\",\"telefone\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes - deve listar vazio quando não há clientes")
    void testListarClientesVazio() throws Exception {
        when(listarClientesUseCase.execute()).thenReturn(List.of());

        mockMvc.perform(get("/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(listarClientesUseCase, times(1)).execute();
    }
}
