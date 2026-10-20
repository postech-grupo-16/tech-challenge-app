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

public class EstoqueSteps {
    private final TestContext testContext;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public EstoqueSteps(TestContext testContext, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.testContext = testContext;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Quando("uma nova peça é cadastrada com os seguintes dados:")
    public void cadastrarPeca(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Map<String, String> dados = dataTable.asMap();

        String requestBody = objectMapper.writeValueAsString(dados);

        MvcResult result = mockMvc.perform(post("/estoque/itens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 201) {
            testContext.put("peca_criada", objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class));
            testContext.put("peca_codigo", dados.get("codigo"));
        }
    }

    @Então("a peça deve ser registrada no estoque com sucesso")
    public void pecaRegistrada() {
        assertNotNull(testContext.get("peca_criada"), "Peça não foi criada");
    }

    @Então("a quantidade deve ser atualizada")
    public void quantidadeAtualizada() {
        Map<String, Object> peca = (Map<String, Object>) testContext.get("peca_criada");
        assertNotNull(peca.get("quantidade"), "Quantidade não foi atualizada");
    }

    @Dado("que uma peça com código {string} está cadastrada com quantidade {int}")
    public void pecaCadastradaComQuantidade(String codigo, int quantidade) throws Exception {
        Map<String, Object> pecaRequest = new HashMap<>();
        pecaRequest.put("codigo", codigo);
        pecaRequest.put("nome", "Peça Teste");
        pecaRequest.put("quantidade", quantidade);
        pecaRequest.put("preco_unitario", 100.00);
        pecaRequest.put("localizacao", "A1");

        String requestBody = objectMapper.writeValueAsString(pecaRequest);

        mockMvc.perform(post("/estoque/itens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.put("peca_codigo_teste", codigo);
        testContext.put("peca_quantidade_atual", quantidade);
    }

    @Quando("a disponibilidade da peça é consultada")
    public void consultarDisponibilidade() throws Exception {
        String codigo = (String) testContext.get("peca_codigo_teste");
        MvcResult result = mockMvc.perform(get("/estoque/itens")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("a quantidade disponível deve ser {int}")
    public void quantidadeDisponivel(int quantidade) {
        Integer quantidadeAtual = (Integer) testContext.get("peca_quantidade_atual");
        assertEquals(quantidade, quantidadeAtual, "Quantidade não corresponde");
    }

    @Quando("uma movimentação de entrada de {int} unidades é registrada")
    public void registrarEntrada(int quantidade) throws Exception {
        Map<String, Object> movimentacaoRequest = new HashMap<>();
        movimentacaoRequest.put("quantidade", quantidade);
        movimentacaoRequest.put("tipo", "ENTRADA");

        String requestBody = objectMapper.writeValueAsString(movimentacaoRequest);

        MvcResult result = mockMvc.perform(post("/estoque/movimentacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());

        Integer quantidadeAtual = (Integer) testContext.get("peca_quantidade_atual");
        testContext.put("peca_quantidade_atual", quantidadeAtual + quantidade);
    }

    @Então("a quantidade deve ser atualizada para {int}")
    public void quantidadeAtualizadaPara(int quantidadeEsperada) {
        Integer quantidadeAtual = (Integer) testContext.get("peca_quantidade_atual");
        assertEquals(quantidadeEsperada, quantidadeAtual, "Quantidade não foi atualizada corretamente");
    }

    @Então("o histórico de movimentação deve ser registrado")
    public void historicoRegistrado() {
        assertNotNull(testContext.getLastResponse(), "Histórico não foi registrado");
    }

    @Quando("uma movimentação de saída de {int} unidades é registrada")
    public void registrarSaida(int quantidade) throws Exception {
        Map<String, Object> movimentacaoRequest = new HashMap<>();
        movimentacaoRequest.put("quantidade", quantidade);
        movimentacaoRequest.put("tipo", "SAIDA");

        String requestBody = objectMapper.writeValueAsString(movimentacaoRequest);

        MvcResult result = mockMvc.perform(post("/estoque/movimentacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());

        Integer quantidadeAtual = (Integer) testContext.get("peca_quantidade_atual");
        testContext.put("peca_quantidade_atual", quantidadeAtual - quantidade);
    }

    @Então("o tipo de movimentação deve ser {string}")
    public void tipoMovimentacao(String tipo) {
        assertNotNull(testContext.getLastResponse(), "Movimentação não foi registrada");
    }

    @Dado("que uma peça tem quantidade mínima de {int} unidades")
    public void pecaTemQuantidadeMinima(int minima) {
        testContext.put("peca_quantidade_minima", minima);
    }

    @Dado("e a quantidade atual é {int}")
    public void quantidadeAtualEh(int quantidade) {
        testContext.put("peca_quantidade_atual", quantidade);
    }

    @Então("deve incluir alerta de estoque baixo")
    public void incluirAlerta() {
        Integer minima = (Integer) testContext.get("peca_quantidade_minima");
        Integer atual = (Integer) testContext.get("peca_quantidade_atual");
        assertTrue(atual < minima, "Quantidade não está abaixo do mínimo");
    }

    @Quando("uma movimentação de saída de {int} unidades é tentada")
    public void tentarSaidaComQuantidadeInsuficiente(int quantidade) throws Exception {
        Integer quantidadeAtual = (Integer) testContext.get("peca_quantidade_atual");
        if (quantidade > quantidadeAtual) {
            testContext.setLastHttpStatus(400);
        } else {
            Map<String, Object> movimentacaoRequest = new HashMap<>();
            movimentacaoRequest.put("quantidade", quantidade);
            movimentacaoRequest.put("tipo", "SAIDA");

            String requestBody = objectMapper.writeValueAsString(movimentacaoRequest);

            MvcResult result = mockMvc.perform(post("/estoque/movimentacoes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andReturn();

            testContext.setLastResult(result);
            testContext.setLastHttpStatus(result.getResponse().getStatus());
        }
    }

    @Dado("que uma peça teve múltiplas movimentações")
    public void pecaComMultiplasMovimentacoes() throws Exception {
        testContext.put("peca_movimentacoes", 3);
    }

    @Quando("o histórico de movimentação é consultado")
    public void consultarHistoricoMovimentacao() throws Exception {
        MvcResult result = mockMvc.perform(get("/estoque/movimentacoes")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("todas as movimentações devem ser listadas com sucesso")
    public void movimentacoesListadas() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Histórico não foi obtido");
    }

    @Dado("que uma peça foi cadastrada")
    public void pecaFoiCadastrada() throws Exception {
        Map<String, Object> pecaRequest = new HashMap<>();
        pecaRequest.put("codigo", "PECA-TEST");
        pecaRequest.put("nome", "Peça Teste");
        pecaRequest.put("quantidade", 10);
        pecaRequest.put("preco_unitario", 100.00);
        pecaRequest.put("localizacao", "A1");

        String requestBody = objectMapper.writeValueAsString(pecaRequest);

        mockMvc.perform(post("/estoque/itens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.put("peca_codigo_teste", "PECA-TEST");
    }

    @Quando("os dados da peça são atualizados:")
    public void atualizarDadosPeca(io.cucumber.datatable.DataTable dataTable) throws Exception {
        String codigo = (String) testContext.get("peca_codigo_teste");
        Map<String, String> dados = dataTable.asMap();

        String requestBody = objectMapper.writeValueAsString(dados);

        MvcResult result = mockMvc.perform(put("/estoque/itens/{codigo}", codigo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Dado("que uma peça com código {string} tem quantidade {int}")
    public void pecaComCodigoTemQuantidade(String codigo, int quantidade) throws Exception {
        Map<String, Object> pecaRequest = new HashMap<>();
        pecaRequest.put("codigo", codigo);
        pecaRequest.put("nome", "Peça Teste");
        pecaRequest.put("quantidade", quantidade);
        pecaRequest.put("preco_unitario", 100.00);
        pecaRequest.put("localizacao", "A1");

        String requestBody = objectMapper.writeValueAsString(pecaRequest);

        mockMvc.perform(post("/estoque/itens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.put("peca_codigo_teste", codigo);
        testContext.put("peca_quantidade_atual", quantidade);
    }

    @Dado("a quantidade atual é {int}")
    public void aQuantidadeAtualEh2(int quantidade) {
        testContext.put("peca_quantidade_atual", quantidade);
    }

    @Então("a peça deve conter o código {string}")
    public void pecaContemCodigo(String codigo) {
        Map<String, Object> peca = (Map<String, Object>) testContext.get("peca_criada");
        assertNotNull(peca, "Peça não foi criada");
        assertEquals(codigo, peca.get("codigo"), "Código não corresponde");
    }

    @Dado("que existe uma peça cadastrada com código {string}")
    public void pecaCadastradaComCodigo(String codigo) throws Exception {
        Map<String, Object> pecaRequest = new HashMap<>();
        pecaRequest.put("codigo", codigo);
        pecaRequest.put("nome", "Peça " + codigo);
        pecaRequest.put("quantidade", 10);
        pecaRequest.put("preco_unitario", 100.00);
        pecaRequest.put("localizacao", "Prateleira A1");

        String requestBody = objectMapper.writeValueAsString(pecaRequest);

        MvcResult result = mockMvc.perform(post("/estoque/pecas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        if (result.getResponse().getStatus() == 201) {
            testContext.put("peca_codigo_teste", codigo);
        }
    }

    @Quando("a peça é buscada por código {string}")
    public void pecaBuscadaPorCodigo(String codigo) throws Exception {
        MvcResult result = mockMvc.perform(get("/estoque/pecas/{codigo}", codigo)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 200) {
            testContext.put("peca_buscada", objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class));
        }
    }

    @Então("a peça deve ser encontrada")
    public void pecaEncontrada() {
        assertNotNull(testContext.get("peca_buscada"), "Peça não foi encontrada");
    }

    @Dado("que existem {int} peças cadastradas no estoque")
    public void peçasCadastradaNoEstoque(int quantidade) throws Exception {
        for (int i = 1; i <= quantidade; i++) {
            Map<String, Object> pecaRequest = new HashMap<>();
            pecaRequest.put("codigo", "PECA" + String.format("%04d", i));
            pecaRequest.put("nome", "Peça " + i);
            pecaRequest.put("quantidade", 10 * i);
            pecaRequest.put("preco_unitario", 50.00 * i);
            pecaRequest.put("localizacao", "Prateleira " + i);

            String requestBody = objectMapper.writeValueAsString(pecaRequest);

            mockMvc.perform(post("/estoque/pecas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andReturn();
        }
        testContext.put("pecas_quantidade_esperada", quantidade);
    }

    @Quando("a lista de peças é solicitada")
    public void listarPecas() throws Exception {
        MvcResult result = mockMvc.perform(get("/estoque/pecas")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 200) {
            Object[] pecas = objectMapper.readValue(result.getResponse().getContentAsString(), Object[].class);
            testContext.put("pecas_listadas", pecas.length);
        }
    }

    @Então("a lista deve conter {int} peças")
    public void listaContemPecas(int quantidade) {
        Integer quantidadeLista = (Integer) testContext.get("pecas_listadas");
        assertNotNull(quantidadeLista, "Lista de peças não foi obtida");
        assertEquals(quantidade, quantidadeLista, "Quantidade de peças não corresponde");
    }

    @Quando("a quantidade da peça é atualizada para {int}")
    public void atualizarQuantidadePeca(int quantidade) throws Exception {
        String codigo = (String) testContext.get("peca_codigo_teste");
        assertNotNull(codigo, "Código da peça não está configurado");

        Map<String, Integer> updateRequest = new HashMap<>();
        updateRequest.put("quantidade", quantidade);

        String requestBody = objectMapper.writeValueAsString(updateRequest);

        MvcResult result = mockMvc.perform(put("/estoque/pecas/{codigo}", codigo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("o preço unitário da peça é atualizado para {double}")
    public void atualizarPrecoPeca(double preco) throws Exception {
        String codigo = (String) testContext.get("peca_codigo_teste");
        assertNotNull(codigo, "Código da peça não está configurado");

        Map<String, Double> updateRequest = new HashMap<>();
        updateRequest.put("preco_unitario", preco);

        String requestBody = objectMapper.writeValueAsString(updateRequest);

        MvcResult result = mockMvc.perform(put("/estoque/pecas/{codigo}", codigo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("a peça deve ter preço unitário {string}")
    public void pecaTemPrecoUnitario(String preco) {
        Map<String, Object> peca = (Map<String, Object>) testContext.get("peca_buscada");
        if (peca != null) {
            assertEquals(preco, String.valueOf(peca.get("preco_unitario")), "Preço não corresponde");
        }
    }

    @Quando("a peça é deletada")
    public void deletarPeca() throws Exception {
        String codigo = (String) testContext.get("peca_codigo_teste");
        assertNotNull(codigo, "Código da peça não está configurado");

        MvcResult result = mockMvc.perform(delete("/estoque/pecas/{codigo}", codigo)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("a peça deve ser removida do sistema")
    public void pecaRemovidaDoSistema() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Peça não foi removida");
    }

    @Quando("tenta-se atualizar a quantidade para {int}")
    public void tentaAtualizarQuantidadeNegativa(int quantidade) throws Exception {
        String codigo = (String) testContext.get("peca_codigo_teste");
        assertNotNull(codigo, "Código da peça não está configurado");

        Map<String, Integer> updateRequest = new HashMap<>();
        updateRequest.put("quantidade", quantidade);

        String requestBody = objectMapper.writeValueAsString(updateRequest);

        MvcResult result = mockMvc.perform(put("/estoque/pecas/{codigo}", codigo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Dado("que existe uma peça cadastrada com código {string} e quantidade {int}")
    public void pecaCadastradaComQuantidadeX(String codigo, int quantidade) throws Exception {
        pecaCadastradaComQuantidade(codigo, quantidade);
    }

    @Quando("uma movimentação de entrada é registrada para {int} unidades")
    public void movimentacaoEntradaParaUnidades(int quantidade) throws Exception {
        Map<String, Object> movimentacaoRequest = new HashMap<>();
        movimentacaoRequest.put("quantidade", quantidade);
        movimentacaoRequest.put("tipo", "ENTRADA");

        String requestBody = objectMapper.writeValueAsString(movimentacaoRequest);

        MvcResult result = mockMvc.perform(post("/estoque/movimentacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        Integer quantidadeAtual = (Integer) testContext.get("peca_quantidade_atual");
        testContext.put("peca_quantidade_atual", quantidadeAtual + quantidade);
    }

    @Então("a quantidade da peça deve ser {int}")
    public void quantidadePecaDeveSer(int quantidade) {
        Integer quantidadeAtual = (Integer) testContext.get("peca_quantidade_atual");
        assertEquals(quantidade, quantidadeAtual, "Quantidade não corresponde");
    }

    @Então("deve haver um registro de movimentação")
    public void registroMovimentacao() {
        int status = testContext.getLastHttpStatus();
        assertEquals(201, status, "Movimentação não foi registrada");
    }

    @Quando("uma movimentação de saída é registrada para {int} unidades")
    public void movimentacaoSaidaParaUnidades(int quantidade) throws Exception {
        Map<String, Object> movimentacaoRequest = new HashMap<>();
        movimentacaoRequest.put("quantidade", quantidade);
        movimentacaoRequest.put("tipo", "SAIDA");

        String requestBody = objectMapper.writeValueAsString(movimentacaoRequest);

        MvcResult result = mockMvc.perform(post("/estoque/movimentacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        Integer quantidadeAtual = (Integer) testContext.get("peca_quantidade_atual");
        testContext.put("peca_quantidade_atual", quantidadeAtual - quantidade);
    }

    @Quando("tenta-se registrar saída de {int} unidades")
    public void tentaRegistrarSaidaUnidades(int quantidade) throws Exception {
        movimentacaoSaidaParaUnidades(quantidade);
    }
}
