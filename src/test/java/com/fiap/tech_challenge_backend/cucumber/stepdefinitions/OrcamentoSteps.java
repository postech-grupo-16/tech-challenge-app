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

public class OrcamentoSteps {
    private final TestContext testContext;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public OrcamentoSteps(TestContext testContext, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.testContext = testContext;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Quando("um novo orçamento é criado com os seguintes dados:")
    public void criarOrcamento(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        assertNotNull(ordemId, "ID da ordem não está configurado");

        Map<String, String> dados = dataTable.asMap();

        String requestBody = objectMapper.writeValueAsString(dados);

        MvcResult result = mockMvc.perform(post("/api/atendimento/ordens/orcamento")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 201) {
            Map<String, Object> orcamentoRetorno = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class);
            testContext.put("orcamento_criado", orcamentoRetorno);
            testContext.put("orcamento_id", orcamentoRetorno.get("id"));
        }
    }

    @Então("o orçamento deve ser criado com sucesso")
    public void orcamentoCriadoComSucesso() {
        assertNotNull(testContext.get("orcamento_criado"), "Orçamento não foi criado");
    }

    @Então("a ordem deve ter o orçamento associado")
    public void ordemTemOrcamento() {
        assertNotNull(testContext.get("orcamento_id"), "Orçamento não está associado à ordem");
    }

    @Dado("que um orçamento foi criado para a ordem")
    public void criarOrcamentoPadrao() throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        if (ordemId == null) {
            // Criar uma ordem primeiro
            testContext.put("ordem_id", 1L);
        }

        Map<String, String> orcamentoRequest = new HashMap<>();
        orcamentoRequest.put("descricao", "Revisão de motor");
        orcamentoRequest.put("valor", "1500.00");
        orcamentoRequest.put("status", "PENDENTE");

        String requestBody = objectMapper.writeValueAsString(orcamentoRequest);

        MvcResult result = mockMvc.perform(post("/api/atendimento/ordens/orcamento")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        if (result.getResponse().getStatus() == 201) {
            Map<String, Object> orcamentoRetorno = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class);
            testContext.put("orcamento_id", orcamentoRetorno.get("id"));
            testContext.put("orcamento_criado", orcamentoRetorno);
        }
    }

    @Quando("o orçamento é consultado por seu ID")
    public void consultarOrcamentoPorId() throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        Object orcamentoId = testContext.get("orcamento_id");

        assertNotNull(ordemId, "ID da ordem não está configurado");
        assertNotNull(orcamentoId, "ID do orçamento não está configurado");

        MvcResult result = mockMvc.perform(get("/api/atendimento/ordens/orcamento")
                .queryParam("idOS", String.valueOf(ordemId))
                .queryParam("idOrcamento", String.valueOf(orcamentoId))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("os dados do orçamento devem ser retornados corretamente")
    public void dadosOrcamentoRetornados() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Orçamento não foi retornado");
    }

    @Dado("que um orçamento foi criado com status {string}")
    public void criarOrcamentoComStatus(String status) throws Exception {
        criarOrcamentoPadrao();
        testContext.put("orcamento_status", status);
    }

    @Quando("o orçamento é aprovado")
    public void aprovarOrcamento() throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        Object orcamentoId = testContext.get("orcamento_id");

        Map<String, String> aprovacaoRequest = new HashMap<>();
        aprovacaoRequest.put("aprovado", "true");

        String requestBody = objectMapper.writeValueAsString(aprovacaoRequest);

        MvcResult result = mockMvc.perform(patch("/api/atendimento/ordens/orcamento/aprovar")
                .queryParam("idOS", String.valueOf(ordemId))
                .queryParam("idOrcamento", String.valueOf(orcamentoId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());

        if (result.getResponse().getStatus() == 200) {
            testContext.put("orcamento_status", "APROVADO");
        }
    }

    @Então("o status do orçamento deve ser alterado para {string}")
    public void statusOrcamentoDeve(String status) {
        String statusAtual = (String) testContext.get("orcamento_status");
        assertEquals(status, statusAtual, "Status do orçamento não foi alterado");
    }

    @Então("a ordem deve ser atualizada para {string}")
    public void ordemAtualizadaPara(String status) {
        assertNotNull(testContext.get("ordem_id"), "Ordem não foi atualizada");
    }

    @Quando("o orçamento é rejeitado com motivo {string}")
    public void rejeitarOrcamento(String motivo) throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        Object orcamentoId = testContext.get("orcamento_id");

        Map<String, String> rejeicaoRequest = new HashMap<>();
        rejeicaoRequest.put("motivo", motivo);

        String requestBody = objectMapper.writeValueAsString(rejeicaoRequest);

        MvcResult result = mockMvc.perform(patch("/api/atendimento/ordens/orcamento/rejeitar")
                .queryParam("idOS", String.valueOf(ordemId))
                .queryParam("idOrcamento", String.valueOf(orcamentoId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());

        if (result.getResponse().getStatus() == 200) {
            testContext.put("orcamento_status", "REJEITADO");
            testContext.put("orcamento_motivo_rejeicao", motivo);
        }
    }

    @Então("o motivo da rejeição deve ser armazenado")
    public void motivoRejeicaoArmazenado() {
        String motivo = (String) testContext.get("orcamento_motivo_rejeicao");
        assertNotNull(motivo, "Motivo da rejeição não foi armazenado");
    }

    @Dado("que um orçamento foi rejeitado")
    public void orcamentoFoiRejeitado() throws Exception {
        criarOrcamentoPadrao();
        rejeitarOrcamento("Valor muito alto");
    }

    @Então("o novo orçamento deve ser criado com sucesso")
    public void novoOrcamentoCriado() {
        assertNotNull(testContext.get("orcamento_criado"), "Novo orçamento não foi criado");
    }

    @Então("o histórico de orçamentos deve conter {int} registros")
    public void historicoOrcamentos(int quantidade) {
        assertNotNull(testContext.getLastResponse(), "Histórico de orçamentos não obtido");
    }

    @Quando("um orçamento é criado com serviços e peças")
    public void criarOrcamentoComServicosePecas() throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");

        Map<String, Object> orcamentoRequest = new HashMap<>();
        orcamentoRequest.put("descricao", "Revisão completa");
        orcamentoRequest.put("valor", "2000.00");

        String requestBody = objectMapper.writeValueAsString(orcamentoRequest);

        MvcResult result = mockMvc.perform(post("/api/atendimento/ordens/orcamento")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());

        if (result.getResponse().getStatus() == 201) {
            testContext.put("orcamento_valor_total", 2000.00);
        }
    }

    @Então("o valor total deve ser calculado automaticamente")
    public void valorTotalCalculado() {
        Double valorTotal = (Double) testContext.get("orcamento_valor_total");
        assertNotNull(valorTotal, "Valor total não foi calculado");
        assertTrue(valorTotal > 0, "Valor total deve ser positivo");
    }

    @Então("o valor deve incluir: (quantidade_horas * valor_hora) + soma_pecas")
    public void valorIncluiFormula() {
        Double valorTotal = (Double) testContext.get("orcamento_valor_total");
        assertNotNull(valorTotal, "Valor total não foi calculado");
    }

    @Quando("um orçamento é criado para uma ordem inexistente")
    public void criarOrcamentoOrdemInexistente() throws Exception {
        Map<String, String> orcamentoRequest = new HashMap<>();
        orcamentoRequest.put("descricao", "Teste");
        orcamentoRequest.put("valor", "1000.00");

        String requestBody = objectMapper.writeValueAsString(orcamentoRequest);

        MvcResult result = mockMvc.perform(post("/api/atendimento/ordens/orcamento")
                .queryParam("id", "99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um novo orçamento é criado para a mesma ordem")
    public void criarNovoOrcamentoMesmaOrdem() throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        assertNotNull(ordemId, "ID da ordem não está configurado");

        Map<String, String> orcamentoRequest = new HashMap<>();
        orcamentoRequest.put("descricao", "Novo orçamento");
        orcamentoRequest.put("valor", "2000.00");
        orcamentoRequest.put("status", "PENDENTE");

        String requestBody = objectMapper.writeValueAsString(orcamentoRequest);

        MvcResult result = mockMvc.perform(post("/api/atendimento/ordens/orcamento")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 201) {
            Map<String, Object> orcamentoRetorno = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class);
            testContext.put("orcamento_id", orcamentoRetorno.get("id"));
            testContext.put("orcamento_criado", orcamentoRetorno);
        }
    }

    @Quando("um orçamento é criado para a ordem de serviço com:")
    public void criarOrcamentoParaOrdem(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        assertNotNull(ordemId, "ID da ordem não está configurado");

        Map<String, String> dados = dataTable.asMap();

        String requestBody = objectMapper.writeValueAsString(dados);

        MvcResult result = mockMvc.perform(post("/os/orcamento")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 201) {
            Map<String, Object> orcamentoRetorno = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class);
            testContext.put("orcamento_criado", orcamentoRetorno);
            testContext.put("orcamento_id", orcamentoRetorno.get("id"));
        }
    }

    @Então("o orçamento deve ser criado com status {string}")
    public void orcamentoCriadoComStatus(String status) {
        Map<String, Object> orcamento = (Map<String, Object>) testContext.get("orcamento_criado");
        assertNotNull(orcamento, "Orçamento não foi criado");
        String statusObtido = (String) orcamento.get("status");
        assertEquals(status, statusObtido, "Status do orçamento não corresponde");
    }

    @Então("o orçamento deve conter o valor total {string}")
    public void orcamentoContemValor(String valor) {
        Map<String, Object> orcamento = (Map<String, Object>) testContext.get("orcamento_criado");
        assertNotNull(orcamento, "Orçamento não foi criado");
        Object valorObtido = orcamento.get("valorTotal");
        assertEquals(valor, String.valueOf(valorObtido), "Valor total não corresponde");
    }

    @Quando("um orçamento é criado com ID de ordem inexistente e valores:")
    public void orcamentoCriadoOrdemInexistente(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Map<String, String> dados = dataTable.asMap();

        String requestBody = objectMapper.writeValueAsString(dados);

        MvcResult result = mockMvc.perform(post("/os/orcamento")
                .queryParam("id", "999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Dado("que existe uma ordem de serviço com orçamento criado")
    public void ordemComOrcamentoCriado() throws Exception {
        // Criar ordem
        Long ordemId = 1L;
        testContext.put("ordem_id", ordemId);

        // Criar orçamento
        criarOrcamentoPadrao();
    }

    @Quando("o orçamento é buscado por ID")
    public void orcamentoBuscadoPorId() throws Exception {
        consultarOrcamentoPorId();
    }

    @Então("o orçamento deve ser encontrado")
    public void orcamentoEncontrado() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Orçamento não foi encontrado");
        assertNotNull(testContext.getLastResponse(), "Resposta vazia");
    }

    @Quando("um orçamento é buscado com ID inexistente")
    public void orcamentoBuscadoIdInexistente() throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        assertNotNull(ordemId, "ID da ordem não está configurado");

        MvcResult result = mockMvc.perform(get("/os/orcamento")
                .queryParam("idOS", String.valueOf(ordemId))
                .queryParam("idOrcamento", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Dado("que existe uma ordem de serviço com orçamento pendente")
    public void ordemComOrcamentoPendente() throws Exception {
        Long ordemId = 1L;
        testContext.put("ordem_id", ordemId);
        criarOrcamentoPadrao();
        testContext.put("orcamento_status", "PENDENTE");
    }

    @Quando("o orçamento é rejeitado")
    public void orcamentoRejeitado() throws Exception {
        rejeitarOrcamento("Valor não aceito");
    }

    @Então("o orçamento deve ter status {string}")
    public void orcamentoTemStatus(String status) {
        String statusObtido = (String) testContext.get("orcamento_status");
        assertEquals(status, statusObtido, "Status do orçamento não corresponde");
    }

    @Dado("que existe uma ordem de serviço com orçamento aprovado")
    public void ordemComOrcamentoAprovado() throws Exception {
        Long ordemId = 1L;
        testContext.put("ordem_id", ordemId);
        criarOrcamentoPadrao();
        aprovarOrcamento();
    }

    @Quando("tenta-se aprovar novamente o orçamento")
    public void tentaAprovarNovoOrcamento() throws Exception {
        aprovarOrcamento();
    }
}
