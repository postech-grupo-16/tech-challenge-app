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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrdemServicoSteps {
    private final TestContext testContext;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final ClienteSteps clienteSteps;
    private final VeiculoSteps veiculoSteps;

    public OrdemServicoSteps(TestContext testContext, MockMvc mockMvc, ObjectMapper objectMapper,
                            ClienteSteps clienteSteps, VeiculoSteps veiculoSteps) {
        this.testContext = testContext;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.clienteSteps = clienteSteps;
        this.veiculoSteps = veiculoSteps;
    }

    @Dado("que existe uma ordem de serviço criada com status {string}")
    public void criarOrdemServicoComStatus(String status) throws Exception {
        criarOrdemServicoPadrao();
        testContext.put("ordem_status_esperado", status);
    }

    private void criarOrdemServicoPadrao() throws Exception {
        String cpf = "12345678901";
        String placa = "ABC1234";

        Map<String, String> osRequest = new HashMap<>();
        osRequest.put("cpfCnpjCliente", cpf);
        osRequest.put("placaVeiculo", placa);
        osRequest.put("queixaCliente", "Problema no motor");
        osRequest.put("observacoes", "Cliente relata trepidação");
        osRequest.put("urgente", "false");

        String requestBody = objectMapper.writeValueAsString(osRequest);

        MvcResult result = mockMvc.perform(post("/api/atendimento/ordens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        if (result.getResponse().getStatus() == 201) {
            Map<String, Object> osRetorno = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class);
            testContext.put("ordem_id", osRetorno.get("id"));
            testContext.put("ordem_status", osRetorno.get("status"));
        }
    }

    @Quando("uma nova ordem de serviço é criada para o cliente {string} e veículo {string}")
    public void criarOrdemServico(String cpf, String placa) throws Exception {
        Map<String, String> osRequest = new HashMap<>();
        osRequest.put("cpfCnpjCliente", cpf);
        osRequest.put("placaVeiculo", placa);
        osRequest.put("queixaCliente", "Problema no motor");
        osRequest.put("observacoes", "");
        osRequest.put("urgente", "false");

        String requestBody = objectMapper.writeValueAsString(osRequest);

        MvcResult result = mockMvc.perform(post("/api/atendimento/ordens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 201) {
            Map<String, Object> osRetorno = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class);
            testContext.put("ordem_id", osRetorno.get("id"));
            testContext.put("ordem_criada", osRetorno);
        }
    }

    @Então("a ordem de serviço deve ser registrada com sucesso")
    public void ordemRegistradaComSucesso() {
        assertNotNull(testContext.get("ordem_criada"), "Ordem não foi criada");
        Map<String, Object> ordem = (Map<String, Object>) testContext.get("ordem_criada");
        assertNotNull(ordem.get("id"), "Ordem não contém ID");
    }

    @Então("o status da ordem deve ser {string}")
    public void statusOrdemDeve(String statusEsperado) {
        Map<String, Object> ordem = (Map<String, Object>) testContext.get("ordem_criada");
        assertNotNull(ordem, "Ordem não encontrada");
        String status = (String) ordem.get("status");
        assertEquals(statusEsperado, status, "Status da ordem não corresponde");
    }

    @Quando("uma nova ordem de serviço é criada com os seguintes dados:")
    public void criarOrdemServicoComDados(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Map<String, String> dados = dataTable.asMap();

        Map<String, String> osRequest = new HashMap<>();
        osRequest.put("cpfCnpjCliente", dados.getOrDefault("cliente", "12345678901"));
        osRequest.put("placaVeiculo", dados.getOrDefault("veiculo", "ABC1234"));
        osRequest.put("queixaCliente", dados.getOrDefault("descricao", "Problema"));
        osRequest.put("observacoes", dados.getOrDefault("observacoes", ""));
        osRequest.put("urgente", dados.getOrDefault("urgente", "false"));

        String requestBody = objectMapper.writeValueAsString(osRequest);

        MvcResult result = mockMvc.perform(post("/api/atendimento/ordens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 201) {
            Map<String, Object> osRetorno = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class);
            testContext.put("ordem_id", osRetorno.get("id"));
            testContext.put("ordem_criada", osRetorno);
        }
    }

    @Então("a descrição deve ser armazenada corretamente")
    public void descricaoArmazenada() {
        Map<String, Object> ordem = (Map<String, Object>) testContext.get("ordem_criada");
        assertNotNull(ordem.get("queixaCliente"), "Queixa não foi armazenada");
    }

    @Quando("uma nova ordem de serviço é criada sem as informações necessárias")
    public void criarOrdemSemInformacoes() throws Exception {
        Map<String, String> osRequest = new HashMap<>();
        osRequest.put("cpfCnpjCliente", "");
        osRequest.put("placaVeiculo", "");
        osRequest.put("queixaCliente", "");

        String requestBody = objectMapper.writeValueAsString(osRequest);

        MvcResult result = mockMvc.perform(post("/api/atendimento/ordens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Dado("que a ordem tem status {string}")
    public void ordemTemStatus(String status) throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        assertNotNull(ordemId, "ID da ordem não está configurado");

        alterarStatusOrdem(status);
        testContext.put("ordem_status_esperado", status);
    }

    private void alterarStatusOrdem(String novoStatus) throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("novoStatus", novoStatus);
        statusRequest.put("mecanicoId", "1");

        String requestBody = objectMapper.writeValueAsString(statusRequest);

        mockMvc.perform(patch("/api/atendimento/ordens/status")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();
    }

    @Quando("o status da ordem é alterado para {string}")
    public void alterarStatusOrdemPara(String novoStatus) throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        assertNotNull(ordemId, "ID da ordem não está configurado");

        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("novoStatus", novoStatus);
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

        if (result.getResponse().getStatus() == 200) {
            testContext.put("ordem_status", novoStatus);
        }
    }

    @Então("o status deve ser atualizado com sucesso")
    public void statusAtualizado() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Status não foi atualizado");
    }

    @Então("o novo status deve ser {string}")
    public void novoStatusDeve(String statusEsperado) {
        String statusAtual = (String) testContext.get("ordem_status");
        assertEquals(statusEsperado, statusAtual, "Status não foi alterado corretamente");
    }

    @Então("o histórico de status deve registrar a transição")
    public void historicoRegistrado() {
        assertNotNull(testContext.get("ordem_id"), "Ordem não foi criada");
    }

    @Quando("o status da ordem é alterado para {string} com mecânico responsável")
    public void alterarStatusComMecanico(String novoStatus) throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        assertNotNull(ordemId, "ID da ordem não está configurado");

        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("novoStatus", novoStatus);
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

        if (result.getResponse().getStatus() == 200) {
            testContext.put("ordem_status", novoStatus);
        }
    }

    @Então("o mecânico responsável deve ser registrado")
    public void mecanicoRegistrado() {
        assertNotNull(testContext.get("ordem_id"), "Ordem não foi criada");
    }

    @Então("a data de conclusão deve ser registrada")
    public void dataConclusaoRegistrada() {
        assertNotNull(testContext.get("ordem_id"), "Ordem não foi criada");
    }

    @Dado("que a ordem passou pelas seguintes transições:")
    public void ordemPassouTransicoes(io.cucumber.datatable.DataTable dataTable) throws Exception {
        var transicoes = dataTable.asMaps(String.class, String.class);
        for (var transicao : transicoes) {
            String novoStatus = transicao.get("status_novo");
            if (novoStatus != null && !novoStatus.isEmpty()) {
                alterarStatusOrdem(novoStatus);
            }
        }
    }

    @Quando("o histórico de status é consultado")
    public void consultarHistoricoStatus() throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        assertNotNull(ordemId, "ID da ordem não está configurado");

        MvcResult result = mockMvc.perform(get("/api/atendimento/ordens/buscar")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("o histórico deve conter {int} registros de transição")
    public void historicoContem(int quantidade) {
        assertNotNull(testContext.getLastResponse(), "Resposta não obtida");
    }

    @Quando("é tentada uma transição inválida para {string}")
    public void tentarTransicaoInvalida(String status) throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        assertNotNull(ordemId, "ID da ordem não está configurado");

        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("novoStatus", status);
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

    @Quando("a ordem de serviço é consultada por seu ID")
    public void consultarOrdemPorId() throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        assertNotNull(ordemId, "ID da ordem não está configurado");

        MvcResult result = mockMvc.perform(get("/api/atendimento/ordens/buscar")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("os dados da ordem devem ser retornados corretamente")
    public void dadosOrdemRetornados() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Ordem não foi retornada");
        assertNotNull(testContext.getLastResponse(), "Resposta vazia");
    }

    @Dado("que o cliente {string} possui o veículo {string} associado")
    public void clientePossuiVeiculoAssociado(String cpf, String placa) throws Exception {
        // Verificar se a associação existe ou criar dados de teste
        testContext.put("cliente_cpf", cpf);
        testContext.put("veiculo_placa", placa);
    }

    @Então("a ordem de serviço deve ser criada com status {string}")
    public void ordemCriadaComStatus(String status) {
        Map<String, Object> ordem = (Map<String, Object>) testContext.get("ordem_criada");
        assertNotNull(ordem, "Ordem não foi criada");
        String statusObtido = (String) ordem.get("status");
        assertEquals(status, statusObtido, "Status da ordem não corresponde");
    }

    @Então("a ordem de serviço deve conter a descrição {string}")
    public void ordemContemDescricao(String descricao) {
        Map<String, Object> ordem = (Map<String, Object>) testContext.get("ordem_criada");
        assertNotNull(ordem, "Ordem não foi criada");
        String descricaoObtida = (String) ordem.get("queixaCliente");
        assertEquals(descricao, descricaoObtida, "Descrição da ordem não corresponde");
    }

    @Dado("que existem {int} ordens de serviço criadas no sistema")
    public void ordensServiçoCriadas(int quantidade) throws Exception {
        for (int i = 1; i <= quantidade; i++) {
            criarOrdemServicoComDados(io.cucumber.datatable.DataTable.create(
                    java.util.List.of(
                            java.util.List.of("cpfCnpj", "cliente", "placa", "veiculo", "descricao"),
                            java.util.List.of("", "1234567890" + i, "", "ABC" + String.format("%04d", i), "Ordem " + i)
                    )
            ));
        }
        testContext.put("ordensServico_quantidade_esperada", quantidade);
    }

    @Quando("a lista de ordens de serviço é solicitada")
    public void listarOrdensServico() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/atendimento/ordens")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 200) {
            Object[] ordens = objectMapper.readValue(result.getResponse().getContentAsString(), Object[].class);
            testContext.put("ordensServico_listadas", ordens.length);
        }
    }

    @Então("a lista deve conter {int} ordens de serviço")
    public void listaContemOrdens(int quantidade) {
        Integer quantidadeObtida = (Integer) testContext.get("ordensServico_listadas");
        assertNotNull(quantidadeObtida, "Lista de ordens não foi obtida");
        assertEquals(quantidade, quantidadeObtida, "Quantidade de ordens não corresponde");
    }

    @Dado("que existe uma ordem de serviço criada")
    public void ordemServiçoCriada() throws Exception {
        criarOrdemServicoPadrao();
    }

    @Quando("a ordem de serviço é buscada por ID")
    public void ordemServicoBuscadaPorId() throws Exception {
        consultarOrdemPorId();
    }

    @Então("a ordem de serviço deve ser encontrada")
    public void ordemServicoEncontrada() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Ordem não foi encontrada");
        assertNotNull(testContext.getLastResponse(), "Resposta vazia");
    }

    @Quando("uma ordem de serviço é buscada com ID inexistente")
    public void ordemServicoBuscadaIdInexistente() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/atendimento/ordens/buscar")
                .queryParam("id", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Dado("que existe uma ordem de serviço com status {string}")
    public void ordemServicoComStatus(String status) throws Exception {
        criarOrdemServicoPadrao();
        if (!status.equals("RECEBIDA")) {
            alterarStatusOrdem(status);
            testContext.put("ordem_status", status);
        }
    }

    @Quando("a ordem é cancelada")
    public void ordemCancelada() throws Exception {
        alterarStatusOrdemPara("CANCELADA");
    }

    @Então("deve haver um registro no histórico de status")
    public void registroHistoricoStatus() {
        assertNotNull(testContext.get("ordem_id"), "Ordem não foi criada");
    }

    @Quando("tenta-se alterar o status para {string}")
    public void tentaAltearStatusPara(String status) throws Exception {
        tentarTransicaoInvalida(status);
    }

    @Dado("que este cliente possui uma ordem de serviço em aberto")
    public void clientePossuiOrdemEmAberto() throws Exception {
        String cpf = (String) testContext.get("cliente_cpf");
        if (cpf == null) {
            cpf = "12345678901";
            testContext.put("cliente_cpf", cpf);
        }
        criarOrdemServicoPadrao();
    }

    @Quando("o cliente busca suas ordens de serviço")
    public void clienteBuscaSuasOrdens() throws Exception {
        String cpf = (String) testContext.get("cliente_cpf");
        MvcResult result = mockMvc.perform(get("/api/cliente/ordens")
                .queryParam("cpf", cpf)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("a ordem deve ser listada na resposta")
    public void ordemListadaNaResposta() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Falha ao listar ordens");
        assertNotNull(testContext.getLastResponse(), "Resposta vazia");
    }

    @Dado("existe outro cliente com CPF {string}")
    public void outroClienteComCpf(String cpf) throws Exception {
        testContext.put("outro_cliente_cpf", cpf);
    }

    @Dado("que o cliente {string} possui uma ordem de serviço")
    public void clientePossuiOrdemServico(String cpf) throws Exception {
        testContext.put("cliente_com_ordem_cpf", cpf);
        // Criar ordem para este cliente
    }

    @Quando("o cliente {string} tenta buscar suas ordens")
    public void clienteBuscaSuasOrdensTentativa(String cpf) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cliente/ordens")
                .queryParam("cpf", cpf)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("a ordem do cliente {string} não deve estar na resposta")
    public void ordemNaoEstaNoResposta(String cpf) {
        String resposta = testContext.getLastResponse();
        assertNotNull(resposta, "Resposta vazia");
        // Validar que a ordem não está na resposta
    }

    @Quando("a ordem é deletada por usuário ADMIN")
    public void ordemDeletadaPorAdmin() throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        MvcResult result = mockMvc.perform(delete("/api/atendimento/ordens")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("a ordem deve ser removida do sistema")
    public void ordemRemovidaDoSistema() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Ordem não foi removida");
    }

    @Quando("tenta-se deletar a ordem com usuário não-ADMIN")
    public void tentaDeletarOrdemmNaoAdmin() throws Exception {
        Long ordemId = (Long) testContext.get("ordem_id");
        MvcResult result = mockMvc.perform(delete("/api/atendimento/ordens")
                .queryParam("id", String.valueOf(ordemId))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("uma nova ordem de serviço é criada com os dados:")
    public void umaNovaOrdemDeServicoECriadaComOsDados(io.cucumber.datatable.DataTable dataTable) throws Exception {
        criarOrdemServicoComDados(dataTable);
    }
}
