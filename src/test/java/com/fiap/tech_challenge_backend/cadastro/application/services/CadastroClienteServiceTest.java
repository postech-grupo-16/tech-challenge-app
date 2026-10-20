package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.ClienteJaCadastradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.CriarUsuarioClienteCommand;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.CriarUsuarioClientePort;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Cep;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CadastroClienteService")
class CadastroClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private CriarUsuarioClientePort criarUsuarioClientePort;

    @InjectMocks
    private CadastroClienteService cadastroClienteService;

    private CadastroClienteRequest request;
    private Usuario usuario;
    private Cliente cliente;
    private UUID clienteId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();

        request = new CadastroClienteRequest(
                "João Silva", "joao@email.com", "senha1234",
                "12345678901", "11987654321", "01310100",
                "Av. Paulista", "1000", null, "São Paulo", "SP"
        );

        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .email(new Email("joao@email.com"))
                .senha("$2a$10$hashedSenha")
                .perfil(PerfilUsuario.CLIENTE)
                .cpfCnpj(new CpfCnpj("12345678901"))
                .build();

        cliente = Cliente.builder()
                .id(clienteId)
                .usuarioId(usuario.getId())
                .nome("João Silva")
                .cpfCnpj(new CpfCnpj("12345678901"))
                .telefone(new Telefone("11987654321"))
                .cep(new Cep("01310100"))
                .rua("Av. Paulista")
                .numero("1000")
                .cidade("São Paulo")
                .estado("SP")
                .build();
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("deve cadastrar cliente com sucesso")
        void deveCadastrarComSucesso() {
            when(clienteRepository.existePorCpfCnpj(any(CpfCnpj.class))).thenReturn(false);
            when(criarUsuarioClientePort.criarUsuarioCliente(any(CriarUsuarioClienteCommand.class))).thenReturn(usuario);
            when(clienteRepository.salvar(any(Cliente.class))).thenReturn(cliente);

            var response = cadastroClienteService.execute(request);

            assertThat(response.id()).isEqualTo(clienteId);
            assertThat(response.nome()).isEqualTo("João Silva");
            assertThat(response.cpfCnpj()).isEqualTo("12345678901");
            assertThat(response.cidade()).isEqualTo("São Paulo");
            verify(criarUsuarioClientePort).criarUsuarioCliente(any(CriarUsuarioClienteCommand.class));
            verify(clienteRepository).salvar(any(Cliente.class));
        }

        @Test
        @DisplayName("deve lançar ClienteJaCadastradoException quando CPF/CNPJ já existe")
        void deveLancarExcecaoQuandoJaCadastrado() {
            when(clienteRepository.existePorCpfCnpj(any(CpfCnpj.class))).thenReturn(true);

            assertThatThrownBy(() -> cadastroClienteService.execute(request))
                    .isInstanceOf(ClienteJaCadastradoException.class);

            verify(criarUsuarioClientePort, never()).criarUsuarioCliente(any());
            verify(clienteRepository, never()).salvar(any());
        }
    }
}
