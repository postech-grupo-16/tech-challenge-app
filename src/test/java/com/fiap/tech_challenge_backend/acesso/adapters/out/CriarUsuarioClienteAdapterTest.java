package com.fiap.tech_challenge_backend.acesso.adapters.out;

import com.fiap.tech_challenge_backend.acesso.application.exceptions.UsuarioJaCadastradoException;
import com.fiap.tech_challenge_backend.acesso.application.ports.out.UsuarioRepositoryPort;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.CriarUsuarioClienteCommand;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CriarUsuarioClienteAdapter")
class CriarUsuarioClienteAdapterTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CriarUsuarioClienteAdapter adapter;

    private CriarUsuarioClienteCommand command;

    @BeforeEach
    void setUp() {
        command = new CriarUsuarioClienteCommand(
                "João Silva",
                new Email("joao@email.com"),
                "senha123",
                new Telefone("11987654321"),
                new CpfCnpj("12345678901")
        );
    }

    @Nested
    @DisplayName("criarUsuarioCliente")
    class CriarUsuarioCliente {

        @Test
        @DisplayName("cria o usuário com perfil CLIENTE e senha criptografada")
        void criaUsuarioComSucesso() {
            when(usuarioRepository.existePorEmail(command.email())).thenReturn(false);
            when(usuarioRepository.existePorCpfCnpj(command.cpfCnpj())).thenReturn(false);
            when(passwordEncoder.encode("senha123")).thenReturn("hash-da-senha");
            when(usuarioRepository.salvar(any(Usuario.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Usuario resultado = adapter.criarUsuarioCliente(command);

            assertThat(resultado.getNome()).isEqualTo("João Silva");
            assertThat(resultado.getSenha()).isEqualTo("hash-da-senha");
            assertThat(resultado.getPerfil()).isEqualTo(PerfilUsuario.CLIENTE);

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).salvar(captor.capture());
            assertThat(captor.getValue().getEmail()).isEqualTo(command.email());
        }

        @Test
        @DisplayName("lança UsuarioJaCadastradoException quando o e-mail já existe")
        void lancaExcecaoQuandoEmailJaExiste() {
            when(usuarioRepository.existePorEmail(command.email())).thenReturn(true);

            assertThatThrownBy(() -> adapter.criarUsuarioCliente(command))
                    .isInstanceOf(UsuarioJaCadastradoException.class);

            verify(usuarioRepository, never()).salvar(any());
        }

        @Test
        @DisplayName("lança UsuarioJaCadastradoException quando o CPF/CNPJ já existe")
        void lancaExcecaoQuandoCpfCnpjJaExiste() {
            when(usuarioRepository.existePorEmail(command.email())).thenReturn(false);
            when(usuarioRepository.existePorCpfCnpj(command.cpfCnpj())).thenReturn(true);

            assertThatThrownBy(() -> adapter.criarUsuarioCliente(command))
                    .isInstanceOf(UsuarioJaCadastradoException.class);

            verify(usuarioRepository, never()).salvar(any());
        }
    }
}
