package com.fiap.tech_challenge_backend.cucumber.stepdefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.cucumber.config.TestContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ClienteSteps {
    private final TestContext testContext;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ClienteSteps(TestContext testContext, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.testContext = testContext;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Quando("um cliente é cadastrado com os seguintes dados:")
    public void cadastrarClienteComDados(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Map<String, String> dados = dataTable.asMap();
        Map<String, String> clienteRequest = new HashMap<>(dados);

        String requestBody = objectMapper.writeValueAsString(clienteRequest);

        MvcResult result = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 201) {
            testContext.put("cliente_criado", objectMapper.readValue(result.getResponse().getContentAsString(), Map.class));
            testContext.put("cpf_criado", dados.get("cpfCnpj"));
        }
    }

    @Então("o cliente deve ser criado com sucesso")
    public void clienteCriadoComSucesso() {
        assertNotNull(testContext.get("cliente_criado"), "Cliente não foi criado");
        Map<String, Object> cliente = (Map<String, Object>) testContext.get("cliente_criado");
        assertNotNull(cliente.get("cpfCnpj"), "Cliente não contém CPF/CNPJ");
    }

    @Dado("que um cliente foi cadastrado com CPF {string}")
    public void clienteCadastradoComCpf(String cpf) throws Exception {
        Map<String, String> clienteRequest = new HashMap<>();
        clienteRequest.put("cpfCnpj", cpf);
        clienteRequest.put("nome", "Cliente Teste");
        clienteRequest.put("email", "cliente@example.com");
        clienteRequest.put("telefone", "11999999999");

        String requestBody = objectMapper.writeValueAsString(clienteRequest);

        MvcResult result = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        if (result.getResponse().getStatus() == 201) {
            testContext.put("cliente_teste_cpf", cpf);
        }
    }

    @Quando("o cliente é buscado por CPF {string}")
    public void buscarClientePorCpf(String cpf) throws Exception {
        MvcResult result = mockMvc.perform(get("/clientes/{cpfCnpj}", cpf)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 200) {
            testContext.put("cliente_buscado", objectMapper.readValue(result.getResponse().getContentAsString(), Map.class));
        }
    }

    @Então("o cliente deve ser encontrado")
    public void clienteEncontrado() {
        assertNotNull(testContext.get("cliente_buscado"), "Cliente não foi encontrado");
    }

    @Dado("que existem {int} clientes cadastrados no sistema")
    public void clientesCadastrados(int quantidade) throws Exception {
        for (int i = 1; i <= quantidade; i++) {
            Map<String, String> clienteRequest = new HashMap<>();
            clienteRequest.put("cpfCnpj", String.format("1234567890%d", i));
            clienteRequest.put("nome", "Cliente " + i);
            clienteRequest.put("email", "cliente" + i + "@example.com");
            clienteRequest.put("telefone", "1199999999" + i);

            String requestBody = objectMapper.writeValueAsString(clienteRequest);

            mockMvc.perform(post("/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andReturn();
        }
        testContext.put("clientes_quantidade_esperada", quantidade);
    }

    @Quando("a lista de clientes é solicitada")
    public void listarClientes() throws Exception {
        MvcResult result = mockMvc.perform(get("/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 200) {
            Object[] clientes = objectMapper.readValue(result.getResponse().getContentAsString(), Object[].class);
            testContext.put("clientes_listados", clientes.length);
        }
    }

    @Então("a lista deve conter {int} clientes")
    public void listaContemClientes(int quantidade) {
        Integer quantidadeLista = (Integer) testContext.get("clientes_listados");
        assertNotNull(quantidadeLista, "Lista de clientes não foi obtida");
        assertEquals(quantidade, quantidadeLista, "Quantidade de clientes não corresponde");
    }

    @Quando("os dados do cliente são atualizados:")
    public void atualizarDadosCliente(io.cucumber.datatable.DataTable dataTable) throws Exception {
        String cpf = (String) testContext.get("cliente_teste_cpf");
        assertNotNull(cpf, "CPF do cliente não está configurado");

        Map<String, String> dados = dataTable.asMap();

        String requestBody = objectMapper.writeValueAsString(dados);

        MvcResult result = mockMvc.perform(put("/clientes/{cpfCnpj}", cpf)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("o cliente deve ser atualizado com sucesso")
    public void clienteAtualizado() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Cliente não foi atualizado com sucesso");
    }

    @Quando("o cliente é deletado")
    public void deletarCliente() throws Exception {
        String cpf = (String) testContext.get("cliente_teste_cpf");
        assertNotNull(cpf, "CPF do cliente não está configurado");

        MvcResult result = mockMvc.perform(delete("/clientes/{cpfCnpj}", cpf)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("o cliente deve ser removido com sucesso")
    public void clienteRemovido() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Cliente não foi removido com sucesso");
    }

    @Quando("um cliente é cadastrado com o mesmo CPF {string}")
    public void cadastrarClienteDuplicado(String cpf) throws Exception {
        Map<String, String> clienteRequest = new HashMap<>();
        clienteRequest.put("cpfCnpj", cpf);
        clienteRequest.put("nome", "Cliente Duplicado");
        clienteRequest.put("email", "duplicado@example.com");
        clienteRequest.put("telefone", "11999999999");

        String requestBody = objectMapper.writeValueAsString(clienteRequest);

        MvcResult result = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Dado("que existe um cliente cadastrado com CPF {string}")
    public void clienteExisteComCpf(String cpf) throws Exception {
        Map<String, String> clienteRequest = new HashMap<>();
        clienteRequest.put("cpfCnpj", cpf);
        clienteRequest.put("nome", "Cliente Teste");
        clienteRequest.put("email", "teste@example.com");
        clienteRequest.put("telefone", "11999999999");

        String requestBody = objectMapper.writeValueAsString(clienteRequest);

        MvcResult result = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        if (result.getResponse().getStatus() == 201) {
            testContext.put("cliente_teste_cpf", cpf);
        }
    }

    @Então("o cliente deve conter o CPF/CNPJ {string}")
    public void clienteContemCpf(String cpf) {
        Map<String, Object> cliente = (Map<String, Object>) testContext.get("cliente_criado");
        assertNotNull(cliente, "Cliente não foi criado");
        assertEquals(cpf, cliente.get("cpfCnpj"), "CPF/CNPJ não corresponde");
    }

    @Então("o cliente deve conter o nome {string}")
    public void clienteContemNome(String nome) {
        Map<String, Object> cliente = (Map<String, Object>) testContext.get("cliente_criado");
        assertNotNull(cliente, "Cliente não foi criado");
        assertEquals(nome, cliente.get("nome"), "Nome não corresponde");
    }

    @Quando("os dados do cliente são atualizados com CPF inexistente:")
    public void atualizarDadosClienteInexistente(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Map<String, String> dados = dataTable.asMap();
        String cpf = dados.get("cpfCnpj");

        Map<String, String> updateRequest = new HashMap<>(dados);
        updateRequest.remove("cpfCnpj");

        String requestBody = objectMapper.writeValueAsString(updateRequest);

        MvcResult result = mockMvc.perform(put("/clientes/{cpfCnpj}", cpf)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("ao buscar o cliente deletado por CPF {string} deve retornar 404")
    public void buscarClienteDeletadoRetorna404(String cpf) throws Exception {
        MvcResult result = mockMvc.perform(get("/clientes/{cpfCnpj}", cpf)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        assertEquals(404, result.getResponse().getStatus(), "Cliente não foi deletado corretamente");
    }

    @Quando("um cliente é cadastrado com CPF inválido {string}")
    public void cadastrarClienteComCpfInvalido(String cpf) throws Exception {
        Map<String, String> clienteRequest = new HashMap<>();
        clienteRequest.put("cpfCnpj", cpf);
        clienteRequest.put("nome", "Cliente Teste");
        clienteRequest.put("email", "teste@example.com");
        clienteRequest.put("telefone", "11999999999");

        String requestBody = objectMapper.writeValueAsString(clienteRequest);

        MvcResult result = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um cliente é cadastrado com nome com mais de 150 caracteres")
    public void cadastrarClienteNomeGrande() throws Exception {
        Map<String, String> clienteRequest = new HashMap<>();
        clienteRequest.put("cpfCnpj", "12345678901");
        clienteRequest.put("nome", "A".repeat(151));
        clienteRequest.put("email", "teste@example.com");
        clienteRequest.put("telefone", "11999999999");

        String requestBody = objectMapper.writeValueAsString(clienteRequest);

        MvcResult result = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Dado("que existe um cliente com CPF {string}")
    public void queExisteUmClienteComCPF(String cpf) throws Exception {
        clienteExisteComCpf(cpf);
    }
}
