package com.fiap.tech_challenge_backend.cucumber.stepdefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.cucumber.config.TestContext;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ValidationSteps {
    private final TestContext testContext;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ValidationSteps(TestContext testContext, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.testContext = testContext;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Quando("um cliente é cadastrado com CNPJ inválido {string}")
    public void cadastrarClienteCnpjInvalido(String cnpj) throws Exception {
        Map<String, String> clienteRequest = new HashMap<>();
        clienteRequest.put("cpfCnpj", cnpj);
        clienteRequest.put("nome", "Empresa");
        clienteRequest.put("email", "empresa@test.com");
        clienteRequest.put("telefone", "1133333333");

        String requestBody = objectMapper.writeValueAsString(clienteRequest);

        MvcResult result = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um cliente é cadastrado sem email")
    public void cadastrarClienteSemEmail() throws Exception {
        Map<String, String> clienteRequest = new HashMap<>();
        clienteRequest.put("cpfCnpj", "12345678901");
        clienteRequest.put("nome", "Cliente");
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

    @Quando("um cliente é cadastrado sem telefone")
    public void cadastrarClienteSemTelefone() throws Exception {
        Map<String, String> clienteRequest = new HashMap<>();
        clienteRequest.put("cpfCnpj", "12345678901");
        clienteRequest.put("nome", "Cliente");
        clienteRequest.put("email", "cliente@test.com");

        String requestBody = objectMapper.writeValueAsString(clienteRequest);

        MvcResult result = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("uma movimentação de estoque é registrada com quantidade negativa")
    public void movimentacaoQuantidadeNegativa() throws Exception {
        Map<String, Object> movimentacaoRequest = new HashMap<>();
        movimentacaoRequest.put("quantidade", -5);
        movimentacaoRequest.put("tipo", "SAIDA");

        String requestBody = objectMapper.writeValueAsString(movimentacaoRequest);

        MvcResult result = mockMvc.perform(post("/estoque/movimentacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Dado("que uma ordem tem status {string}")
    public void ordemTemStatusValidacao(String status) {
        testContext.put("ordem_status_validacao", status);
    }

    @Quando("a ordem tenta transicionar para um status inválido {string}")
    public void tentarTransicaoInvalidaValidacao(String statusInvalido) throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        if (ordemId == null) {
            ordemId = 1L;
        }

        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("novoStatus", statusInvalido);
        statusRequest.put("mecanicoId", "1");

        String requestBody = objectMapper.writeValueAsString(statusRequest);

        MvcResult result = mockMvc.perform(patch("/api/atendimento/ordens/status")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um endpoint protegido é acessado sem autenticação")
    public void acessarSemAutenticacao() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/atendimento/ordens")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Dado("que um usuário tem perfil {string}")
    public void usuariomPerfil(String perfil) {
        testContext.put("usuario_perfil", perfil);
    }

    @Quando("tenta acessar um endpoint apenas para {string}")
    public void tentarAcessarEndpointAdmin(String perfil) throws Exception {
        // Simulando acesso sem credencial ADMIN
        MvcResult result = mockMvc.perform(delete("/api/atendimento/ordens/remover")
                .queryParam("id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um cliente é cadastrado com nome inválido")
    public void clienteComNomeInvalido() throws Exception {
        Map<String, String> clienteRequest = new HashMap<>();
        clienteRequest.put("cpfCnpj", "12345678901");
        clienteRequest.put("nome", "");
        clienteRequest.put("email", "cliente@test.com");
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

    @Quando("um cliente é cadastrado com email inválido")
    public void clienteComEmailInvalido() throws Exception {
        Map<String, String> clienteRequest = new HashMap<>();
        clienteRequest.put("cpfCnpj", "12345678901");
        clienteRequest.put("nome", "Cliente");
        clienteRequest.put("email", "email_invalido");
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

    @Quando("um cliente é cadastrado com telefone inválido")
    public void clienteComTelefoneInvalido() throws Exception {
        Map<String, String> clienteRequest = new HashMap<>();
        clienteRequest.put("cpfCnpj", "12345678901");
        clienteRequest.put("nome", "Cliente");
        clienteRequest.put("email", "cliente@test.com");
        clienteRequest.put("telefone", "123");

        String requestBody = objectMapper.writeValueAsString(clienteRequest);

        MvcResult result = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um orçamento é criado com valor negativo")
    public void orcamentoValorNegativo() throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        if (ordemId == null) {
            ordemId = 1L;
        }

        Map<String, String> orcamentoRequest = new HashMap<>();
        orcamentoRequest.put("descricao", "Teste");
        orcamentoRequest.put("valor", "-1000.00");

        String requestBody = objectMapper.writeValueAsString(orcamentoRequest);

        MvcResult result = mockMvc.perform(post("/api/atendimento/ordens/orcamento")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("a mensagem de validação deve ser clara")
    public void mensagemValidacaoClara() {
        String resposta = testContext.getLastResponse();
        assertNotNull(resposta, "Mensagem de validação não encontrada");
        assertFalse(resposta.isEmpty(), "Mensagem de validação está vazia");
    }
}
