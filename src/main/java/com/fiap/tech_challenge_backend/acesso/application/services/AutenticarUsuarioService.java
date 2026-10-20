package com.fiap.tech_challenge_backend.acesso.application.services;

import com.fiap.tech_challenge_backend.acesso.application.exceptions.CredenciaisInvalidasException;
import com.fiap.tech_challenge_backend.acesso.application.ports.out.TokenGeneratorPort;
import com.fiap.tech_challenge_backend.acesso.application.ports.out.UsuarioRepositoryPort;
import com.fiap.tech_challenge_backend.acesso.application.ports.in.AutenticarUsuarioUseCase;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Único caso de uso de autenticação da aplicação: verifica as credenciais
 * através da porta de saída {@link UsuarioRepositoryPort} e delega a emissão do
 * token à porta de saída {@link TokenGeneratorPort}.
 * Contexto Delimitado: acesso
 * Camada: Application
 */
@Service
public class AutenticarUsuarioService implements AutenticarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;

    public AutenticarUsuarioService(
            UsuarioRepositoryPort usuarioRepository,
            PasswordEncoder passwordEncoder,
            TokenGeneratorPort tokenGenerator
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public String autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.procuraPorEmail(new Email(email))
                .filter(candidato -> passwordEncoder.matches(senha, candidato.getSenha()))
                .orElseThrow(CredenciaisInvalidasException::new);

        return tokenGenerator.generateToken(
                usuario.getEmail().valor(),
                Map.of("role", usuario.getPerfil().name())
        );
    }
}
