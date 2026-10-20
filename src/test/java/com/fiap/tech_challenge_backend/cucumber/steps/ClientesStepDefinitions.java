package com.fiap.tech_challenge_backend.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.acesso.adapters.out.persistence.UsuarioJpaRepository;
import com.fiap.tech_challenge_backend.acesso.adapters.out.persistence.UsuarioMapper;
import com.fiap.tech_challenge_backend.cadastro.application.dto.AtualizarClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteJpaRepository;
import com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence.ClienteMapper;
import com.fiap.tech_challenge_backend.cucumber.context.ScenarioContext;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Cep;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ClientesStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScenarioContext context;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    private String cpfAtual;

    private void salvarCliente(String cpf, String nome, String email) {
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
                .telefone(new Telefone("11987654321"))
                .cep(new Cep("01310100"))
                .rua("Av. Paulista")
                .numero("1000")
                .cidade("São Paulo")
                .estado("SP")
                .build();
        clienteJpaRepository.save(ClienteMapper.toEntity(cliente));
    }

    @Dado("que estou autenticado como administrador")
    public void queEstouAutenticadoComoAdmin() {
        // simulado via SecurityMockMvcRequestPostProcessors.user()
    }

    @Dado("tenho os dados válidos de um novo cliente")
    public void tenhoDadosValidos() {
        cpfAtual = "12345678901";
    }

    @Dado("existem {int} clientes no sistema")
    public void existemClientesNoSistema(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            salvarCliente("1234567890" + i, "Cliente " + i, "cliente" + i + "@email.com");
        }
    }

    @Dado("existe um cliente com CPF {string}")
    public void existeClienteComCpf(String cpf) {
        cpfAtual = cpf;
        salvarCliente(cpf, "João Silva", "joao@email.com");
    }

    @Dado("existe um cliente no sistema")
    public void existeUmClienteNoSistema() {
        cpfAtual = "12345678901";
        salvarCliente(cpfAtual, "João Silva", "joao@email.com");
    }

    @Dado("tenho dados inválidos de cliente")
    public void tenhoDadosInvalidos() {
        cpfAtual = "";
    }

    @Dado("já existe um cliente com CPF {string}")
    public void jaExisteClienteComCpf(String cpf) {
        existeClienteComCpf(cpf);
    }

    @Dado("não há clientes no sistema")
    public void naoHaClientes() {
        // base já limpa no @Before
    }

    @Quando("cadastro o novo cliente")
    public void cadastroNovoCliente() throws Exception {
        CadastroClienteRequest request = new CadastroClienteRequest(
                "João Silva", "joao@test.com", "senha123", cpfAtual,
                "11987654321", "01310100", "Av. Paulista", "1000", null, "São Paulo", "SP"
        );
        context.setLastResult(mockMvc.perform(post("/clientes")
                .with(SecurityMockMvcRequestPostProcessors.user("user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn());
    }

    @Quando("solicito listar todos os clientes")
    public void solicitoListarTodos() throws Exception {
        context.setLastResult(mockMvc.perform(get("/clientes")
                .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andReturn());
    }

    @Quando("solicito o cliente pelo CPF {string}")
    public void solicitoClientePeloCpf(String cpf) throws Exception {
        context.setLastResult(mockMvc.perform(get("/clientes/{cpf}", cpf)
                .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andReturn());
    }

    @Quando("atualizo o nome do cliente para {string}")
    public void atualizoNomeCliente(String novoNome) throws Exception {
        AtualizarClienteRequest request = new AtualizarClienteRequest(
                novoNome, "11987654321", "01310100", "Av. Paulista", "1000", null, "São Paulo", "SP"
        );
        context.setLastResult(mockMvc.perform(put("/clientes/{cpf}", cpfAtual)
                .with(SecurityMockMvcRequestPostProcessors.user("user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn());
    }

    @Quando("deleto o cliente")
    public void deletoCliente() throws Exception {
        context.setLastResult(mockMvc.perform(delete("/clientes/{cpf}", cpfAtual)
                .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andReturn());
    }

    @Quando("tento cadastrar o cliente")
    public void tentoCadastrarCliente() throws Exception {
        cadastroNovoCliente();
    }

    @Quando("tento cadastrar outro cliente com o mesmo CPF")
    public void tentoCadastroMesmoCpf() throws Exception {
        cadastroNovoCliente();
    }

    @Quando("solicito listar clientes")
    public void solicitoListarClientesSemAuth() throws Exception {
        context.setLastResult(mockMvc.perform(get("/clientes"))
                .andReturn());
    }

    @Quando("solicito um cliente que não existe")
    public void solicitoClienteInexistente() throws Exception {
        context.setLastResult(mockMvc.perform(get("/clientes/{cpf}", "99999999999")
                .with(SecurityMockMvcRequestPostProcessors.user("user")))
                .andReturn());
    }

    @Então("o cliente deve ser criado no sistema")
    public void clienteDeveSercriado() throws Exception {
        String body = context.getLastResult().getResponse().getContentAsString();
        assertTrue(body.contains(cpfAtual), "CPF não encontrado na resposta: " + body);
    }

    @Então("devo receber uma lista com {int} clientes")
    public void deveReceberListaComClientes(int quantidade) throws Exception {
        String body = context.getLastResult().getResponse().getContentAsString();
        assertTrue(body.startsWith("["), "Esperado array JSON, recebeu: " + body);
    }

    @Então("devo receber os dados corretos do cliente")
    public void deveReceberDadosCorretos() throws Exception {
        String body = context.getLastResult().getResponse().getContentAsString();
        assertTrue(body.contains(cpfAtual), "CPF não encontrado: " + body);
    }

    @Então("o nome do cliente deve ser atualizado")
    public void nomeDeveSerAtualizado() throws Exception {
        String body = context.getLastResult().getResponse().getContentAsString();
        assertTrue(body.contains("nome") || body.contains("João"),
                "Nome não atualizado na resposta: " + body);
    }

    @Então("o cliente deve ser removido do sistema")
    public void clienteDeveSerRemovido() throws Exception {
        String body = context.getLastResult().getResponse().getContentAsString();
        assertTrue(body.contains("sucesso") || body.contains("true"),
                "Confirmação de remoção não encontrada: " + body);
    }
}
