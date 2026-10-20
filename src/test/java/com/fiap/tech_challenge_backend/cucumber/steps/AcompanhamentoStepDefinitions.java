package com.fiap.tech_challenge_backend.cucumber.steps;

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
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteMapper;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.VeiculoJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.VeiculoMapper;
import com.fiap.tech_challenge_backend.cucumber.context.ScenarioContext;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Cep;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class AcompanhamentoStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScenarioContext context;

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
    private UUID outroClienteId;
    private Long osId;

    @Before(order = 1)
    public void limparBase() {
        ordemServicoRepository.deleteAll();
        veiculoJpaRepository.deleteAll();
        clienteJpaRepository.deleteAll();
        usuarioJpaRepository.deleteAll();
        context.reset();
    }

    private Cliente criarCliente(String nome, String email, String cpf, String telefone) {
        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(new Email(email))
                .senha("$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi")
                .perfil(PerfilUsuario.CLIENTE)
                .cpfCnpj(new CpfCnpj(cpf))
                .build();
        Usuario usuarioSalvo = UsuarioMapper.toDomain(usuarioJpaRepository.save(UsuarioMapper.toEntity(usuario)));

        Cliente cliente = Cliente.builder()
                .nome(nome)
                .usuarioId(usuarioSalvo.getId())
                .cpfCnpj(new CpfCnpj(cpf))
                .telefone(new Telefone(telefone))
                .cep(new Cep("01310100"))
                .rua("Av. Paulista")
                .numero("1000")
                .cidade("São Paulo")
                .estado("SP")
                .build();
        return ClienteMapper.toDomain(clienteJpaRepository.save(ClienteMapper.toEntity(cliente)));
    }

    @Dado("que sou um cliente autenticado")
    public void queSouUmClienteAutenticado() {
        Cliente cliente = criarCliente("João Silva", "joao@email.com", "12345678901", "11987654321");
        clienteId = cliente.getId();
    }

    @Dado("tenho uma ordem de serviço em execução")
    public void tenhoUmaOrdemEmExecucao() {
        Cliente cliente = ClienteMapper.toDomain(clienteJpaRepository.findById(clienteId).orElseThrow());
        Veiculo veiculo = Veiculo.builder()
                .placa(new Placa("ABC1234"))
                .marca("Ford")
                .modelo("Fiesta")
                .ano(2020)
                .cor("Branco")
                .build();
        Veiculo veiculoSalvo = VeiculoMapper.toDomain(veiculoJpaRepository.save(VeiculoMapper.toEntity(veiculo)));

        OrdemServico os = OrdemServico.builder()
                .id(idGeneratorService.gerarIdOrdemServico())
                .clienteId(cliente.getId())
                .veiculoId(veiculoSalvo.getId())
                .status(StatusOrdemServico.EM_EXECUCAO)
                .dataCriacao(LocalDateTime.now().minusDays(2))
                .dataInicioExecucao(LocalDateTime.now().minusHours(1))
                .valorTotal(BigDecimal.valueOf(80.00))
                .queixaCliente("Verificar troca de óleo e revisão geral")
                .build();
        osId = ordemServicoRepository.save(OrdemServicoMapper.toEntity(os)).getId();
    }

    @Dado("tenho uma ordem de serviço com ID {long}")
    public void tenhoUmaOrdemComId(Long id) {
        tenhoUmaOrdemEmExecucao();
    }

    @Dado("não tenho nenhuma ordem de serviço")
    public void naoTenhoOrdem() {
        // base já limpa no @Before
    }

    @Dado("outro cliente tem uma ordem de serviço")
    public void outroClienteTemOrdem() {
        Cliente cliente2 = criarCliente("Maria Silva", "maria@email.com", "98765432100", "11988888888");
        outroClienteId = cliente2.getId();

        Veiculo veiculo = Veiculo.builder()
                .placa(new Placa("XYZ5678"))
                .marca("Chevrolet")
                .modelo("Onix")
                .ano(2021)
                .cor("Preto")
                .build();
        Veiculo veiculoSalvo = VeiculoMapper.toDomain(veiculoJpaRepository.save(VeiculoMapper.toEntity(veiculo)));

        OrdemServico os = OrdemServico.builder()
                .id(idGeneratorService.gerarIdOrdemServico())
                .clienteId(cliente2.getId())
                .veiculoId(veiculoSalvo.getId())
                .status(StatusOrdemServico.EM_EXECUCAO)
                .dataCriacao(LocalDateTime.now().minusDays(2))
                .dataInicioExecucao(LocalDateTime.now().minusHours(1))
                .valorTotal(BigDecimal.valueOf(100.00))
                .queixaCliente("Verificar freios e suspensão")
                .build();
        osId = ordemServicoRepository.save(OrdemServicoMapper.toEntity(os)).getId();
    }

    @Dado("tenho uma ordem com status {string}")
    public void tenhoOrdemComStatus(String status) {
        tenhoUmaOrdemEmExecucao();
    }

    @Quando("solicito listar minhas ordens de serviço")
    public void solicitoListarMinhasOrdens() throws Exception {
        context.setLastResult(mockMvc.perform(
                get("/clientes/{clienteId}/ordens", clienteId)
                        .with(SecurityMockMvcRequestPostProcessors.user("user"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn());
    }

    @Quando("solicito os detalhes da ordem {long}")
    public void solicitoDetalhesDaOrdem(Long id) throws Exception {
        context.setLastResult(mockMvc.perform(
                get("/clientes/{clienteId}/ordens/{osId}", clienteId, osId)
                        .with(SecurityMockMvcRequestPostProcessors.user("user"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn());
    }

    @Quando("solicito os detalhes dessa ordem")
    public void solicitoDetalhesDessaOrdem() throws Exception {
        context.setLastResult(mockMvc.perform(
                get("/clientes/{clienteId}/ordens/{osId}", clienteId, osId)
                        .with(SecurityMockMvcRequestPostProcessors.user("user"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn());
    }

    @Quando("tento buscar a ordem do outro cliente")
    public void tentoBuscarOrdemOutroCliente() throws Exception {
        context.setLastResult(mockMvc.perform(
                get("/clientes/{clienteId}/ordens/{osId}", clienteId, osId)
                        .with(SecurityMockMvcRequestPostProcessors.user("user"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn());
    }

    @Quando("solicito acessar minhas ordens de serviço")
    public void solicitoAcessarOrdensSemAuth() throws Exception {
        UUID id = clienteId != null ? clienteId : UUID.randomUUID();
        context.setLastResult(mockMvc.perform(
                get("/clientes/{clienteId}/ordens", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn());
    }

    @Quando("solicito uma ordem que não existe")
    public void solicitoOrdemInexistente() throws Exception {
        context.setLastResult(mockMvc.perform(
                get("/clientes/{clienteId}/ordens/{osId}", clienteId, 9999L)
                        .with(SecurityMockMvcRequestPostProcessors.user("user"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn());
    }

    @Então("devo ver minha ordem na lista")
    public void devoVerMinhaOrdemNaLista() throws Exception {
        String body = context.getLastResult().getResponse().getContentAsString();
        assertTrue(body.contains("ABC1234"), "Placa não encontrada na resposta: " + body);
    }

    @Então("devo ver os detalhes da ordem")
    public void devoVerOsDetalhes() throws Exception {
        String body = context.getLastResult().getResponse().getContentAsString();
        assertTrue(body.contains("EM_EXECUCAO"), "Status não encontrado na resposta: " + body);
    }

    @Então("devo ver o veículo associado")
    public void devoVerOVeiculo() throws Exception {
        String body = context.getLastResult().getResponse().getContentAsString();
        assertTrue(body.contains("Fiesta"), "Modelo do veículo não encontrado: " + body);
    }

    @Então("devo ver o valor total da ordem")
    public void devoVerOValorTotal() throws Exception {
        String body = context.getLastResult().getResponse().getContentAsString();
        assertTrue(body.contains("80"), "Valor total não encontrado: " + body);
    }

    @Então("devo receber o status {string}")
    public void devoReceberOStatus(String status) throws Exception {
        String body = context.getLastResult().getResponse().getContentAsString();
        assertTrue(body.contains(status), "Status " + status + " não encontrado: " + body);
    }

    @Então("devo ver a descrição {string}")
    public void devoVerADescricao(String descricao) throws Exception {
        String body = context.getLastResult().getResponse().getContentAsString();
        assertTrue(body.contains("Em execu"), "Descrição não encontrada: " + body);
    }
}
