package com.fiap.tech_challenge_backend.acesso.adapters.out;

import com.fiap.tech_challenge_backend.acesso.application.exceptions.UsuarioJaCadastradoException;
import com.fiap.tech_challenge_backend.acesso.application.ports.out.UsuarioRepositoryPort;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.acesso.domain.enums.PerfilUsuario;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.CriarUsuarioClienteCommand;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.CriarUsuarioClientePort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter de saída que implementa uma porta definida pelo contexto {@code cadastro}
 * ({@link CriarUsuarioClientePort}). Vive em {@code adapters.out} porque é um
 * adapter plugável, não uma peça do núcleo de aplicação de {@code acesso}.
 */
@Component
public class CriarUsuarioClienteAdapter implements CriarUsuarioClientePort {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CriarUsuarioClienteAdapter(
            UsuarioRepositoryPort usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario criarUsuarioCliente(CriarUsuarioClienteCommand command) {
        if (usuarioRepository.existePorEmail(command.email())) {
            throw new UsuarioJaCadastradoException("email", command.email().valor());
        }
        if (usuarioRepository.existePorCpfCnpj(command.cpfCnpj())) {
            throw new UsuarioJaCadastradoException("CPF/CNPJ", command.cpfCnpj().valor());
        }

        String senhaCriptografada = passwordEncoder.encode(command.senha());

        Usuario usuario = Usuario.builder()
                .nome(command.nome())
                .email(command.email())
                .senha(senhaCriptografada)
                .telefone(command.telefone())
                .cpfCnpj(command.cpfCnpj())
                .perfil(PerfilUsuario.CLIENTE)
                .build();

        return usuarioRepository.salvar(usuario);
    }

}
