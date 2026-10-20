package com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence;

import com.fiap.tech_challenge_backend.cadastro.application.ports.out.VeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VeiculoRepositoryAdapter implements VeiculoRepositoryPort {

    private final VeiculoJpaRepository veiculoJpaRepository;

    public VeiculoRepositoryAdapter(VeiculoJpaRepository veiculoJpaRepository) {
        this.veiculoJpaRepository = veiculoJpaRepository;
    }

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        var salvo = this.veiculoJpaRepository.save(VeiculoMapper.toEntity(veiculo));
        return VeiculoMapper.toDomain(salvo);
    }

    @Override
    public boolean existePorPlaca(Placa placa) {
        return this.veiculoJpaRepository.existsByPlacaValor(placa.valor());
    }

    @Override
    public Optional<Veiculo> buscarPorPlaca(Placa placa) {
        return this.veiculoJpaRepository.findByPlacaValor(placa.valor()).map(VeiculoMapper::toDomain);
    }

    @Override
    public Optional<Veiculo> buscarPorId(UUID id) {
        return this.veiculoJpaRepository.findById(id)
                .filter(v -> v.getDeletedAt() == null)
                .map(VeiculoMapper::toDomain);
    }

    @Override
    public List<Veiculo> listar() {
        return this.veiculoJpaRepository.findAllActive().stream()
                .map(VeiculoMapper::toDomain)
                .toList();
    }

    @Override
    public void deletar(UUID id) {
        this.veiculoJpaRepository.deleteById(id);
    }
}
