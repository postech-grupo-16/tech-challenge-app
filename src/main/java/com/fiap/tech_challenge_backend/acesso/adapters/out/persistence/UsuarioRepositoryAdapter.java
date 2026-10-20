package com.fiap.tech_challenge_backend.acesso.adapters.out.persistence;

import com.fiap.tech_challenge_backend.acesso.application.ports.out.UsuarioRepositoryPort;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        var salvo = usuarioJpaRepository.save(UsuarioMapper.toEntity(usuario));
        return UsuarioMapper.toDomain(salvo);
    }

    @Override
    public boolean existePorEmail(Email email) {
        return usuarioJpaRepository.existsByEmail(email.valor());
    }

    @Override
    public boolean existePorCpfCnpj(CpfCnpj cpfCnpj) {
        return usuarioJpaRepository.existsByCpfCnpj(cpfCnpj.valor());
    }

    @Override
    public Optional<Usuario> procuraPorEmail(Email email) {
        return usuarioJpaRepository.findByEmail(email.valor()).map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> procuraPorCpfCnpj(CpfCnpj cpfCnpj) {
        return usuarioJpaRepository.findByCpfCnpj(cpfCnpj.valor()).map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioJpaRepository.findById(id).map(UsuarioMapper::toDomain);
    }

    @Override
    public void deletar(UUID id) {
        this.usuarioJpaRepository.deleteById(id);
    }
}
