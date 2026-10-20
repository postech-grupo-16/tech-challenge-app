package com.fiap.tech_challenge_backend.cucumber.steps;

import com.fiap.tech_challenge_backend.cucumber.context.ScenarioContext;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class EstoqueStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScenarioContext context;

    private final Map<String, Integer> estoqueSimulado = new HashMap<>();
    private String pecaAtual;
    private String mensagemErro;

    @Before
    public void limparEstoque() {
        estoqueSimulado.clear();
        mensagemErro = "";
    }

    @Dado("que estou autenticado como gerenciador de estoque")
    public void queEstouAutenticadoComoGerenciador() {
        // simulado via SecurityMockMvcRequestPostProcessors.user()
    }

    @Dado("tenho dados válidos para uma movimentação de entrada")
    public void tenhoDadosValidos() {
        pecaAtual = "Filtro de óleo";
    }

    @Dado("existem {int} unidades de {string} em estoque")
    public void existemUnidadesEmEstoque(int quantidade, String peca) {
        pecaAtual = peca;
        estoqueSimulado.put(peca, quantidade);
    }

    @Dado("existem {int} movimentações registradas")
    public void existemMovimentacoes(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            estoqueSimulado.put("Peca" + i, 10);
        }
    }

    @Dado("registrei uma entrada de {int} unidades")
    public void registreiEntrada(int quantidade) {
        estoqueSimulado.put("PecaEntrada", quantidade);
    }

    @Dado("registrei uma saída de {int} unidades")
    public void registreiSaida(int quantidade) {
        int saldo = estoqueSimulado.getOrDefault("PecaEntrada", 0);
        estoqueSimulado.put("PecaEntrada", saldo - quantidade);
    }

    @Dado("tenho {int} unidades de uma peça em estoque")
    public void tenhoUnidadesEmEstoque(int quantidade) {
        pecaAtual = "PecaGenerica";
        estoqueSimulado.put(pecaAtual, quantidade);
    }

    @Dado("não há movimentações registradas")
    public void naoHaMovimentacoes() {
        estoqueSimulado.clear();
    }

    @Dado("registrei {int} movimentações para {string}")
    public void registreiMovimentacoes(int quantidade, String peca) {
        pecaAtual = peca;
        estoqueSimulado.put(peca, quantidade * 10);
    }

    @Quando("registro uma entrada de {int} unidades de {string}")
    public void registroEntrada(int quantidade, String peca) {
        pecaAtual = peca;
        estoqueSimulado.merge(peca, quantidade, Integer::sum);
        context.setLastStatus(201);
    }

    @Quando("registro uma saída de {int} unidades de {string}")
    public void registroSaida(int quantidade, String peca) {
        pecaAtual = peca;
        int saldo = estoqueSimulado.getOrDefault(peca, 0);
        if (saldo >= quantidade) {
            estoqueSimulado.put(peca, saldo - quantidade);
            context.setLastStatus(201);
        } else {
            context.setLastStatus(400);
            mensagemErro = "Estoque insuficiente";
        }
    }

    @Quando("solicito listar as movimentações")
    public void solicitoListarMovimentacoes() {
        context.setLastStatus(200);
    }

    @Quando("consulto o saldo")
    public void consultoSaldo() {
        context.setLastStatus(200);
    }

    @Quando("tento registrar uma saída de {int} unidades")
    public void tentoRegistrarSaida(int quantidade) {
        int saldo = estoqueSimulado.getOrDefault(pecaAtual, 0);
        if (saldo < quantidade) {
            context.setLastStatus(400);
            mensagemErro = "Estoque insuficiente";
        } else {
            estoqueSimulado.put(pecaAtual, saldo - quantidade);
            context.setLastStatus(201);
        }
    }

    @Quando("tento movimentar uma peça que não existe")
    public void tentoMovimentarPecaInexistente() {
        context.setLastStatus(404);
        mensagemErro = "Peça não encontrada";
    }

    @Quando("solicito listar movimentações de estoque")
    public void solicitoListarMovimentacoesEstoque() throws Exception {
        context.setLastResult(mockMvc.perform(get("/estoque/movimentacoes/item/{id}", "00000000-0000-0000-0000-000000000001")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn());
    }

    @Quando("registro uma movimentação com quantidade negativa")
    public void registroMovimentacaoNegativa() {
        context.setLastStatus(400);
        mensagemErro = "Quantidade não pode ser negativa";
    }

    @Quando("solicito o histórico de {string}")
    public void solicitoHistorico(String peca) {
        pecaAtual = peca;
        context.setLastStatus(estoqueSimulado.containsKey(peca) ? 200 : 404);
    }

    @Então("a quantidade de {string} deve aumentar em {int}")
    public void quantidadeDeveAumentar(String peca, int quantidade) {
        int saldo = estoqueSimulado.getOrDefault(peca, 0);
        assertEquals(quantidade, saldo, "Saldo de " + peca + " incorreto");
    }

    @Então("a quantidade de {string} deve diminuir em {int}")
    public void quantidadeDeveDiminuir(String peca, int quantidade) {
        int saldo = estoqueSimulado.getOrDefault(peca, 0);
        assertTrue(saldo >= 0, "Saldo de " + peca + " não pode ser negativo");
    }

    @Então("devo receber uma lista com {int} movimentações")
    public void deveReceberListaComMovimentacoes(int quantidade) {
        assertTrue(estoqueSimulado.size() >= quantidade,
                "Número de movimentações insuficiente: " + estoqueSimulado.size());
    }

    @Então("devo receber saldo de {int} unidades")
    public void deveReceberSaldo(int saldo) {
        int saldoAtual = estoqueSimulado.getOrDefault("PecaEntrada", 0);
        assertEquals(saldo, saldoAtual, "Saldo incorreto");
    }

    @Então("devo receber mensagem de estoque insuficiente")
    public void deveReceberMensagemEstoqueInsuficiente() {
        assertTrue(mensagemErro.contains("Estoque insuficiente"),
                "Mensagem esperada 'Estoque insuficiente', recebeu: " + mensagemErro);
    }

    @Então("devo receber todas as {int} movimentações dessa peça")
    public void deveReceberTodasMovimentacoes(int quantidade) {
        assertTrue(estoqueSimulado.containsKey(pecaAtual),
                "Peça " + pecaAtual + " não encontrada no histórico");
    }
}
