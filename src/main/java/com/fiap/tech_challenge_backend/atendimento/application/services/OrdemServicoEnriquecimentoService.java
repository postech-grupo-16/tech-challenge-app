package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.acesso.application.ports.out.UsuarioRepositoryPort;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.ReferenciaNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PecaInsumoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.VeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Resolve por ID as referências de {@code OrdemServico} para outros contextos delimitados
 * (cliente, veículo, mecânico, peça) e monta o DTO de resposta completo. Colaborador único que
 * substitui as resoluções duplicadas em cada caso de uso — necessário desde que
 * {@code OrdemServico.cliente/veiculo/mecanico} e {@code OsPeca.peca} viraram referências por
 * UUID (ver seção 13 do REFATORACAO_HEXAGONAL.md).
 */
@Service
public class OrdemServicoEnriquecimentoService {

    private final ClienteRepositoryPort clienteRepository;
    private final VeiculoRepositoryPort veiculoRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final PecaInsumoCatalogoRepositoryPort pecaInsumoRepository;

    public OrdemServicoEnriquecimentoService(ClienteRepositoryPort clienteRepository,
                                              VeiculoRepositoryPort veiculoRepository,
                                              UsuarioRepositoryPort usuarioRepository,
                                              PecaInsumoCatalogoRepositoryPort pecaInsumoRepository) {
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pecaInsumoRepository = pecaInsumoRepository;
    }

    public Cliente resolverCliente(UUID clienteId) {
        return clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new ReferenciaNaoEncontradaException("Cliente não encontrado: " + clienteId));
    }

    public Veiculo resolverVeiculo(UUID veiculoId) {
        return veiculoRepository.buscarPorId(veiculoId)
                .orElseThrow(() -> new ReferenciaNaoEncontradaException("Veículo não encontrado: " + veiculoId));
    }

    public Usuario resolverMecanico(UUID mecanicoId) {
        if (mecanicoId == null) {
            return null;
        }
        return usuarioRepository.buscarPorId(mecanicoId).orElse(null);
    }

    public Usuario resolverMecanicoObrigatorio(UUID mecanicoId) {
        return usuarioRepository.buscarPorId(mecanicoId)
                .orElseThrow(() -> new ReferenciaNaoEncontradaException("Mecânico não encontrado: " + mecanicoId));
    }

    public Optional<Usuario> resolverUsuarioDoCliente(Cliente cliente) {
        if (cliente == null || cliente.getUsuarioId() == null) {
            return Optional.empty();
        }
        return usuarioRepository.buscarPorId(cliente.getUsuarioId());
    }

    public String resolverEmailCliente(Cliente cliente) {
        return resolverUsuarioDoCliente(cliente)
                .map(u -> u.getEmail() != null ? u.getEmail().valor() : null)
                .orElse(null);
    }

    public String resolverNomePeca(UUID pecaInsumoId) {
        if (pecaInsumoId == null) {
            return null;
        }
        return pecaInsumoRepository.buscarPorId(pecaInsumoId).map(PecaInsumo::getNome).orElse(null);
    }

    public OrdemServicoResponseDTO montar(OrdemServico os) {
        Cliente cliente = resolverCliente(os.getClienteId());
        Veiculo veiculo = resolverVeiculo(os.getVeiculoId());
        Usuario mecanico = resolverMecanico(os.getMecanicoId());
        String email = resolverEmailCliente(cliente);
        return OrdemServicoResponseDTO.from(os, cliente, email, veiculo, mecanico, this::resolverNomePeca);
    }
}
