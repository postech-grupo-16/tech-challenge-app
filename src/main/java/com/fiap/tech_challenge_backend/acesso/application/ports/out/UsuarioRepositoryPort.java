package com.fiap.tech_challenge_backend.acesso.application.ports.out;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositoryPort {

    Usuario salvar(Usuario usuario);

    boolean existePorEmail(Email email);

    boolean existePorCpfCnpj(CpfCnpj cpfCnpj);

    Optional<Usuario> procuraPorEmail(Email email);

    Optional<Usuario> procuraPorCpfCnpj(CpfCnpj cpfCnpj);

    Optional<Usuario> buscarPorId(UUID id);

    void deletar(UUID id);
}
