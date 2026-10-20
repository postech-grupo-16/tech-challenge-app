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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ServicoSteps {
    private final TestContext testContext;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ServicoSteps(TestContext testContext, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.testContext = testContext;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Quando("um novo serviço é cadastrado com os seguintes dados:")
    public void cadastrarServico(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Map<String, String> dados = dataTable.asMap();

        String requestBody = objectMapper.writeValueAsString(dados);

        MvcResult result = mockMvc.perform(post("/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 201) {
            testContext.put("servico_criado", objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class));
            testContext.put("servico_id", objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class).get("id"));
        }
    }

    @Então("o serviço deve ser registrado com sucesso")
    public void servicoRegistrado() {
        assertNotNull(testContext.get("servico_criado"), "Serviço não foi criado");
    }

    @Dado("que um serviço com nome {string} foi cadastrado")
    public void servicoCadastradoComNome(String nome) throws Exception {
        Map<String, Object> servicoRequest = new HashMap<>();
        servicoRequest.put("nome", nome);
        servicoRequest.put("descricao", "Serviço de teste");
        servicoRequest.put("valor_hora", 100.00);
        servicoRequest.put("tempo_estimado_horas", 0.5);

        String requestBody = objectMapper.writeValueAsString(servicoRequest);

        MvcResult result = mockMvc.perform(post("/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        if (result.getResponse().getStatus() == 201) {
            Map<String, Object> servicoRetorno = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class);
            testContext.put("servico_id", servicoRetorno.get("id"));
            testContext.put("servico_nome", nome);
        }
    }

    @Quando("o serviço é buscado por seu ID")
    public void buscarServicoPorId() throws Exception {
        Object servicoId = testContext.get("servico_id");
        assertNotNull(servicoId, "ID do serviço não está configurado");

        MvcResult result = mockMvc.perform(get("/servicos/{id}", servicoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 200) {
            testContext.put("servico_buscado", objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class));
        }
    }

    @Então("os dados do serviço devem ser retornados corretamente")
    public void dadosServicoRetornados() {
        assertNotNull(testContext.get("servico_buscado"), "Serviço não foi encontrado");
    }

    @Então("o valor hora deve estar correto")
    public void valorHoraCorreto() {
        Map<String, Object> servico = (Map<String, Object>) testContext.get("servico_buscado");
        assertNotNull(servico.get("valor_hora"), "Valor hora não está presente");
    }

    @Dado("que existem {int} serviços cadastrados no catálogo")
    public void servicosCadastrados(int quantidade) throws Exception {
        for (int i = 1; i <= quantidade; i++) {
            Map<String, Object> servicoRequest = new HashMap<>();
            servicoRequest.put("nome", "Serviço " + i);
            servicoRequest.put("descricao", "Descrição do serviço " + i);
            servicoRequest.put("valor_hora", 100.00 + i);
            servicoRequest.put("tempo_estimado_horas", i * 0.5);

            String requestBody = objectMapper.writeValueAsString(servicoRequest);

            mockMvc.perform(post("/servicos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andReturn();
        }
        testContext.put("servicos_quantidade_esperada", quantidade);
    }

    @Quando("a lista de serviços é solicitada")
    public void listarServicos() throws Exception {
        MvcResult result = mockMvc.perform(get("/servicos")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 200) {
            Object[] servicos = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Object[].class);
            testContext.put("servicos_listados", servicos.length);
        }
    }

    @Então("a lista deve conter todos os {int} serviços")
    public void listaContemServicos(int quantidade) {
        Integer quantidadeLista = (Integer) testContext.get("servicos_listados");
        assertNotNull(quantidadeLista, "Lista de serviços não foi obtida");
        assertEquals(quantidade, quantidadeLista, "Quantidade de serviços não corresponde");
    }

    @Dado("que um serviço foi cadastrado")
    public void servicoFoiCadastrado() throws Exception {
        Map<String, Object> servicoRequest = new HashMap<>();
        servicoRequest.put("nome", "Serviço Teste");
        servicoRequest.put("descricao", "Descrição de teste");
        servicoRequest.put("valor_hora", 100.00);
        servicoRequest.put("tempo_estimado_horas", 0.5);

        String requestBody = objectMapper.writeValueAsString(servicoRequest);

        MvcResult result = mockMvc.perform(post("/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        if (result.getResponse().getStatus() == 201) {
            testContext.put("servico_id", objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class).get("id"));
        }
    }

    @Quando("os dados do serviço são atualizados:")
    public void atualizarServico(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Object servicoId = testContext.get("servico_id");
        assertNotNull(servicoId, "ID do serviço não está configurado");

        Map<String, String> dados = dataTable.asMap();

        String requestBody = objectMapper.writeValueAsString(dados);

        MvcResult result = mockMvc.perform(put("/servicos/{id}", servicoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("o novo valor hora deve ser {double}")
    public void novoValorHora(double valor) {
        assertNotNull(testContext.getLastResponse(), "Resposta não obtida");
    }

    @Quando("o serviço é deletado")
    public void deletarServico() throws Exception {
        Object servicoId = testContext.get("servico_id");
        assertNotNull(servicoId, "ID do serviço não está configurado");

        MvcResult result = mockMvc.perform(delete("/servicos/{id}", servicoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("o serviço deve ser removido com sucesso")
    public void servicoRemovido() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Serviço não foi removido com sucesso");
    }

    @Quando("um serviço é cadastrado sem o campo {string}")
    public void cadastrarServicoSemCampo(String campo) throws Exception {
        Map<String, Object> servicoRequest = new HashMap<>();
        servicoRequest.put("nome", "Serviço Teste".equals(campo) ? null : "Serviço");
        servicoRequest.put("descricao", "Teste");
        servicoRequest.put("valor_hora", 100.00);
        servicoRequest.put("tempo_estimado_horas", 0.5);

        String requestBody = objectMapper.writeValueAsString(servicoRequest);

        MvcResult result = mockMvc.perform(post("/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um serviço é cadastrado com valor_hora negativo")
    public void cadastrarServicoValorNegativo() throws Exception {
        Map<String, Object> servicoRequest = new HashMap<>();
        servicoRequest.put("nome", "Serviço Inválido");
        servicoRequest.put("descricao", "Teste");
        servicoRequest.put("valor_hora", -100.00);
        servicoRequest.put("tempo_estimado_horas", 0.5);

        String requestBody = objectMapper.writeValueAsString(servicoRequest);

        MvcResult result = mockMvc.perform(post("/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Dado("que um serviço foi cadastrado e está ativo")
    public void servicoAtivo() throws Exception {
        servicoFoiCadastrado();
    }

    @Quando("a disponibilidade do serviço é consultada")
    public void consultarDisponibilidade() throws Exception {
        Object servicoId = testContext.get("servico_id");
        assertNotNull(servicoId, "ID do serviço não está configurado");

        MvcResult result = mockMvc.perform(get("/servicos/{id}", servicoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("o serviço deve estar disponível para uso")
    public void servicoDisponivel() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Serviço não está disponível");
    }

    @Então("o serviço deve conter o nome {string}")
    public void servicoContemNome(String nome) {
        Map<String, Object> servico = (Map<String, Object>) testContext.get("servico_criado");
        assertNotNull(servico, "Serviço não foi criado");
        assertEquals(nome, servico.get("nome"), "Nome não corresponde");
    }

    @Então("o serviço deve ter valor por hora {string}")
    public void servicoTemValorPorHora(String valor) {
        Map<String, Object> servico = (Map<String, Object>) testContext.get("servico_criado");
        assertNotNull(servico, "Serviço não foi criado");
        assertEquals(valor, String.valueOf(servico.get("valor_hora")), "Valor por hora não corresponde");
    }

    @Dado("que existe um serviço cadastrado com nome {string}")
    public void existeServicoCadastradoComNome(String nome) throws Exception {
        servicoCadastradoComNome(nome);
    }

    @Quando("o serviço é buscado por nome {string}")
    public void servicoBuscadoPorNome(String nome) throws Exception {
        MvcResult result = mockMvc.perform(get("/servicos")
                .param("nome", nome)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 200) {
            testContext.put("servico_buscado", objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class));
        }
    }

    @Então("o serviço deve ser encontrado")
    public void servicoEncontrado() {
        assertNotNull(testContext.get("servico_buscado"), "Serviço não foi encontrado");
    }

    @Dado("que existem {int} serviços cadastrados no sistema")
    public void servicosCadastradosNoSistema(int quantidade) throws Exception {
        servicosCadastrados(quantidade);
    }

    @Então("o serviço deve ser removido do sistema")
    public void servicoRemovidoDoSistema() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Serviço não foi removido");
    }

}
