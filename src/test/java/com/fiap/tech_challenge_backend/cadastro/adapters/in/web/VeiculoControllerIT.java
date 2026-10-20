package com.fiap.tech_challenge_backend.cadastro.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.acesso.adapters.out.persistence.UsuarioJpaRepository;
import com.fiap.tech_challenge_backend.acesso.adapters.out.persistence.UsuarioMapper;
import com.fiap.tech_challenge_backend.cadastro.application.dto.AtualizarVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteMapper;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteVeiculoJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.VeiculoJpaRepository;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Cep;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
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
import org.springframework.jdbc.core.JdbcTemplate;
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
@DisplayName("VeiculoController - Integração")
class VeiculoControllerIT {

    private static final String SENHA_HASH = "$2y$10$mFqh8hTdGLkdvZaJp7Fb1uUm7RrWSFgoWVgPMjJPzd8gP.3snTNw2";

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
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Autowired
    private ClienteVeiculoJpaRepository clienteVeiculoJpaRepository;

    @Autowired
    private VeiculoJpaRepository veiculoJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limparBase() {
        clienteVeiculoJpaRepository.deleteAll();
        // Usa SQL nativo para contornar @Where(deleted_at IS NULL) do soft-delete
        jdbcTemplate.update("DELETE FROM veiculos");
        clienteJpaRepository.deleteAll();
        usuarioJpaRepository.deleteAll();

        Usuario usuario = Usuario.builder()
                .nome("João Silva")
                .email(new Email("joao@email.com"))
                .senha(SENHA_HASH)
                .perfil(PerfilUsuario.CLIENTE)
                .cpfCnpj(new CpfCnpj("12345678901"))
                .build();
        Usuario usuarioSalvo = UsuarioMapper.toDomain(usuarioJpaRepository.save(UsuarioMapper.toEntity(usuario)));

        Cliente cliente = Cliente.builder()
                .nome("João Silva")
                .usuarioId(usuarioSalvo.getId())
                .cpfCnpj(new CpfCnpj("12345678901"))
                .telefone(new Telefone("11987654321"))
                .cep(new Cep("01310100"))
                .rua("Av. Paulista")
                .numero("1000")
                .complemento(null)
                .cidade("São Paulo")
                .estado("SP")
                .build();
        clienteJpaRepository.save(ClienteMapper.toEntity(cliente));
    }

    private CadastroVeiculoRequest requestFord() {
        return new CadastroVeiculoRequest(
                "ABC1234", "Ford", "Fiesta", 2020, "Branco", "12345678901"
        );
    }

    @Test
    @WithMockUser
    @DisplayName("POST /veiculos - deve cadastrar veículo e retornar 201")
    void deveCadastrarVeiculo() throws Exception {
        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFord())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Ford"))
                .andExpect(jsonPath("$.modelo").value("Fiesta"))
                .andExpect(jsonPath("$.ano").value(2020))
                .andExpect(jsonPath("$.cor").value("Branco"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /veiculos - deve retornar 409 quando placa já cadastrada")
    void deveRetornar409QuandoPlacaJaCadastrada() throws Exception {
        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFord())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFord())))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /veiculos - deve listar veículos cadastrados")
    void deveListarVeiculos() throws Exception {
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestFord())));

        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].placa").value("ABC1234"))
                .andExpect(jsonPath("$[0].marca").value("Ford"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /veiculos/{placa} - deve retornar veículo pela placa")
    void deveBuscarVeiculoPorPlaca() throws Exception {
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestFord())));

        mockMvc.perform(get("/veiculos/ABC1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.marca").value("Ford"))
                .andExpect(jsonPath("$.modelo").value("Fiesta"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /veiculos/{placa} - deve retornar 404 quando veículo não existe")
    void deveRetornar404QuandoVeiculoNaoExiste() throws Exception {
        mockMvc.perform(get("/veiculos/XXX9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /veiculos/{placa} - deve atualizar dados do veículo")
    void deveAtualizarVeiculo() throws Exception {
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestFord())));

        AtualizarVeiculoRequest atualizacao = new AtualizarVeiculoRequest("Fiesta Atualizado");

        mockMvc.perform(put("/veiculos/ABC1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Fiesta Atualizado"));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /veiculos/{placa} - deve deletar veículo e retornar 200")
    void deveDeletarVeiculo() throws Exception {
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestFord())));

        mockMvc.perform(delete("/veiculos/ABC1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value(
                        "Veículo removido com sucesso. Todas as ordens de serviço associadas ao veículo foram preservadas no histórico."));

        mockMvc.perform(get("/veiculos"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /veiculos - deve retornar 401 sem autenticação")
    void deveExigirAutenticacao() throws Exception {
        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /veiculos - deve retornar 400 com dados inválidos")
    void deveRetornar400ComDadosInvalidos() throws Exception {
        CadastroVeiculoRequest invalido = new CadastroVeiculoRequest(
                "", "", "", 1800, "", ""
        );

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /veiculos - deve listar múltiplos veículos")
    void deveListarMultiplosVeiculos() throws Exception {
        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestFord())));

        CadastroVeiculoRequest chevrolet = new CadastroVeiculoRequest(
                "XYZ5678", "Chevrolet", "Onix", 2021, "Preto", "12345678901"
        );

        mockMvc.perform(post("/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chevrolet)));

        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
