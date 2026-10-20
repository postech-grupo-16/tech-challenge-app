package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.PecaOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.ServicoOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.ReferenciaNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PecaInsumoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.ServicoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CriarOrcamentoService")
class CriarOrcamentoServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private ServicoCatalogoRepositoryPort servicoCatalogoRepository;

    @Mock
    private PecaInsumoCatalogoRepositoryPort pecaInsumoRepository;

    @Mock
    private IdGeneratorService idGeneratorService;

    @Mock
    private OrdemServicoNotificacaoService notificacaoService;

    @InjectMocks
    private CriarOrcamentoService service;

    private OrdemServico os;

    @BeforeEach
    void setUp() {
        os = OrdemServico.builder()
                .id(1L)
                .status(StatusOrdemServico.EM_DIAGNOSTICO)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .build();

        lenient().when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
        // O adapter recebe a mesma instância de OrdemServico e a devolve já com o orçamento adicionado
        lenient().when(ordemServicoRepository.salvar(any(OrdemServico.class)))
                .thenAnswer((Answer<OrdemServico>) invocation -> invocation.getArgument(0));
    }

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("deve criar orçamento com serviço e peça, aplicando os preços atuais do catálogo/estoque")
        void deveCriarComServicoEPeca() {
            UUID servicoId = UUID.randomUUID();
            UUID pecaId = UUID.randomUUID();

            CriarOrcamentoRequestDTO request = new CriarOrcamentoRequestDTO(
                    TipoOrcamento.INICIAL,
                    "5 dias",
                    List.of(new ServicoOrcamentoRequestDTO(servicoId)),
                    List.of(new PecaOrcamentoRequestDTO(pecaId, 2))
            );

            ServicoCatalogo servico = ServicoCatalogo.builder()
                    .id(servicoId).nome("Troca de óleo").precoMaoDeObra(BigDecimal.valueOf(150))
                    .categoria("PREVENTIVA").build();

            PecaInsumo peca = PecaInsumo.builder()
                    .id(pecaId).nome("Filtro de óleo").precoVenda(BigDecimal.valueOf(50))
                    .build();

            when(idGeneratorService.gerarIdOrcamento()).thenReturn(1L);
            when(servicoCatalogoRepository.buscarPorId(servicoId)).thenReturn(Optional.of(servico));
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));

            OrcamentoResponseDTO resultado = service.criar(1L, request);

            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(os.getOrcamentos()).hasSize(1);
            assertThat(os.getOrcamentos().get(0).getServicos()).hasSize(1);
            assertThat(os.getOrcamentos().get(0).getPecas()).hasSize(1);
        }

        @Test
        @DisplayName("deve lançar exceção quando serviço do catálogo não é encontrado")
        void deveLancarExcecaoQuandoServicoNaoEncontrado() {
            UUID servicoId = UUID.randomUUID();
            CriarOrcamentoRequestDTO request = new CriarOrcamentoRequestDTO(
                    TipoOrcamento.INICIAL, "5 dias",
                    List.of(new ServicoOrcamentoRequestDTO(servicoId)), List.of());

            when(idGeneratorService.gerarIdOrcamento()).thenReturn(1L);
            when(servicoCatalogoRepository.buscarPorId(servicoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.criar(1L, request))
                    .isInstanceOf(ReferenciaNaoEncontradaException.class)
                    .hasMessageContaining("Serviço não encontrado");
        }

        @Test
        @DisplayName("deve lançar exceção quando peça do estoque não é encontrada")
        void deveLancarExcecaoQuandoPecaNaoEncontrada() {
            UUID pecaId = UUID.randomUUID();
            CriarOrcamentoRequestDTO request = new CriarOrcamentoRequestDTO(
                    TipoOrcamento.INICIAL, "5 dias",
                    List.of(), List.of(new PecaOrcamentoRequestDTO(pecaId, 1)));

            when(idGeneratorService.gerarIdOrcamento()).thenReturn(1L);
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.criar(1L, request))
                    .isInstanceOf(ReferenciaNaoEncontradaException.class)
                    .hasMessageContaining("Peça não encontrada");
        }

        @Test
        @DisplayName("deve lançar exceção quando a ordem de serviço não existe")
        void deveLancarExcecaoQuandoOrdemNaoExiste() {
            CriarOrcamentoRequestDTO request = new CriarOrcamentoRequestDTO(
                    TipoOrcamento.INICIAL, "5 dias", List.of(), List.of());

            when(ordemServicoRepository.buscarPorId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.criar(999L, request))
                    .isInstanceOf(OrdemServicoNaoEncontradaException.class);
        }
    }
}
