package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.exceptions.OrdemServicoStatusException;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrdemServico - Testes Unitários")
class OrdemServicoTest {

    private OrdemServico ordemServico;
    private Cliente cliente;
    private Veiculo veiculo;
    private Usuario mecanico;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .build();

        veiculo = Veiculo.builder()
                .id(UUID.randomUUID())
                .placa(new Placa("ABC1234"))
                .marca("Ford")
                .modelo("Fiesta")
                .ano(2020)
                .cor("Branco")
                .build();

        mecanico = Usuario.builder()
                .id(UUID.randomUUID())
                .email(new Email("mecanico@oficina.com"))
                .nome("Carlos Mecânico")
                .build();

        ordemServico = OrdemServico.builder()
                .id(1L)
                .clienteId(cliente.getId())
                .veiculoId(veiculo.getId())
                .status(StatusOrdemServico.RECEBIDA)
                .queixaCliente("Carro não liga")
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar ordem de serviço com status padrão RECEBIDA")
    void testCriarOrdemServicoComStatusPadrao() {
        assertNotNull(ordemServico);
        assertEquals(StatusOrdemServico.RECEBIDA, ordemServico.getStatus());
        assertFalse(ordemServico.getUrgente());
        assertEquals(BigDecimal.ZERO, ordemServico.getValorTotalAcumulado());
    }

    @Test
    @DisplayName("Deve concluir diagnóstico e mudar status para AGUARDANDO_APROVACAO")
    void testConcluirDiagnostico() {
        ordemServico.setStatus(StatusOrdemServico.EM_DIAGNOSTICO);
        OsOrcamento orcamento = OsOrcamento.builder()
                .id(1L)
                .tipo(TipoOrcamento.INICIAL)
                .status(StatusOrcamento.PENDENTE)
                .ordemServico(ordemServico)
                .dataCriacao(LocalDateTime.now())
                .build();
        ordemServico.getOrcamentos().add(orcamento);

        LocalDateTime prazo = LocalDateTime.now().plusDays(7);
        ordemServico.concluirDiagnostico(1L, prazo);

        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, ordemServico.getStatus());
        assertEquals(prazo, orcamento.getPrazoEstipulado());
    }

    @Test
    @DisplayName("Deve lançar exceção ao concluir diagnóstico em status inválido")
    void testConcluirDiagnosticoEmStatusInvalido() {
        ordemServico.setStatus(StatusOrdemServico.RECEBIDA);

        assertThrows(OrdemServicoStatusException.class,
                () -> ordemServico.concluirDiagnostico(1L, LocalDateTime.now()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar concluir diagnóstico com orçamento inexistente")
    void testConcluirDiagnosticoComOrcamentoInexistente() {
        ordemServico.setStatus(StatusOrdemServico.EM_DIAGNOSTICO);

        assertThrows(IllegalArgumentException.class,
                () -> ordemServico.concluirDiagnostico(999L, LocalDateTime.now()));
    }

    @Test
    @DisplayName("Deve autorizar pelo cliente e mudar status para EM_EXECUCAO")
    void testAutorizarPeloCliente() {
        ordemServico.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);

        ordemServico.autorizarPeloCliente();

        assertEquals(StatusOrdemServico.EM_EXECUCAO, ordemServico.getStatus());
        assertNotNull(ordemServico.getDataInicioExecucao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao autorizar em status inválido")
    void testAutorizarPeloClienteEmStatusInvalido() {
        ordemServico.setStatus(StatusOrdemServico.RECEBIDA);

        assertThrows(OrdemServicoStatusException.class,
                () -> ordemServico.autorizarPeloCliente());
    }

    @Test
    @DisplayName("Deve definir ordem como urgente")
    void testDefinirUrgente() {
        assertFalse(ordemServico.getUrgente());

        ordemServico.definirUrgente(true);

        assertTrue(ordemServico.getUrgente());
    }

    @Test
    @DisplayName("Deve ignorar definir urgente com valor null")
    void testDefinirUrgenteComNull() {
        boolean original = ordemServico.getUrgente();
        ordemServico.definirUrgente(null);

        assertEquals(original, ordemServico.getUrgente());
    }

    @Test
    @DisplayName("Deve lançar exceção ao marcar como urgente uma OS finalizada")
    void testDefinirUrgenteEmOSFinalizada() {
        ordemServico.setStatus(StatusOrdemServico.FINALIZADA);

        assertThrows(IllegalArgumentException.class,
                () -> ordemServico.definirUrgente(true));
    }

    @Test
    @DisplayName("Deve lançar exceção ao marcar como urgente uma OS entregue")
    void testDefinirUrgenteEmOSEntregue() {
        ordemServico.setStatus(StatusOrdemServico.ENTREGUE);

        assertThrows(IllegalArgumentException.class,
                () -> ordemServico.definirUrgente(true));
    }

    @Test
    @DisplayName("Deve alterar status e mecanico")
    void testAlterarStatus() {
        ordemServico.alterarStatus(StatusOrdemServico.EM_EXECUCAO, mecanico);

        assertEquals(StatusOrdemServico.EM_EXECUCAO, ordemServico.getStatus());
        assertEquals(mecanico.getId(), ordemServico.getMecanicoId());
    }

    @Test
    @DisplayName("Deve alterar apenas status quando mecanico é null")
    void testAlterarStatusSemMecanico() {
        ordemServico.alterarStatus(StatusOrdemServico.EM_EXECUCAO, null);

        assertEquals(StatusOrdemServico.EM_EXECUCAO, ordemServico.getStatus());
        assertNull(ordemServico.getMecanicoId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar retroceder o status")
    void testAlterarStatusNaoPermiteRetroceder() {
        ordemServico.setStatus(StatusOrdemServico.EM_EXECUCAO);

        assertThrows(OrdemServicoStatusException.class,
                () -> ordemServico.alterarStatus(StatusOrdemServico.EM_DIAGNOSTICO, null));

        assertEquals(StatusOrdemServico.EM_EXECUCAO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar status de uma OS já entregue")
    void testAlterarStatusEmOsEntregue() {
        ordemServico.setStatus(StatusOrdemServico.ENTREGUE);

        assertThrows(OrdemServicoStatusException.class,
                () -> ordemServico.alterarStatus(StatusOrdemServico.EM_EXECUCAO, null));
    }

    @Test
    @DisplayName("Deve permitir manter o status ENTREGUE ao reenviar o mesmo status")
    void testAlterarStatusEntregueParaEntregue() {
        ordemServico.setStatus(StatusOrdemServico.ENTREGUE);

        ordemServico.alterarStatus(StatusOrdemServico.ENTREGUE, mecanico);

        assertEquals(StatusOrdemServico.ENTREGUE, ordemServico.getStatus());
        assertEquals(mecanico.getId(), ordemServico.getMecanicoId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando novo status é nulo")
    void testAlterarStatusComStatusNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> ordemServico.alterarStatus(null, null));
    }

    @Test
    @DisplayName("Deve adicionar orçamento inicial e mudar status para AGUARDANDO_APROVACAO")
    void testAdicionarOrcamentoInicial() {
        ordemServico.setStatus(StatusOrdemServico.RECEBIDA);
        OsOrcamento orcamento = OsOrcamento.builder()
                .id(1L)
                .tipo(TipoOrcamento.INICIAL)
                .status(StatusOrcamento.PENDENTE)
                .ordemServico(ordemServico)
                .dataCriacao(LocalDateTime.now())
                .build();

        ordemServico.adicionarOrcamento(orcamento);

        assertEquals(1, ordemServico.getOrcamentos().size());
        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar segundo orçamento inicial")
    void testAdicionarSegundoOrcamentoInicial() {
        OsOrcamento primeiro = OsOrcamento.builder()
                .id(1L)
                .tipo(TipoOrcamento.INICIAL)
                .status(StatusOrcamento.PENDENTE)
                .ordemServico(ordemServico)
                .dataCriacao(LocalDateTime.now())
                .build();
        ordemServico.adicionarOrcamento(primeiro);

        OsOrcamento segundo = OsOrcamento.builder()
                .id(2L)
                .tipo(TipoOrcamento.INICIAL)
                .status(StatusOrcamento.PENDENTE)
                .ordemServico(ordemServico)
                .dataCriacao(LocalDateTime.now())
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> ordemServico.adicionarOrcamento(segundo));
    }

    @Test
    @DisplayName("Deve adicionar orçamento adicional sem mudar status")
    void testAdicionarOrcamentoAdicional() {
        ordemServico.setStatus(StatusOrdemServico.EM_EXECUCAO);
        OsOrcamento orcamento = OsOrcamento.builder()
                .id(1L)
                .tipo(TipoOrcamento.ADICIONAL)
                .status(StatusOrcamento.PENDENTE)
                .ordemServico(ordemServico)
                .dataCriacao(LocalDateTime.now())
                .build();

        ordemServico.adicionarOrcamento(orcamento);

        assertEquals(StatusOrdemServico.EM_EXECUCAO, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve aprovar orçamento e acumular valor")
    void testAprovarOrcamento() {
        ordemServico.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        OsOrcamento orcamento = OsOrcamento.builder()
                .id(1L)
                .tipo(TipoOrcamento.INICIAL)
                .status(StatusOrcamento.PENDENTE)
                .valorTotal(BigDecimal.valueOf(500))
                .ordemServico(ordemServico)
                .dataCriacao(LocalDateTime.now())
                .build();
        ordemServico.getOrcamentos().add(orcamento);

        ordemServico.aprovarOrcamento(1L);

        assertEquals(StatusOrcamento.APROVADO, orcamento.getStatus());
        assertEquals(StatusOrdemServico.EM_EXECUCAO, ordemServico.getStatus());
        assertEquals(BigDecimal.valueOf(500), ordemServico.getValorTotalAcumulado());
    }

    @Test
    @DisplayName("Deve lançar exceção ao aprovar em status inválido")
    void testAprovarOrcamentoEmStatusInvalido() {
        ordemServico.setStatus(StatusOrdemServico.RECEBIDA);
        OsOrcamento orcamento = OsOrcamento.builder()
                .id(1L)
                .tipo(TipoOrcamento.INICIAL)
                .status(StatusOrcamento.PENDENTE)
                .ordemServico(ordemServico)
                .dataCriacao(LocalDateTime.now())
                .build();
        ordemServico.getOrcamentos().add(orcamento);

        assertThrows(OrdemServicoStatusException.class,
                () -> ordemServico.aprovarOrcamento(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção ao aprovar orçamento inexistente")
    void testAprovarOrcamentoInexistente() {
        ordemServico.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);

        assertThrows(IllegalArgumentException.class,
                () -> ordemServico.aprovarOrcamento(999L));
    }

    @Test
    @DisplayName("Deve rejeitar orçamento adicional")
    void testRejeitarOrcamentoAdicional() {
        OsOrcamento orcamento = OsOrcamento.builder()
                .id(1L)
                .tipo(TipoOrcamento.ADICIONAL)
                .status(StatusOrcamento.PENDENTE)
                .ordemServico(ordemServico)
                .dataCriacao(LocalDateTime.now())
                .build();
        ordemServico.getOrcamentos().add(orcamento);

        ordemServico.rejeitarOrcamento(1L);

        assertEquals(StatusOrcamento.REJEITADO, orcamento.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao rejeitar orçamento inicial")
    void testRejeitarOrcamentoInicial() {
        OsOrcamento orcamento = OsOrcamento.builder()
                .id(1L)
                .tipo(TipoOrcamento.INICIAL)
                .status(StatusOrcamento.PENDENTE)
                .ordemServico(ordemServico)
                .dataCriacao(LocalDateTime.now())
                .build();
        ordemServico.getOrcamentos().add(orcamento);

        assertThrows(IllegalArgumentException.class,
                () -> ordemServico.rejeitarOrcamento(1L));
    }
}
