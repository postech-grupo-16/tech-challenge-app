package com.fiap.tech_challenge_backend.estoque.application.services;

import com.fiap.tech_challenge_backend.estoque.application.dto.EntradaEstoqueRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.MovimentacaoResponseDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoResponseDTO;
import com.fiap.tech_challenge_backend.estoque.application.exceptions.PecaInsumoNaoEncontradoException;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.AtualizarPecaInsumoUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.BuscarPecaInsumoUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.CadastrarPecaInsumoUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.DarEntradaEstoqueUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.ListarMovimentacoesUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.MovimentarEstoqueUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.out.MovimentacaoRepositoryPort;
import com.fiap.tech_challenge_backend.estoque.application.ports.out.PecaInsumoRepositoryPort;
import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pelo gerenciamento do estoque de peças e insumos.
 * Contexto Delimitado: estoque
 * Camada: Application
 */
@Service
@Transactional
public class EstoqueService implements CadastrarPecaInsumoUseCase, AtualizarPecaInsumoUseCase,
        BuscarPecaInsumoUseCase, DarEntradaEstoqueUseCase, MovimentarEstoqueUseCase, ListarMovimentacoesUseCase {

    private final PecaInsumoRepositoryPort pecaInsumoRepository;
    private final MovimentacaoRepositoryPort movimentacaoRepository;

    /**
     * Dá entrada no estoque de forma unificada: se {@code request.id()} apontar para
     * uma peça/insumo existente, incrementa o estoque (reposição); caso contrário,
     * cadastra uma nova peça/insumo usando a quantidade como estoque inicial.
     * Em ambos os casos gera uma movimentação do tipo ENTRADA.
     */
    public EstoqueService(PecaInsumoRepositoryPort pecaInsumoRepository,
                          MovimentacaoRepositoryPort movimentacaoRepository) {
        this.pecaInsumoRepository = pecaInsumoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Override
    public PecaInsumoResponseDTO darEntrada(EntradaEstoqueRequestDTO request) {
        PecaInsumo peca = request.id() != null
                ? pecaInsumoRepository.buscarPorId(request.id()).orElse(null)
                : null;

        if (peca != null) {
            peca.entrada(request.quantidade());
        } else {
            if (request.nome() == null || request.nome().isBlank()) {
                throw new IllegalArgumentException("O nome é obrigatório para cadastrar uma nova peça/insumo");
            }
            if (request.precoVenda() == null || request.precoCompra() == null) {
                throw new IllegalArgumentException("Preço de venda e de compra são obrigatórios para cadastrar uma nova peça/insumo");
            }
            if (request.tipo() == null) {
                throw new IllegalArgumentException("O tipo (PECA ou INSUMO) é obrigatório para cadastrar uma nova peça/insumo");
            }
            peca = PecaInsumo.builder()
                    .nome(request.nome())
                    .descricao(request.descricao())
                    .precoVenda(request.precoVenda())
                    .precoCompra(request.precoCompra())
                    .quantidadePorUnidade(request.quantidadePorUnidade())
                    .quantidadeEstoque(request.quantidade())
                    .quantidadeMinima(request.quantidadeMinima() != null ? request.quantidadeMinima() : 0)
                    .tipo(request.tipo())
                    .build();
        }

        var salva = pecaInsumoRepository.salvar(peca);
        movimentacaoRepository.salvar(MovimentacaoEstoque.builder()
                .pecaInsumo(salva)
                .tipoMovimentacao(TipoMovimentacao.ENTRADA)
                .quantidade(request.quantidade())
                .observacao(request.observacao())
                .build());
        return PecaInsumoResponseDTO.from(salva);
    }

    @Override
    public PecaInsumoResponseDTO cadastrar(PecaInsumoRequestDTO request) {
        var peca = PecaInsumo.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .precoVenda(request.precoVenda())
                .precoCompra(request.precoCompra())
                .quantidadePorUnidade(request.quantidadePorUnidade())
                .quantidadeEstoque(request.quantidadeEstoque())
                .quantidadeMinima(request.quantidadeMinima())
                .tipo(request.tipo())
                .build();
        return PecaInsumoResponseDTO.from(pecaInsumoRepository.salvar(peca));
    }

    /**
     * Roteia toda alteração de {@code quantidadeEstoque} pelos métodos de invariante do
     * domínio ({@code entrada()}/{@code saida()}) em vez de um setter direto, para que a
     * validação de estoque insuficiente em {@code saida()} também se aplique aqui.
     */
    @Override
    public PecaInsumoResponseDTO atualizar(UUID id, PecaInsumoRequestDTO request) {
        var peca = buscarEntidade(id);
        peca.setNome(request.nome());
        peca.setDescricao(request.descricao());
        peca.setPrecoVenda(request.precoVenda());
        peca.setPrecoCompra(request.precoCompra());
        peca.setQuantidadePorUnidade(request.quantidadePorUnidade());
        peca.setQuantidadeMinima(request.quantidadeMinima());
        peca.setTipo(request.tipo());

        int diferenca = request.quantidadeEstoque() - peca.getQuantidadeEstoque();
        if (diferenca > 0) {
            peca.entrada(diferenca);
        } else if (diferenca < 0) {
            peca.saida(-diferenca);
        }

        var salva = pecaInsumoRepository.salvar(peca);
        if (diferenca != 0) {
            movimentacaoRepository.salvar(MovimentacaoEstoque.builder()
                    .pecaInsumo(salva)
                    .tipoMovimentacao(TipoMovimentacao.AJUSTE)
                    .quantidade(Math.abs(diferenca))
                    .observacao("Ajuste via atualização cadastral")
                    .build());
        }
        return PecaInsumoResponseDTO.from(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public PecaInsumoResponseDTO buscarPorId(UUID id) {
        return PecaInsumoResponseDTO.from(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PecaInsumoResponseDTO> listarTodos(TipoPecaInsumo tipo) {
        List<PecaInsumo> itens = tipo != null
                ? pecaInsumoRepository.buscarPorTipo(tipo)
                : pecaInsumoRepository.buscarTodos();
        return itens.stream().map(PecaInsumoResponseDTO::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PecaInsumoResponseDTO> listarAbaixoDoMinimo() {
        return pecaInsumoRepository.buscarAbaixoDoMinimo().stream()
                .map(PecaInsumoResponseDTO::from)
                .toList();
    }

    @Override
    public void remover(UUID id) {
        var peca = buscarEntidade(id);
        peca.setDeletedAt(java.time.LocalDateTime.now());
        pecaInsumoRepository.salvar(peca);
    }

    @Override
    public void registrarEntrada(UUID id, Integer quantidade, String observacao) {
        var peca = buscarEntidade(id);
        peca.entrada(quantidade);
        pecaInsumoRepository.salvar(peca);
        var movimentacao = new MovimentacaoEstoque();
        movimentacao.setPecaInsumo(peca);
        movimentacao.setTipoMovimentacao(TipoMovimentacao.ENTRADA);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setObservacao(observacao);
        movimentacaoRepository.salvar(movimentacao);
    }

    @Override
    public void registrarSaida(UUID id, Integer quantidade, String observacao) {
        var peca = buscarEntidade(id);
        peca.saida(quantidade);
        pecaInsumoRepository.salvar(peca);
        var movimentacao = new MovimentacaoEstoque();
        movimentacao.setPecaInsumo(peca);
        movimentacao.setTipoMovimentacao(TipoMovimentacao.SAIDA);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setObservacao(observacao);
        movimentacaoRepository.salvar(movimentacao);
    }

    @Override
    public void registrarVenda(UUID id, Integer quantidade, String observacao) {
        var peca = buscarEntidade(id);
        peca.saida(quantidade);
        pecaInsumoRepository.salvar(peca);
        movimentacaoRepository.salvar(MovimentacaoEstoque.builder()
                .pecaInsumo(peca)
                .tipoMovimentacao(TipoMovimentacao.VENDA)
                .quantidade(quantidade)
                .observacao(observacao)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarMovimentacoes(UUID pecaInsumoId) {
        var peca = buscarEntidade(pecaInsumoId);
        return movimentacaoRepository.buscarPorPecaInsumoOrdenadoPorDataDesc(peca).stream()
                .map(MovimentacaoResponseDTO::from)
                .toList();
    }

    @Override
    public void cancelarReserva(UUID id, Integer quantidade, String observacao) {
        var peca = buscarEntidade(id);
        peca.entrada(quantidade);
        pecaInsumoRepository.salvar(peca);
        movimentacaoRepository.salvar(MovimentacaoEstoque.builder()
                .pecaInsumo(peca)
                .tipoMovimentacao(TipoMovimentacao.AJUSTE)
                .quantidade(quantidade)
                .observacao(observacao)
                .build());
    }

    private PecaInsumo buscarEntidade(UUID id) {
        return pecaInsumoRepository.buscarPorId(id)
                .orElseThrow(() -> new PecaInsumoNaoEncontradoException(id));
    }
}
