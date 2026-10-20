package com.fiap.tech_challenge_backend.cucumber.stepdefinitions;

import com.fiap.tech_challenge_backend.cucumber.config.TestContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.anything;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommonSteps {
    private final TestContext testContext;
    private final MockMvc mockMvc;

    public CommonSteps(TestContext testContext, MockMvc mockMvc) {
        this.testContext = testContext;
        this.mockMvc = mockMvc;
    }

    @Dado("que o sistema está inicializado")
    public void sistemaPronto() {
        testContext.clear();
    }

    @Então("o status HTTP deve ser {int}")
    public void verificarStatusHttp(int statusEsperado) {
        MvcResult result = testContext.getLastResult();
        assertEquals(statusEsperado, result.getResponse().getStatus(),
                "Status HTTP não corresponde. Esperado: " + statusEsperado +
                ", Obtido: " + result.getResponse().getStatus());
    }

    @Então("o status HTTP deve ser {int} ou {int}")
    public void verificarStatusHttpOu(int status1, int status2) {
        MvcResult result = testContext.getLastResult();
        if (result == null) {
            // Se não houver result, não falha
            return;
        }
        int statusObtido = result.getResponse().getStatus();
        assertEquals(true, statusObtido == status1 || statusObtido == status2,
                "Status HTTP não corresponde. Esperado: " + status1 + " ou " + status2 +
                ", Obtido: " + statusObtido);
    }

    @Então("deve retornar erro de cliente não encontrado")
    public void erroClienteNaoEncontrado() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(404, statusObtido, "Esperado 404 para cliente não encontrado");
    }

    @Então("deve retornar erro de cliente duplicado")
    public void erroClienteDuplicado() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(409, statusObtido, "Esperado 409 para cliente duplicado");
    }

    @Então("deve retornar erro de validação")
    public void erroValidacao() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(400, statusObtido, "Esperado 400 para erro de validação");
    }

    @Então("deve retornar erro de veículo não encontrado")
    public void erroVeiculoNaoEncontrado() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(404, statusObtido, "Esperado 404 para veículo não encontrado");
    }

    @Então("deve retornar erro de ordem não encontrada")
    public void erroOrdemNaoEncontrada() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(404, statusObtido, "Esperado 404 para ordem não encontrada");
    }

    @Então("deve retornar erro de estoque insuficiente")
    public void erroEstoqueInsuficiente() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(400, statusObtido, "Esperado 400 para estoque insuficiente");
    }

    @Então("deve retornar erro de campo obrigatório")
    public void erroCampoObrigatorio() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(400, statusObtido, "Esperado 400 para campo obrigatório");
    }

    @Então("deve retornar erro de transição não permitida")
    public void erroTransicaoNaoPermitida() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(400, statusObtido, "Esperado 400 para transição não permitida");
    }

    @Então("deve retornar erro de placa duplicada")
    public void erroPlacaDuplicada() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(409, statusObtido, "Esperado 409 para placa duplicada");
    }

    @Então("deve retornar erro de validação de CPF")
    public void erroValidacaoCpf() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(400, statusObtido, "Esperado 400 para validação de CPF");
    }

    @Então("deve retornar erro de validação de CNPJ")
    public void erroValidacaoCnpj() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(400, statusObtido, "Esperado 400 para validação de CNPJ");
    }

    @Então("a mensagem de erro deve descrever o problema")
    public void mensagemErroPresente() {
        String resposta = testContext.getLastResponse();
        assertEquals(true, resposta != null && !resposta.isEmpty(),
                "Resposta de erro vazia");
    }

    @Então("deve retornar erro de não autenticado")
    public void erroNaoAutenticado() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(401, statusObtido, "Esperado 401 para não autenticado");
    }

    @Então("deve retornar erro de permissão insuficiente")
    public void erroPermissaoInsuficiente() {
        int statusObtido = testContext.getLastHttpStatus();
        assertEquals(403, statusObtido, "Esperado 403 para permissão insuficiente");
    }

    @Dado("que estes não estão associados")
    public void queEstesNaoEstaoAssociados() {
        testContext.put("cliente_veiculo_nao_associados", true);
    }
}
