package com.fiap.tech_challenge_backend.atendimento.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.acesso.adapters.out.persistence.UsuarioJpaRepository;
import com.fiap.tech_challenge_backend.acesso.adapters.out.persistence.UsuarioMapper;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoMapper;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.ServicoCatalogoMapper;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.ServicoCatalogoRepository;
import com.fiap.tech_challenge_backend.atendimento.application.dto.*;
import com.fiap.tech_challenge_backend.atendimento.application.services.IdGeneratorService;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteMapper;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.VeiculoJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.VeiculoMapper;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Cep;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("OrdemServicoController - Integracao")
class OrdemServicoControllerIT {

    private static final String SENHA_HASH = "$2y$10$ZBA7G5klVUBsfUJwwjd8R.Bgr9NQrZ2Eb8pixmU9ccFwPy47U9qKm";
    private static final String BASE = "/api/v1/ordens-servico";

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

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private UsuarioJpaRepository usuarioJpaRepository;
    @Autowired private ClienteJpaRepository clienteJpaRepository;
    @Autowired private VeiculoJpaRepository veiculoJpaRepository;
    @Autowired private OrdemServicoRepository ordemServicoRepository;
    @Autowired private ServicoCatalogoRepository servicoCatalogoRepository;
    @Autowired private IdGeneratorService idGeneratorService;

    private UUID clienteId;
    private UUID veiculoId;
    private Long osId;
    private UUID servicoId;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM os_servicos");
        jdbcTemplate.execute("DELETE FROM os_pecas");
        jdbcTemplate.execute("DELETE FROM os_orcamentos");
        jdbcTemplate.execute("DELETE FROM os_historico_status");
        jdbcTemplate.execute("DELETE FROM ordens_servico");
        jdbcTemplate.execute("DELETE FROM clientes");
        jdbcTemplate.execute("DELETE FROM veiculos");
        jdbcTemplate.execute("DELETE FROM usuarios");
        jdbcTemplate.execute("DELETE FROM servico_catalogo");

        Usuario usuario = Usuario.builder()
                .nome("Joao Silva")
                .email(new Email("joao@email.com"))
                .senha(SENHA_HASH)
                .perfil(PerfilUsuario.CLIENTE)
                .cpfCnpj(new CpfCnpj("12345678901"))
                .build();
        Usuario usuarioSalvo = UsuarioMapper.toDomain(usuarioJpaRepository.save(UsuarioMapper.toEntity(usuario)));

        Cliente cliente = Cliente.builder()
                .nome("Joao Silva")
                .usuarioId(usuarioSalvo.getId())
                .cpfCnpj(new CpfCnpj("12345678901"))
                .telefone(new Telefone("11987654321"))
                .cep(new Cep("01310100"))
                .rua("Av. Paulista")
                .numero("1000")
                .complemento(null)
                .cidade("Sao Paulo")
                .estado("SP")
                .build();
        clienteId = clienteJpaRepository.save(ClienteMapper.toEntity(cliente)).getId();

        Veiculo veiculo = Veiculo.builder()
                .placa(new Placa("ABC1234"))
                .marca("Ford")
                .modelo("Fiesta")
                .ano(2020)
                .cor("Branco")
                .build();
        veiculoId = veiculoJpaRepository.save(VeiculoMapper.toEntity(veiculo)).getId();

        ServicoCatalogo servico = ServicoCatalogo.builder()
                .nome("Troca de Oleo")
                .descricao("Substituicao do oleo do motor")
                .precoMaoDeObra(new BigDecimal("120.00"))
                .build();
        servicoId = servicoCatalogoRepository.save(ServicoCatalogoMapper.toEntity(servico)).getId();

        Cliente clienteSalvo = ClienteMapper.toDomain(clienteJpaRepository.findById(clienteId).orElseThrow());
        OrdemServico os = OrdemServico.builder()
                .id(idGeneratorService.gerarIdOrdemServico())
                .clienteId(clienteSalvo.getId())
                .veiculoId(veiculoId)
                .mecanicoId(null)
                .status(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataCriacao(LocalDateTime.now())
                .valorTotal(BigDecimal.ZERO)
                .queixaCliente("Verificar troca de oleo")
                .urgente(false)
                .build();
        osId = ordemServicoRepository.save(OrdemServicoMapper.toEntity(os)).getId();
    }

    // ─────────────────────────────────────────────
    // GET /listar-os
    // ─────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /listar-os - deve listar ordens de servico")
    void deveListarOrdens() throws Exception {
        mockMvc.perform(get(BASE + "/listar-os"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(osId))
                .andExpect(jsonPath("$[0].status").value("EM_DIAGNOSTICO"));
    }

    @Test
    @DisplayName("GET /listar-os - deve retornar 401 sem autenticacao")
    void listarRetorna401SemAuth() throws Exception {
        mockMvc.perform(get(BASE + "/listar-os"))
                .andExpect(status().isUnauthorized());
    }

    // ─────────────────────────────────────────────
    // GET /buscar
    // ─────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /buscar?id= - deve retornar OS por ID")
    void deveBuscarPorId() throws Exception {
        mockMvc.perform(get(BASE + "/buscar").param("id", osId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(osId))
                .andExpect(jsonPath("$.status").value("EM_DIAGNOSTICO"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /buscar?id=9999 - deve retornar 404 para OS inexistente")
    void buscarRetorna404() throws Exception {
        mockMvc.perform(get(BASE + "/buscar").param("id", "9999"))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // POST /criar
    // ─────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("POST /criar - deve criar OS pelo cliente com CPF e placa")
    void deveCriarOrdem() throws Exception {
        CriarOrdemServicoClienteRequestDTO request = new CriarOrdemServicoClienteRequestDTO(
                "12345678901", "ABC1234", "Carro nao liga", null, false);

        mockMvc.perform(post(BASE + "/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("POST /criar - deve retornar 4xx quando placa nao encontrada")
    void criarRetornaErroVeiculoNaoEncontrado() throws Exception {
        CriarOrdemServicoClienteRequestDTO request = new CriarOrdemServicoClienteRequestDTO(
                "12345678901", "ZZZ9999", "Carro nao liga", null, false);

        mockMvc.perform(post(BASE + "/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    // ─────────────────────────────────────────────
    // PUT /editar
    // ─────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("PUT /editar?id= - deve atualizar OS")
    void deveAtualizarOrdem() throws Exception {
        OrdemServicoAtualizarRequestDTO request = new OrdemServicoAtualizarRequestDTO(
                clienteId, veiculoId, null, StatusOrdemServico.EM_EXECUCAO,
                LocalDateTime.now(), null, false);

        mockMvc.perform(put(BASE + "/editar").param("id", osId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_EXECUCAO"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("PUT /editar?id=9999 - deve retornar 404 para OS inexistente")
    void editarRetorna404() throws Exception {
        OrdemServicoAtualizarRequestDTO request = new OrdemServicoAtualizarRequestDTO(
                clienteId, veiculoId, null, StatusOrdemServico.EM_EXECUCAO,
                null, null, false);

        mockMvc.perform(put(BASE + "/editar").param("id", "9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // DELETE /deletar
    // ─────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /deletar?id= - deve remover OS com sucesso")
    void deveRemoverOrdem() throws Exception {
        mockMvc.perform(delete(BASE + "/deletar").param("id", osId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados[0].id").value(osId))
                .andExpect(jsonPath("$.dados[0].status").value("DELETADO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /deletar?id=9999 - deve retornar 404")
    void deletarRetorna404() throws Exception {
        mockMvc.perform(delete(BASE + "/deletar").param("id", "9999"))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // PATCH /alterar-status
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("PATCH /alterar-status?id= - deve alterar status com JWT")
    void deveAlterarStatus() throws Exception {
        AlterarStatusRequestDTO request = new AlterarStatusRequestDTO(StatusOrdemServico.EM_EXECUCAO, null);

        mockMvc.perform(patch(BASE + "/alterar-status").param("id", osId.toString())
                        .with(jwt().jwt(j -> j.subject("funcionario@email.com"))
                                .authorities(new SimpleGrantedAuthority("ROLE_FUNCIONARIO")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados[0].id").value(osId))
                .andExpect(jsonPath("$.dados[0].status").value("EM_EXECUCAO"));
    }

    @Test
    @DisplayName("PATCH /alterar-status?id=9999 - deve retornar 404 com JWT")
    void alterarStatusRetorna404() throws Exception {
        AlterarStatusRequestDTO request = new AlterarStatusRequestDTO(StatusOrdemServico.EM_EXECUCAO, null);

        mockMvc.perform(patch(BASE + "/alterar-status").param("id", "9999")
                        .with(jwt().jwt(j -> j.subject("funcionario@email.com"))
                                .authorities(new SimpleGrantedAuthority("ROLE_FUNCIONARIO")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // POST /criar-orcamento
    // ─────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("POST /criar-orcamento?id= - deve criar orcamento com sucesso")
    void deveCriarOrcamento() throws Exception {
        CriarOrcamentoRequestDTO request = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL, "5 dias",
                List.of(new ServicoOrcamentoRequestDTO(servicoId)),
                List.of());

        mockMvc.perform(post(BASE + "/criar-orcamento").param("id", osId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("INICIAL"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("POST /criar-orcamento?id=9999 - deve retornar 404 para OS inexistente")
    void criarOrcamentoRetorna404() throws Exception {
        CriarOrcamentoRequestDTO request = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL, "5 dias",
                List.of(new ServicoOrcamentoRequestDTO(servicoId)),
                List.of());

        mockMvc.perform(post(BASE + "/criar-orcamento").param("id", "9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────
    // GET /buscar-orcamento
    // ─────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /buscar-orcamento - deve retornar orcamento existente")
    void deveBuscarOrcamento() throws Exception {
        CriarOrcamentoRequestDTO criarReq = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL, "5 dias",
                List.of(new ServicoOrcamentoRequestDTO(servicoId)),
                List.of());

        String criarResp = mockMvc.perform(post(BASE + "/criar-orcamento").param("id", osId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orcamentoId = objectMapper.readTree(criarResp).get("id").asLong();

        mockMvc.perform(get(BASE + "/buscar-orcamento")
                        .param("idOS", osId.toString())
                        .param("idOrcamento", orcamentoId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orcamentoId))
                .andExpect(jsonPath("$.tipo").value("INICIAL"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    @DisplayName("GET /buscar-orcamento - deve retornar 404 para orcamento inexistente")
    void buscarOrcamentoRetorna404() throws Exception {
        mockMvc.perform(get(BASE + "/buscar-orcamento")
                        .param("idOS", osId.toString())
                        .param("idOrcamento", "9999"))
                .andExpect(status().isNotFound());
    }
}