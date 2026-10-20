package com.fiap.tech_challenge_backend.acesso.application.services;

import com.fiap.tech_challenge_backend.acesso.application.exceptions.CredenciaisInvalidasException;
import com.fiap.tech_challenge_backend.acesso.application.ports.out.TokenGeneratorPort;
import com.fiap.tech_challenge_backend.acesso.application.ports.out.UsuarioRepositoryPort;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do único caso de uso de autenticação, mockando exclusivamente
 * as portas de saída ({@link UsuarioRepositoryPort}, {@link PasswordEncoder},
 * {@link TokenGeneratorPort}) — nenhum contexto Spring é iniciado.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AutenticarUsuarioService")
class AutenticarUsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGeneratorPort tokenGenerator;

    @InjectMocks
    private AutenticarUsuarioService autenticarUsuarioService;

    private Usuario usuario;
    private String email;
    private String senha;

    @BeforeEach
    void setUp() {
        email = "mecanico@oficina.com.br";
        senha = "senha123";

        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("João Mecânico")
                .email(new Email(email))
                .senha("hash_da_senha")
                .perfil(PerfilUsuario.FUNCIONARIO)
                .build();
    }

    @Nested
    @DisplayName("autenticar")
    class Autenticar {

        @Test
        @DisplayName("retorna o token quando e-mail e senha são válidos")
        void retornaTokenComCredenciaisValidas() {
            when(usuarioRepository.procuraPorEmail(new Email(email)))
                    .thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches(senha, usuario.getSenha())).thenReturn(true);
            when(tokenGenerator.generateToken(eq(email), any(Map.class)))
                    .thenReturn("jwt-token-gerado");

            String token = autenticarUsuarioService.autenticar(email, senha);

            assertThat(token).isEqualTo("jwt-token-gerado");
        }

        @Test
        @DisplayName("inclui a role do usuário nas claims do token")
        void incluiRoleNasClaims() {
            when(usuarioRepository.procuraPorEmail(new Email(email)))
                    .thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches(senha, usuario.getSenha())).thenReturn(true);
            when(tokenGenerator.generateToken(anyString(), any(Map.class)))
                    .thenReturn("jwt-token");

            autenticarUsuarioService.autenticar(email, senha);

            var captor = org.mockito.ArgumentCaptor.forClass(Map.class);
            verify(tokenGenerator).generateToken(eq(email), captor.capture());
            assertThat(captor.getValue()).containsEntry("role", PerfilUsuario.FUNCIONARIO.name());
        }

        @Test
        @DisplayName("lança CredenciaisInvalidasException quando e-mail não existe")
        void lancaExcecaoQuandoEmailNaoExiste() {
            when(usuarioRepository.procuraPorEmail(new Email(email)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> autenticarUsuarioService.autenticar(email, senha))
                    .isInstanceOf(CredenciaisInvalidasException.class);

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(tokenGenerator, never()).generateToken(anyString(), any());
        }

        @Test
        @DisplayName("lança CredenciaisInvalidasException quando a senha não confere")
        void lancaExcecaoQuandoSenhaInvalida() {
            when(usuarioRepository.procuraPorEmail(new Email(email)))
                    .thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches(senha, usuario.getSenha())).thenReturn(false);

            assertThatThrownBy(() -> autenticarUsuarioService.autenticar(email, senha))
                    .isInstanceOf(CredenciaisInvalidasException.class);

            verify(tokenGenerator, never()).generateToken(anyString(), any());
        }
    }
}
