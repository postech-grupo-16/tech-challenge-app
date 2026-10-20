package com.fiap.tech_challenge_backend.cadastro.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteJpaRepository;
import com.fiap.tech_challenge_backend.acesso.adapters.out.persistence.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test-integration-auth")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("ClienteController - Integração")
class ClienteControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void overrideDataSourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @BeforeEach
    void limparBase() {
        clienteJpaRepository.deleteAll();
        usuarioJpaRepository.deleteAll();
    }

    private CadastroClienteRequest requestJoao() {
        return new CadastroClienteRequest(
                "João Silva", "joao@email.com", "senha1234",
                "12345678901", "11987654321", "01310100",
                "Av. Paulista", "1000", null, "São Paulo", "SP"
        );
    }

    @Test
    @WithMockUser
    @DisplayName("POST /clientes - deve cadastrar e retornar 201")
    void deveCadastrarCliente() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestJoao())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpfCnpj").value("12345678901"))
                .andExpect(jsonPath("$.cidade").value("São Paulo"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /clientes - deve retornar 409 quando CPF/CNPJ já cadastrado")
    void deveRetornar409QuandoCpfJaCadastrado() throws Exception {
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestJoao())));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestJoao())))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /clientes - deve retornar 400 quando campos obrigatórios estão ausentes")
    void deveRetornarBadRequestQuandoNomeVazio() throws Exception {
        CadastroClienteRequest invalido = new CadastroClienteRequest(
                "", "joao@email.com", "senha1234",
                "12345678901", "11987654321", "01310100",
                "Av. Paulista", "1000", null, "São Paulo", "SP"
        );

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes - deve listar clientes cadastrados")
    void deveListarClientes() throws Exception {
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestJoao())));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{cpfCnpj} - deve retornar cliente pelo CPF/CNPJ")
    void deveBuscarClientePorCpf() throws Exception {
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestJoao())));

        mockMvc.perform(get("/clientes/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpfCnpj").value("12345678901"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{cpfCnpj} - deve retornar 404 quando cliente não existe")
    void deveRetornar404QuandoClienteNaoExiste() throws Exception {
        mockMvc.perform(get("/clientes/99999999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /clientes/{cpfCnpj} - deve atualizar dados do cliente")
    void deveAtualizarCliente() throws Exception {
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestJoao())));

        var atualizacao = """
                {
                  "nome": "João Atualizado",
                  "telefone": "11999990000",
                  "cep": "04538133",
                  "rua": "Rua Nova",
                  "numero": "50",
                  "cidade": "São Paulo",
                  "estado": "SP"
                }
                """;

        mockMvc.perform(put("/clientes/12345678901")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atualizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Atualizado"));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /clientes/{cpfCnpj} - deve deletar cliente e retornar 200")
    void deveDeletarCliente() throws Exception {
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestJoao())));

        mockMvc.perform(delete("/clientes/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem").value("Cliente deletado com sucesso"))
                .andExpect(jsonPath("$.cpfCnpj").value("12345678901"));

        mockMvc.perform(get("/clientes"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /clientes - deve retornar 401 sem autenticação")
    void deveExigirAutenticacao() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isUnauthorized());
    }
}
