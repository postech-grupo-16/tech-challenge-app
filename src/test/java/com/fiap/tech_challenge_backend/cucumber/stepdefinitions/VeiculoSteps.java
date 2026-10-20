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

public class VeiculoSteps {
    private final TestContext testContext;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public VeiculoSteps(TestContext testContext, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.testContext = testContext;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Dado("que existe um veículo associado ao cliente com placa {string}")
    public void criarVeiculoPadrao(String placa) throws Exception {
        String cpfCliente = (String) testContext.get("cliente_teste_cpf");
        if (cpfCliente == null) {
            cpfCliente = "12345678901";
        }

        Map<String, String> veiculoRequest = new HashMap<>();
        veiculoRequest.put("placa", placa);
        veiculoRequest.put("marca", "Toyota");
        veiculoRequest.put("modelo", "Corolla");
        veiculoRequest.put("ano", "2020");

        String requestBody = objectMapper.writeValueAsString(veiculoRequest);

        MvcResult result = mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("X-Cliente-CPF", cpfCliente))
                .andReturn();

        if (result.getResponse().getStatus() == 201) {
            testContext.put("veiculo_placa", placa);
        }
    }

    @Quando("um novo veículo é cadastrado com os seguintes dados:")
    public void cadastrarVeiculoComDados(io.cucumber.datatable.DataTable dataTable) throws Exception {
        Map<String, String> dados = dataTable.asMap();

        String requestBody = objectMapper.writeValueAsString(dados);

        MvcResult result = mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 201) {
            Map<String, Object> veiculoRetorno = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class);
            testContext.put("veiculo_criado", veiculoRetorno);
            testContext.put("veiculo_placa", dados.get("placa"));
        }
    }

    @Então("o veículo deve ser associado ao cliente com sucesso")
    public void veiculoAssociadoComSucesso() {
        assertNotNull(testContext.get("veiculo_criado"), "Veículo não foi criado");
        Map<String, Object> veiculo = (Map<String, Object>) testContext.get("veiculo_criado");
        assertNotNull(veiculo.get("placa"), "Veículo não contém placa");
    }

    @Dado("que um veículo com placa {string} foi cadastrado")
    public void veiculoCadastradoComPlaca(String placa) throws Exception {
        criarVeiculoPadrao(placa);
        testContext.put("veiculo_placa_teste", placa);
    }

    @Quando("o veículo é buscado por placa {string}")
    public void buscarVeiculoPorPlaca(String placa) throws Exception {
        MvcResult result = mockMvc.perform(get("/veiculos/{placa}", placa)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 200) {
            testContext.put("veiculo_buscado", objectMapper.readValue(
                    result.getResponse().getContentAsString(), Map.class));
        }
    }

    @Então("os dados do veículo devem ser retornados corretamente")
    public void dadosVeiculoRetornados() {
        assertNotNull(testContext.get("veiculo_buscado"), "Veículo não foi encontrado");
    }

    @Então("o cliente associado deve estar presente")
    public void clienteAssociadoPresente() {
        Map<String, Object> veiculo = (Map<String, Object>) testContext.get("veiculo_buscado");
        assertNotNull(veiculo, "Veículo não encontrado");
    }

    @Dado("que um cliente tem {int} veículos cadastrados")
    public void clienteComVeiculos(int quantidade) throws Exception {
        String cpfCliente = "12345678901";
        testContext.put("cliente_teste_cpf", cpfCliente);

        for (int i = 1; i <= quantidade; i++) {
            Map<String, String> veiculoRequest = new HashMap<>();
            veiculoRequest.put("placa", String.format("ABC%d", i));
            veiculoRequest.put("marca", "Toyota");
            veiculoRequest.put("modelo", "Corolla");
            veiculoRequest.put("ano", "2020");

            String requestBody = objectMapper.writeValueAsString(veiculoRequest);

            mockMvc.perform(post("/veiculos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .header("X-Cliente-CPF", cpfCliente))
                    .andReturn();
        }

        testContext.put("veiculos_quantidade_esperada", quantidade);
    }

    @Quando("os veículos do cliente são listados")
    public void listarVeiculosCliente() throws Exception {
        MvcResult result = mockMvc.perform(get("/veiculos")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 200) {
            Object[] veiculos = objectMapper.readValue(
                    result.getResponse().getContentAsString(), Object[].class);
            testContext.put("veiculos_listados", veiculos.length);
        }
    }

    @Então("a lista deve conter {int} veículos")
    public void listaContemVeiculos(int quantidade) {
        Integer quantidadeLista = (Integer) testContext.get("veiculos_listados");
        assertNotNull(quantidadeLista, "Lista de veículos não foi obtida");
        assertEquals(quantidade, quantidadeLista, "Quantidade de veículos não corresponde");
    }

    @Quando("os dados do veículo são atualizados:")
    public void atualizarDadosVeiculo(io.cucumber.datatable.DataTable dataTable) throws Exception {
        String placa = (String) testContext.get("veiculo_placa_teste");
        assertNotNull(placa, "Placa do veículo não está configurada");

        Map<String, String> dados = dataTable.asMap();

        String requestBody = objectMapper.writeValueAsString(dados);

        MvcResult result = mockMvc.perform(put("/veiculos/{placa}", placa)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("as informações devem ser atualizadas com sucesso")
    public void informacoesAtualizadas() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Veículo não foi atualizado com sucesso");
    }

    @Quando("o veículo é deletado")
    public void deletarVeiculo() throws Exception {
        String placa = (String) testContext.get("veiculo_placa_teste");
        assertNotNull(placa, "Placa do veículo não está configurada");

        MvcResult result = mockMvc.perform(delete("/veiculos/{placa}", placa)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("o veículo deve ser removido com sucesso")
    public void veiculoRemovido() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Veículo não foi removido com sucesso");
    }

    @Quando("um veículo é cadastrado com a mesma placa {string}")
    public void cadastrarVeiculoDuplicado(String placa) throws Exception {
        Map<String, String> veiculoRequest = new HashMap<>();
        veiculoRequest.put("placa", placa);
        veiculoRequest.put("marca", "Ford");
        veiculoRequest.put("modelo", "Fiesta");
        veiculoRequest.put("ano", "2021");

        String requestBody = objectMapper.writeValueAsString(veiculoRequest);

        MvcResult result = mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um veículo é cadastrado para um cliente inexistente")
    public void cadastrarVeiculoClienteInexistente() throws Exception {
        Map<String, String> veiculoRequest = new HashMap<>();
        veiculoRequest.put("placa", "XYZ9999");
        veiculoRequest.put("marca", "Toyota");
        veiculoRequest.put("modelo", "Corolla");
        veiculoRequest.put("ano", "2020");
        veiculoRequest.put("cliente_cpf", "99999999999");

        String requestBody = objectMapper.writeValueAsString(veiculoRequest);

        MvcResult result = mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um veículo é cadastrado com placa inválida {string}")
    public void cadastrarVeiculoPlacaInvalida(String placa) throws Exception {
        Map<String, String> veiculoRequest = new HashMap<>();
        veiculoRequest.put("placa", placa);
        veiculoRequest.put("marca", "Toyota");
        veiculoRequest.put("modelo", "Corolla");
        veiculoRequest.put("ano", "2020");

        String requestBody = objectMapper.writeValueAsString(veiculoRequest);

        MvcResult result = mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um veículo é cadastrado com ano futuro")
    public void cadastrarVeiculoAnoFuturo() throws Exception {
        Map<String, String> veiculoRequest = new HashMap<>();
        veiculoRequest.put("placa", "FUT2025");
        veiculoRequest.put("marca", "Toyota");
        veiculoRequest.put("modelo", "Corolla");
        veiculoRequest.put("ano", "2030");

        String requestBody = objectMapper.writeValueAsString(veiculoRequest);

        MvcResult result = mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um veículo é cadastrado sem placa")
    public void cadastrarVeiculoSemPlaca() throws Exception {
        Map<String, String> veiculoRequest = new HashMap<>();
        veiculoRequest.put("marca", "Toyota");
        veiculoRequest.put("modelo", "Corolla");
        veiculoRequest.put("ano", "2020");

        String requestBody = objectMapper.writeValueAsString(veiculoRequest);

        MvcResult result = mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("um veículo é cadastrado sem marca ou modelo")
    public void cadastrarVeiculoSemMarcaModelo() throws Exception {
        Map<String, String> veiculoRequest = new HashMap<>();
        veiculoRequest.put("placa", "ABC1234");
        veiculoRequest.put("ano", "2020");

        String requestBody = objectMapper.writeValueAsString(veiculoRequest);

        MvcResult result = mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());
    }


    @Então("o veículo deve conter a placa {string}")
    public void veiculoContemPlaca(String placa) {
        Map<String, Object> veiculo = (Map<String, Object>) testContext.get("veiculo_criado");
        assertNotNull(veiculo, "Veículo não foi criado");
        assertEquals(placa, veiculo.get("placa"), "Placa não corresponde");
    }

    @Então("o veículo deve conter a marca {string}")
    public void veiculoContemMarca(String marca) {
        Map<String, Object> veiculo = (Map<String, Object>) testContext.get("veiculo_criado");
        assertNotNull(veiculo, "Veículo não foi criado");
        assertEquals(marca, veiculo.get("marca"), "Marca não corresponde");
    }

    @Então("o veículo deve ser encontrado")
    public void veiculoEncontrado() {
        assertNotNull(testContext.get("veiculo_buscado"), "Veículo não foi encontrado");
    }

    @Então("o veículo deve ser atualizado com sucesso")
    public void veiculoAtualizado() {
        int status = testContext.getLastHttpStatus();
        assertEquals(200, status, "Veículo não foi atualizado com sucesso");
    }

    @Então("ao buscar o veículo deletado por placa {string} deve retornar 404")
    public void buscarVeiculoDeletadoRetorna404(String placa) throws Exception {
        MvcResult result = mockMvc.perform(get("/veiculos/{placa}", placa)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        assertEquals(404, result.getResponse().getStatus(), "Veículo não foi deletado corretamente");
    }

    @Dado("que existem {int} veículos cadastrados no sistema")
    public void veiculosCadastradosNoSistema(int quantidade) throws Exception {
        for (int i = 1; i <= quantidade; i++) {
            Map<String, String> veiculoRequest = new HashMap<>();
            veiculoRequest.put("placa", String.format("VEI%04d", i));
            veiculoRequest.put("marca", "Toyota");
            veiculoRequest.put("modelo", "Corolla");
            veiculoRequest.put("ano", "2020");

            String requestBody = objectMapper.writeValueAsString(veiculoRequest);

            mockMvc.perform(post("/veiculos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andReturn();
        }
        testContext.put("veiculos_quantidade_esperada", quantidade);
    }

    @Quando("a lista de veículos é solicitada")
    public void listarVeiculos() throws Exception {
        MvcResult result = mockMvc.perform(get("/veiculos")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        testContext.setLastResult(result);
        testContext.setLastHttpStatus(result.getResponse().getStatus());
        testContext.setLastResponse(result.getResponse().getContentAsString());

        if (result.getResponse().getStatus() == 200) {
            Object[] veiculos = objectMapper.readValue(result.getResponse().getContentAsString(), Object[].class);
            testContext.put("veiculos_listados", veiculos.length);
        }
    }

    @Quando("um veículo foi cadastrado com placa {string}")
    public void veiculoFoiCadastradoComPlaca(String placa) throws Exception {
        veiculoCadastradoComPlaca(placa);
    }

    @Dado("que um veículo foi cadastrado com placa {string}")
    public void umVeiculoFoiCadastradoComPlaca(String placa) throws Exception {
        veiculoCadastradoComPlaca(placa);
    }

    @Dado("que existe um veículo cadastrado com placa {string}")
    public void existeUmVeiculoCadastradoComPlaca(String placa) throws Exception {
        veiculoCadastradoComPlaca(placa);
    }
}
