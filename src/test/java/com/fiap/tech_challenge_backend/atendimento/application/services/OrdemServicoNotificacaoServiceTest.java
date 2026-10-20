package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.EmailSenderPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PdfGeneratorPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrdemServicoNotificacaoService")
class OrdemServicoNotificacaoServiceTest {

    @Mock
    private EmailSenderPort emailSenderPort;

    @Mock
    private PdfGeneratorPort pdfGeneratorPort;

    @Mock
    private OrdemServicoEnriquecimentoService enriquecimentoService;

    @InjectMocks
    private OrdemServicoNotificacaoService service;

    private Cliente clienteComEmail;
    private Cliente clienteSemUsuario;
    private Veiculo veiculo;
    private OsOrcamento orcamento;

    @BeforeEach
    void setUp() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Cliente Teste")
                .email(new Email("cliente@email.com"))
                .build();

        clienteComEmail = new Cliente();
        clienteComEmail.setId(UUID.randomUUID());
        clienteComEmail.setNome("Cliente Teste");
        clienteComEmail.setUsuarioId(usuarioId);
        lenient().when(enriquecimentoService.resolverCliente(clienteComEmail.getId())).thenReturn(clienteComEmail);
        lenient().when(enriquecimentoService.resolverUsuarioDoCliente(clienteComEmail)).thenReturn(Optional.of(usuario));
        lenient().when(enriquecimentoService.resolverEmailCliente(clienteComEmail)).thenReturn("cliente@email.com");

        clienteSemUsuario = new Cliente();
        clienteSemUsuario.setId(UUID.randomUUID());
        clienteSemUsuario.setNome("Sem Usuario");
        lenient().when(enriquecimentoService.resolverCliente(clienteSemUsuario.getId())).thenReturn(clienteSemUsuario);
        lenient().when(enriquecimentoService.resolverUsuarioDoCliente(clienteSemUsuario)).thenReturn(Optional.empty());
        lenient().when(enriquecimentoService.resolverEmailCliente(clienteSemUsuario)).thenReturn(null);

        veiculo = new Veiculo();
        veiculo.setId(UUID.randomUUID());
        veiculo.setModelo("Gol");
        veiculo.setPlaca(new Placa("ABC1234"));
        lenient().when(enriquecimentoService.resolverVeiculo(veiculo.getId())).thenReturn(veiculo);

        orcamento = OsOrcamento.builder()
                .id(1L).tipo(TipoOrcamento.INICIAL).valorTotal(BigDecimal.valueOf(300))
                .dataCriacao(LocalDateTime.now()).build();
    }

    @Nested
    @DisplayName("notificarMudancaStatus")
    class NotificarMudancaStatus {

        @Test
        @DisplayName("deve enviar e-mail quando o cliente possui usuário e e-mail cadastrados")
        void deveEnviarEmailQuandoClienteTemEmail() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(clienteComEmail.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_EXECUCAO).build();

            service.notificarMudancaStatus(os, StatusOrdemServico.RECEBIDA);

            verify(emailSenderPort, times(1))
                    .enviarEmail(eq("cliente@email.com"), anyString(), anyString());
        }

        @Test
        @DisplayName("não deve enviar e-mail quando o cliente não possui usuário cadastrado")
        void naoDeveEnviarQuandoSemUsuario() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(clienteSemUsuario.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_EXECUCAO).build();

            service.notificarMudancaStatus(os, StatusOrdemServico.RECEBIDA);

            verify(emailSenderPort, never()).enviarEmail(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("não deve enviar e-mail quando a ordem de serviço não possui cliente")
        void naoDeveEnviarQuandoSemCliente() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).veiculoId(veiculo.getId()).status(StatusOrdemServico.EM_EXECUCAO).build();

            service.notificarMudancaStatus(os, StatusOrdemServico.RECEBIDA);

            verify(emailSenderPort, never()).enviarEmail(anyString(), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("enviarEmailAprovacaoOrcamento")
    class EnviarEmailAprovacaoOrcamento {

        @Test
        @DisplayName("deve gerar o PDF e enviar o e-mail com anexo para o destinatário informado")
        void deveEnviarComAnexo() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).veiculoId(veiculo.getId()).status(StatusOrdemServico.EM_DIAGNOSTICO).build();

            when(pdfGeneratorPort.gerarDocumentoTexto(orcamento, veiculo)).thenReturn(new byte[]{1, 2, 3});

            service.enviarEmailAprovacaoOrcamento(os, orcamento, "destino@email.com");

            verify(pdfGeneratorPort, times(1)).gerarDocumentoTexto(orcamento, veiculo);
            verify(emailSenderPort, times(1)).enviarEmailComAnexo(
                    eq("destino@email.com"), anyString(), anyString(), any(byte[].class), anyString());
        }
    }

    @Nested
    @DisplayName("enviarEmailOrcamento")
    class EnviarEmailOrcamento {

        @Test
        @DisplayName("deve enviar orçamento INICIAL por e-mail com PDF anexado")
        void deveEnviarOrcamentoInicial() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(clienteComEmail.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_DIAGNOSTICO).build();

            when(pdfGeneratorPort.gerarDocumentoTexto(orcamento, veiculo)).thenReturn(new byte[]{1, 2, 3});

            service.enviarEmailOrcamento(os, orcamento);

            verify(emailSenderPort, times(1)).enviarEmailComAnexo(
                    eq("cliente@email.com"), anyString(), anyString(), any(byte[].class), anyString());
        }

        @Test
        @DisplayName("deve enviar orçamento ADICIONAL com template distinto")
        void deveEnviarOrcamentoAdicional() {
            OsOrcamento orcamentoAdicional = OsOrcamento.builder()
                    .id(2L).tipo(TipoOrcamento.ADICIONAL).valorTotal(BigDecimal.valueOf(150))
                    .dataCriacao(LocalDateTime.now()).build();

            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(clienteComEmail.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_EXECUCAO).build();

            when(pdfGeneratorPort.gerarDocumentoTexto(orcamentoAdicional, veiculo)).thenReturn(new byte[]{1});

            service.enviarEmailOrcamento(os, orcamentoAdicional);

            verify(emailSenderPort, times(1)).enviarEmailComAnexo(
                    eq("cliente@email.com"),
                    org.mockito.ArgumentMatchers.contains("Adicional"),
                    anyString(), any(byte[].class), anyString());
        }

        @Test
        @DisplayName("deve lançar exceção quando a ordem de serviço não possui cliente associado")
        void deveLancarExcecaoSemCliente() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).veiculoId(veiculo.getId()).status(StatusOrdemServico.EM_DIAGNOSTICO).build();

            assertThatThrownBy(() -> service.enviarEmailOrcamento(os, orcamento))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não possui cliente associado");
        }

        @Test
        @DisplayName("deve lançar exceção quando o cliente não possui usuário cadastrado")
        void deveLancarExcecaoSemUsuario() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(clienteSemUsuario.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_DIAGNOSTICO).build();

            assertThatThrownBy(() -> service.enviarEmailOrcamento(os, orcamento))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não possui usuário cadastrado");
        }

        @Test
        @DisplayName("deve envolver falha de envio em RuntimeException com a causa original")
        void deveEnvolverFalhaDeEnvio() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(clienteComEmail.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_DIAGNOSTICO).build();

            when(pdfGeneratorPort.gerarDocumentoTexto(orcamento, veiculo)).thenReturn(new byte[]{1});
            org.mockito.Mockito.doThrow(new RuntimeException("SMTP indisponível"))
                    .when(emailSenderPort).enviarEmailComAnexo(anyString(), anyString(), anyString(), any(byte[].class), anyString());

            assertThatThrownBy(() -> service.enviarEmailOrcamento(os, orcamento))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Falha ao enviar email de orçamento");
        }
    }

    @Nested
    @DisplayName("notificarRespostaOrcamento")
    class NotificarRespostaOrcamento {

        @Test
        @DisplayName("deve enviar e-mail de aprovação quando aprovado=true")
        void deveEnviarEmailDeAprovacao() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(clienteComEmail.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_EXECUCAO).build();

            service.notificarRespostaOrcamento(os, orcamento, true);

            verify(emailSenderPort, times(1)).enviarEmail(
                    eq("cliente@email.com"), org.mockito.ArgumentMatchers.contains("aprovado"), anyString());
        }

        @Test
        @DisplayName("deve enviar e-mail de rejeição quando aprovado=false")
        void deveEnviarEmailDeRejeicao() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(clienteComEmail.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.AGUARDANDO_APROVACAO).build();

            service.notificarRespostaOrcamento(os, orcamento, false);

            verify(emailSenderPort, times(1)).enviarEmail(
                    eq("cliente@email.com"), org.mockito.ArgumentMatchers.contains("rejeitado"), anyString());
        }

        @Test
        @DisplayName("não deve enviar e-mail quando o cliente não possui e-mail cadastrado")
        void naoDeveEnviarSemEmail() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(clienteSemUsuario.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_EXECUCAO).build();

            service.notificarRespostaOrcamento(os, orcamento, true);

            verify(emailSenderPort, never()).enviarEmail(anyString(), anyString(), anyString());
        }
    }
}
