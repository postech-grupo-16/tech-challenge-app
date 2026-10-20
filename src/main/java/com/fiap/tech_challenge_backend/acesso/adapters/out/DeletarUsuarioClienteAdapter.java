package com.fiap.tech_challenge_backend.acesso.adapters.out;

import com.fiap.tech_challenge_backend.acesso.application.exceptions.UsuarioNaoEncontradoException;
import com.fiap.tech_challenge_backend.acesso.application.ports.out.UsuarioRepositoryPort;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.DeletarUsuarioClientePort;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import org.springframework.stereotype.Component;

/**
 * Adapter de saída que implementa uma porta definida pelo contexto {@code cadastro}
 * ({@link DeletarUsuarioClientePort}). Vive em {@code adapters.out} pelo mesmo
 * motivo de {@link CriarUsuarioClienteAdapter}.
 */
@Component
public class DeletarUsuarioClienteAdapter implements DeletarUsuarioClientePort {

    private final UsuarioRepositoryPort usuarioRepository;

    public DeletarUsuarioClienteAdapter(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void deletar(String cpfCnpj) {
        Usuario usuarioEncontrado = usuarioRepository.procuraPorCpfCnpj(new CpfCnpj(cpfCnpj))
                .orElseThrow(() -> new UsuarioNaoEncontradoException(cpfCnpj));

        usuarioRepository.deletar(usuarioEncontrado.getId());
    }
}
