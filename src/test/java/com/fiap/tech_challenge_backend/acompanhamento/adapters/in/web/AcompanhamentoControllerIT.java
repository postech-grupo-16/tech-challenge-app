package com.fiap.tech_challenge_backend.acompanhamento.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.acesso.adapters.out.persistence.UsuarioJpaRepository;
import com.fiap.tech_challenge_backend.acesso.adapters.out.persistence.UsuarioMapper;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoMapper;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.atendimento.application.services.IdGeneratorService;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteMapper;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("AcompanhamentoController - Integração")
class AcompanhamentoControllerIT {

    private static final String SENHA_HASH = "$2y$10$ZBA7G5klVUBsfUJwwjd8R.Bgr9NQrZ2Eb8pixmU9ccFwPy47U9qKm";

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
    private VeiculoJpaRepository veiculoJpaRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private IdGeneratorService idGeneratorService;

    private UUID clienteId;
    private Long osId;

    @BeforeEach
    void setUp() {
        ordemServicoRepository.deleteAll();
        clienteJpaRepository.deleteAll();
        veiculoJpaRepository.deleteAll();
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
        clienteId = clienteJpaRepository.save(ClienteMapper.toEntity(cliente)).getId();

        Veiculo veiculo = Veiculo.builder()
                .placa(new Placa("ABC1234"))
                .marca("Ford")
                .modelo("Fiesta")
                .ano(2020)
                .cor("Branco")
                .build();
        Veiculo veiculoSalvo = VeiculoMapper.toDomain(veiculoJpaRepository.save(VeiculoMapper.toEntity(veiculo)));

        Cliente clienteSalvo = ClienteMapper.toDomain(clienteJpaRepository.findById(clienteId).orElseThrow());

        OrdemServico os = OrdemServico.builder()
                .id(idGeneratorService.gerarIdOrdemServico())
                .clienteId(clienteSalvo.getId())
                .veiculoId(veiculoSalvo.getId())
                .mecanicoId(null)
                .status(StatusOrdemServico.EM_EXECUCAO)
                .dataCriacao(LocalDateTime.now().minusDays(2))
                .dataInicioExecucao(LocalDateTime.now().minusHours(1))
                .dataFinalizacao(null)
                .valorTotal(BigDecimal.valueOf(80.00))
                .queixaCliente("Verificar troca de óleo e revisão geral")
                .build();
        osId = ordemServicoRepository.save(OrdemServicoMapper.toEntity(os)).getId();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens - deve listar ordens do cliente")
    void deveListarOrdensDoCliente() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens", clienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados", hasSize(1)))
                .andExpect(jsonPath("$.dados[0].veiculoPlaca").value("ABC1234"))
                .andExpect(jsonPath("$.dados[0].veiculoModelo").value("Fiesta"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens - deve retornar vazio para cliente sem ordens")
    void deveRetornarVazioParaClienteSemOrdens() throws Exception {
        Usuario usuario = Usuario.builder()
                .nome("Maria Silva")
                .email(new Email("maria@email.com"))
                .senha(SENHA_HASH)
                .perfil(PerfilUsuario.CLIENTE)
                .cpfCnpj(new CpfCnpj("98765432100"))
                .build();
        Usuario usuarioSalvo2 = UsuarioMapper.toDomain(usuarioJpaRepository.save(UsuarioMapper.toEntity(usuario)));

        Cliente cliente = Cliente.builder()
                .nome("Maria Silva")
                .usuarioId(usuarioSalvo2.getId())
                .cpfCnpj(new CpfCnpj("98765432100"))
                .telefone(new Telefone("11988888888"))
                .cep(new Cep("01310100"))
                .rua("Rua B")
                .numero("200")
                .complemento(null)
                .cidade("São Paulo")
                .estado("SP")
                .build();
        UUID novoClienteId = clienteJpaRepository.save(ClienteMapper.toEntity(cliente)).getId();

        mockMvc.perform(get("/clientes/{clienteId}/ordens", novoClienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados", hasSize(0)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens/{osId} - deve buscar detalhe da ordem")
    void deveBuscarDetalheDoOrdem() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens/{osId}", clienteId, osId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados[0].id").value(osId))
                .andExpect(jsonPath("$.dados[0].status").value("EM_EXECUCAO"))
                .andExpect(jsonPath("$.dados[0].veiculoPlaca").value("ABC1234"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens/{osId} - deve retornar 404 para ordem inexistente")
    void deveRetornar404ParaOrdemInexistente() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens/9999", clienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /clientes/{clienteId}/ordens/{osId} - deve retornar 404 para ordem de outro cliente")
    void deveRetornar404ParaOrdemDeOutroCliente() throws Exception {
        Usuario usuario = Usuario.builder()
                .nome("Carlos Silva")
                .email(new Email("carlos@email.com"))
                .senha(SENHA_HASH)
                .perfil(PerfilUsuario.CLIENTE)
                .cpfCnpj(new CpfCnpj("55555555555"))
                .build();
        Usuario usuarioSalvo3 = UsuarioMapper.toDomain(usuarioJpaRepository.save(UsuarioMapper.toEntity(usuario)));

        Cliente cliente = Cliente.builder()
                .nome("Carlos Silva")
                .usuarioId(usuarioSalvo3.getId())
                .cpfCnpj(new CpfCnpj("55555555555"))
                .telefone(new Telefone("11999999999"))
                .cep(new Cep("01310100"))
                .rua("Rua C")
                .numero("300")
                .complemento(null)
                .cidade("São Paulo")
                .estado("SP")
                .build();
        UUID outroClienteId = clienteJpaRepository.save(ClienteMapper.toEntity(cliente)).getId();

        mockMvc.perform(get("/clientes/{clienteId}/ordens/{osId}", outroClienteId, osId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /clientes/{clienteId}/ordens - deve retornar 401 sem autenticação")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/ordens", clienteId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
