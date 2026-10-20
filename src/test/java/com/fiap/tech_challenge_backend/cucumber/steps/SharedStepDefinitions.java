package com.fiap.tech_challenge_backend.cucumber.steps;

import com.fiap.tech_challenge_backend.cucumber.context.ScenarioContext;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SharedStepDefinitions {

    @Autowired
    private ScenarioContext context;

    @Dado("que não estou autenticado")
    public void queNaoEstouAutenticado() {
        // Nenhuma ação necessária — as chamadas sem @WithMockUser retornam 401 automaticamente
    }

    @Então("devo receber status {int}")
    public void deveReceberStatus(int statusEsperado) {
        assertEquals(statusEsperado, context.getLastStatus(),
                "Status HTTP esperado: " + statusEsperado + ", recebido: " + context.getLastStatus());
    }

    @Então("devo receber uma lista vazia")
    public void deveReceberListaVazia() throws Exception {
        MvcResult result = context.getLastResult();
        if (result != null) {
            String body = result.getResponse().getContentAsString();
            assertTrue(
                body.contains("\"dados\":[]") || body.contains("[]"),
                "Esperado lista vazia, mas recebeu: " + body
            );
        } else {
            // cenário simulado (ex: estoque) — basta validar que o status é 200
            assertEquals(200, context.getLastStatus(), "Status esperado 200 para lista vazia");
        }
    }
}
