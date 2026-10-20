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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Concentra a construção de templates de e-mail e o envio de notificações relacionadas
 * a Ordens de Serviço e Orçamentos. Extraído do antigo {@code OrdemServicoService} para que
 * cada caso de uso não precise reimplementar a mesma lógica de notificação.
 * Contexto Delimitado: atendimento
 * Camada: Application
 */
@Service
public class OrdemServicoNotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(OrdemServicoNotificacaoService.class);

    private final EmailSenderPort emailSenderPort;
    private final PdfGeneratorPort pdfGeneratorPort;
    private final OrdemServicoEnriquecimentoService enriquecimentoService;

    public OrdemServicoNotificacaoService(EmailSenderPort emailSenderPort, PdfGeneratorPort pdfGeneratorPort,
                                           OrdemServicoEnriquecimentoService enriquecimentoService) {
        this.emailSenderPort = emailSenderPort;
        this.pdfGeneratorPort = pdfGeneratorPort;
        this.enriquecimentoService = enriquecimentoService;
    }

    public void notificarMudancaStatus(OrdemServico os, StatusOrdemServico statusAnterior) {
        if (os.getClienteId() == null) {
            return;
        }
        Cliente cliente = enriquecimentoService.resolverCliente(os.getClienteId());
        Veiculo veiculo = enriquecimentoService.resolverVeiculo(os.getVeiculoId());
        String emailCliente = obterEmailCliente(os, cliente);
        if (emailCliente == null) {
            return;
        }

        StatusOrdemServico statusAtual = os.getStatus();
        String assunto = String.format("Atualização da OS #%s: %s", os.getId(), statusAtual.name());
        String corpoHtml = montarTemplateEmailStatus(os, statusAnterior, cliente, veiculo);

        emailSenderPort.enviarEmail(emailCliente, assunto, corpoHtml);

        log.info("E-mail de status enviado | OS: {} | De: {} | Para: {}",
                os.getId(),
                statusAnterior != null ? statusAnterior.name() : "NENHUM",
                statusAtual.name());
    }

    public void enviarEmailAprovacaoOrcamento(OrdemServico os, OsOrcamento orcamento, String emailDestino) {
        Veiculo veiculo = enriquecimentoService.resolverVeiculo(os.getVeiculoId());
        String linkAprovacao = String.format(
                "https://api.oficina.local/api/public/atendimento/ordens/%s/autorizar?orcamento=%s",
                os.getId(), orcamento.getId()
        );

        byte[] documento = pdfGeneratorPort.gerarDocumentoTexto(orcamento, veiculo);
        String corpoHtml = montarTemplateEmailAprovacao(os, orcamento, linkAprovacao, veiculo);

        emailSenderPort.enviarEmailComAnexo(
                emailDestino,
                String.format("Seu orçamento está pronto para aprovação - OS #%s", os.getId()),
                corpoHtml,
                documento,
                String.format("orcamento_os_%s.pdf", os.getId())
        );
    }

    public void enviarEmailOrcamento(OrdemServico os, OsOrcamento orcamento) {
        if (os.getClienteId() == null) {
            throw new IllegalArgumentException("Ordem de serviço não possui cliente associado");
        }
        Cliente cliente = enriquecimentoService.resolverCliente(os.getClienteId());
        Veiculo veiculo = enriquecimentoService.resolverVeiculo(os.getVeiculoId());
        validarEmailClienteOuLancarExcecao(cliente);

        String emailCliente = obterEmailCliente(os, cliente);

        log.info("Enviando email de orçamento | OS: {} | Orcamento: {} | Tipo: {} | Cliente: {}",
                os.getId(), orcamento.getId(), orcamento.getTipo(), emailCliente);

        try {
            String linkAprovar = String.format(
                    "http://localhost:8080/api/public/atendimento/ordens/%s/orcamentos/%s/aprovar",
                    os.getId(), orcamento.getId()
            );
            String linkRejeitar = String.format(
                    "http://localhost:8080/api/public/atendimento/ordens/%s/orcamentos/%s/rejeitar",
                    os.getId(), orcamento.getId()
            );

            byte[] documento = pdfGeneratorPort.gerarDocumentoTexto(orcamento, veiculo);

            String corpoHtml = orcamento.getTipo() == TipoOrcamento.ADICIONAL
                    ? montarTemplateEmailOrcamentoAdicional(os, orcamento, linkAprovar, linkRejeitar, cliente, veiculo)
                    : montarTemplateEmailOrcamento(os, orcamento, linkAprovar, linkRejeitar, cliente, veiculo);

            String assunto = orcamento.getTipo() == TipoOrcamento.ADICIONAL
                    ? String.format("Orçamento Adicional Necessário - OS #%s | Novos Problemas Identificados", os.getId())
                    : String.format("Seu orçamento está pronto para análise - OS #%s", os.getId());

            emailSenderPort.enviarEmailComAnexo(
                    emailCliente,
                    assunto,
                    corpoHtml,
                    documento,
                    String.format("orcamento_os_%s.pdf", os.getId())
            );

            log.info("Email de orçamento enviado com sucesso | OS: {} | Tipo: {} | Destinatário: {}",
                    os.getId(), orcamento.getTipo(), emailCliente);

        } catch (Exception e) {
            log.error("Erro ao enviar email de orçamento | OS: {} | Email: {} | Erro: {}",
                    os.getId(), emailCliente, e.getMessage(), e);
            throw new RuntimeException(
                    "Falha ao enviar email de orçamento para: " + emailCliente + ". Motivo: " + e.getMessage(), e);
        }
    }

    public void notificarRespostaOrcamento(OrdemServico os, OsOrcamento orcamento, boolean aprovado) {
        if (os.getClienteId() == null) {
            return;
        }
        Cliente cliente = enriquecimentoService.resolverCliente(os.getClienteId());
        Veiculo veiculo = enriquecimentoService.resolverVeiculo(os.getVeiculoId());
        String emailCliente = obterEmailCliente(os, cliente);
        if (emailCliente == null) {
            return;
        }

        String assunto = aprovado
                ? String.format("Orçamento aprovado - OS #%s", os.getId())
                : String.format("Orçamento rejeitado - OS #%s", os.getId());

        String corpoHtml = aprovado
                ? montarTemplateEmailOrcamentoAprovado(os, orcamento, cliente, veiculo)
                : montarTemplateEmailOrcamentoRejeitado(os, orcamento, cliente, veiculo);

        emailSenderPort.enviarEmail(emailCliente, assunto, corpoHtml);

        log.info("E-mail de resposta de orçamento enviado | OS: {} | Orcamento: {} | Aprovado: {}",
                os.getId(), orcamento.getId(), aprovado);
    }

    private void validarEmailClienteOuLancarExcecao(Cliente cliente) {
        Optional<Usuario> usuario = enriquecimentoService.resolverUsuarioDoCliente(cliente);
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cliente '" + cliente.getNome() + "' não possui usuário cadastrado. " +
                    "Impossível enviar orçamento por email.");
        }

        if (usuario.get().getEmail() == null) {
            throw new IllegalArgumentException(
                    "Cliente '" + cliente.getNome() + "' não possui email cadastrado. " +
                    "Impossível enviar orçamento por email.");
        }
    }

    private String obterEmailCliente(OrdemServico os, Cliente cliente) {
        String email = enriquecimentoService.resolverEmailCliente(cliente);
        if (email == null) {
            log.warn("OS {} sem email de cliente cadastrado. Notificação de status não enviada.", os.getId());
        }
        return email;
    }

    private String formatarStatus(StatusOrdemServico status) {
        if (status == null) {
            return "Criação da OS";
        }

        return switch (status) {
            case RECEBIDA -> "Recebida";
            case EM_DIAGNOSTICO -> "Diagnóstico";
            case AGUARDANDO_APROVACAO -> "Aguardando Aprovação";
            case EM_EXECUCAO -> "Em Execução";
            case FINALIZADA -> "Finalizada";
            case ENTREGUE -> "Entregue";
        };
    }

    private String mensagemStatus(StatusOrdemServico status) {
        return switch (status) {
            case RECEBIDA -> "Sua ordem foi recebida e entrou na fila de atendimento.";
            case EM_DIAGNOSTICO -> "Nossa equipe está realizando o diagnóstico do veículo.";
            case AGUARDANDO_APROVACAO -> "O orçamento foi concluído e aguarda sua aprovação.";
            case EM_EXECUCAO -> "Os serviços foram aprovados e já estão em execução.";
            case FINALIZADA -> "Os serviços foram finalizados e o veículo está pronto para retirada.";
            case ENTREGUE -> "Seu veículo foi entregue com sucesso.";
        };
    }

    private String montarTemplateEmailStatus(OrdemServico os, StatusOrdemServico statusAnterior,
                                              Cliente cliente, Veiculo veiculo) {
        return "<!DOCTYPE html>" +
                "<html lang=\"pt-BR\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }" +
                "    .content { background-color: #ecf0f1; padding: 20px; border-radius: 0 0 5px 5px; }" +
                "    .box { background-color: #fff; padding: 15px; border-left: 4px solid #3498db; margin: 15px 0; }" +
                "    .footer { margin-top: 20px; font-size: 12px; color: #7f8c8d; text-align: center; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class=\"container\">" +
                "    <div class=\"header\">" +
                "      <h1>Status da sua Ordem de Serviço</h1>" +
                "    </div>" +
                "    <div class=\"content\">" +
                "      <p>Olá, " + cliente.getNome() + ".</p>" +
                "      <p>Sua OS #" + os.getId() + " teve uma atualização de status.</p>" +
                "      <div class=\"box\">" +
                "        <p><strong>Status anterior:</strong> " + formatarStatus(statusAnterior) + "</p>" +
                "        <p><strong>Status atual:</strong> " + formatarStatus(os.getStatus()) + "</p>" +
                "        <p><strong>Veículo:</strong> " + veiculo.getModelo() + " - " + veiculo.getPlaca() + "</p>" +
                "      </div>" +
                "      <p>" + mensagemStatus(os.getStatus()) + "</p>" +
                "    </div>" +
                "    <div class=\"footer\">" +
                "      <p>Este é um e-mail automático. Não responda diretamente.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    private String montarTemplateEmailAprovacao(OrdemServico os, OsOrcamento orcamento, String linkAprovacao,
                                                 Veiculo veiculo) {
        return "<!DOCTYPE html>" +
                "<html lang=\"pt-BR\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }" +
                "    .header h1 { margin: 0; font-size: 24px; }" +
                "    .content { background-color: #ecf0f1; padding: 20px; border-radius: 0 0 5px 5px; }" +
                "    .content p { margin: 10px 0; }" +
                "    .info-box { background-color: #fff; padding: 15px; border-left: 4px solid #3498db; margin: 15px 0; }" +
                "    .info-box strong { color: #2c3e50; }" +
                "    .cta-button { display: inline-block; margin: 20px 0; padding: 15px 40px; background-color: #27ae60; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px; }" +
                "    .cta-button:hover { background-color: #229954; }" +
                "    .footer { margin-top: 20px; padding-top: 20px; border-top: 1px solid #bdc3c7; font-size: 12px; color: #7f8c8d; text-align: center; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class=\"container\">" +
                "    <div class=\"header\">" +
                "      <h1>Seu Orçamento Está Pronto!</h1>" +
                "    </div>" +
                "    <div class=\"content\">" +
                "      <p>Olá,</p>" +
                "      <p>Temos o prazer de informar que <strong>o diagnóstico do seu veículo foi concluído</strong> e seu orçamento está pronto para análise e aprovação.</p>" +
                "      <div class=\"info-box\">" +
                "        <p><strong>Número da Ordem de Serviço:</strong> " + os.getId() + "</p>" +
                "        <p><strong>Veículo:</strong> " + veiculo.getModelo() + "</p>" +
                "        <p><strong>Placa:</strong> " + veiculo.getPlaca() + "</p>" +
                "        <p><strong>Valor Total do Orçamento:</strong> <strong style=\"color: #27ae60; font-size: 18px;\">R$ " + String.format("%.2f", orcamento.getValorTotal()) + "</strong></p>" +
                "      </div>" +
                "      <p>O arquivo em anexo contém todos os detalhes do orçamento, incluindo:</p>" +
                "      <ul>" +
                "        <li>Lista de serviços e mão de obra</li>" +
                "        <li>Peças e insumos necessários</li>" +
                "        <li>Prazo estimado para conclusão</li>" +
                "      </ul>" +
                "      <p><strong>Para autorizar o início dos trabalhos, clique no botão abaixo:</strong></p>" +
                "      <center>" +
                "        <a href=\"" + linkAprovacao + "\" class=\"cta-button\">Aprovar Orçamento</a>" +
                "      </center>" +
                "      <p>Ou copie e cole este link em seu navegador:</p>" +
                "      <p><small>" + linkAprovacao + "</small></p>" +
                "      <p>Se tiver dúvidas sobre o orçamento, não hesite em entrar em contato conosco através do telefone ou e-mail.</p>" +
                "    </div>" +
                "    <div class=\"footer\">" +
                "      <p>Oficina Mecânica Premium<br>" +
                "      Telefone: (11) 3000-0000<br>" +
                "      E-mail: contato@oficina.com.br</p>" +
                "      <p style=\"margin-top: 15px; color: #95a5a6;\">Este é um e-mail automático. Por favor, não responda.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    private String montarTemplateEmailOrcamentoAdicional(OrdemServico os, OsOrcamento orcamento, String linkAprovar,
                                                           String linkRejeitar, Cliente cliente, Veiculo veiculo) {
        return "<!DOCTYPE html>" +
                "<html lang=\"pt-BR\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }" +
                "    .alert-box { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 15px 0; }" +
                "    .alert-box strong { color: #856404; }" +
                "    .content { background-color: #ecf0f1; padding: 20px; border-radius: 0 0 5px 5px; }" +
                "    .content p { margin: 10px 0; }" +
                "    .info-box { background-color: #fff; padding: 15px; border-left: 4px solid #3498db; margin: 15px 0; }" +
                "    .info-box strong { color: #2c3e50; }" +
                "    .problem-box { background-color: #fff; padding: 15px; border-left: 4px solid #e74c3c; margin: 15px 0; }" +
                "    .problem-box strong { color: #c0392b; }" +
                "    .action-box { background-color: #e8f5e9; border-left: 4px solid #27ae60; padding: 15px; margin: 15px 0; }" +
                "    .action-box strong { color: #27ae60; }" +
                "    .contact-box { background-color: #e3f2fd; border-left: 4px solid #2196f3; padding: 15px; margin: 15px 0; }" +
                "    .contact-box strong { color: #1565c0; }" +
                "    .cta-button { display: inline-block; padding: 15px 40px; background-color: #27ae60; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; margin: 20px 0; }" +
                "    .cta-button:hover { background-color: #229954; }" +
                "    .footer { font-size: 12px; color: #7f8c8d; text-align: center; margin-top: 20px; }" +
                "    .warning-icon { font-size: 24px; margin-right: 10px; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class=\"container\">" +
                "    <div class=\"header\">" +
                "      <h1>⚠️ Orçamento Adicional Necessário</h1>" +
                "      <p style=\"margin: 10px 0; font-size: 14px;\">Novos Problemas Identificados no Seu Veículo</p>" +
                "    </div>" +
                "    <div class=\"content\">" +
                "      <p>Olá <strong>" + cliente.getNome() + "</strong>,</p>" +
                "" +
                "      <div class=\"alert-box\">" +
                "        <strong>ℹ️ Informação Importante:</strong>" +
                "        <p>Durante o diagnóstico técnico de seu veículo, nossos especialistas identificaram <strong>problemas adicionais</strong> que precisam de atenção. " +
                "        Para garantir a segurança e o bom funcionamento do seu veículo, preparamos um <strong>orçamento complementar</strong>.</p>" +
                "      </div>" +
                "" +
                "      <p>Entendemos que descobrir novos problemas pode ser inesperado. Por esse motivo, queremos ser <strong>transparente</strong> e oferecer todas as " +
                "      informações que você precisa para tomar a melhor decisão.</p>" +
                "" +
                "      <div class=\"info-box\">" +
                "        <p><strong>📋 Informações da Ordem de Serviço</strong></p>" +
                "        <p><strong>Número da OS:</strong> #" + os.getId() + "</p>" +
                "        <p><strong>Veículo:</strong> " + veiculo.getModelo() + " - Placa: " + veiculo.getPlaca() + "</p>" +
                "        <p><strong>Status Atual:</strong> Em Diagnóstico Avançado</p>" +
                "      </div>" +
                "" +
                "      <div class=\"problem-box\">" +
                "        <p><strong>🔧 Por Que um Orçamento Adicional?</strong></p>" +
                "        <p>Ao decorrer da execução inicial dos serviços, nossos mecânicos identificaram problemas adicionais que não eram visíveis na inspeção " +
                "        preliminar. Estes problemas, se não resolvidos, podem:</p>" +
                "        <ul style=\"margin: 10px 0; padding-left: 20px;\">" +
                "          <li>Afetar a segurança do veículo</li>" +
                "          <li>Causar maiores danos a longo prazo</li>" +
                "          <li>Impedir o funcionamento adequado de sistemas importantes</li>" +
                "        </ul>" +
                "        <p><em>É uma situação comum em diagnósticos completos - quanto mais detalhado, mais problemas podem aparecer.</em></p>" +
                "      </div>" +
                "" +
                "      <div class=\"action-box\">" +
                "        <p><strong>✓ O Que Fazer Agora?</strong></p>" +
                "        <p>Um novo orçamento foi preparado para resolver estes problemas. Este documento está em <strong>anexo</strong> para sua análise.</p>" +
                "        <ul style=\"margin: 10px 0; padding-left: 20px;\">" +
                "          <li>✓ Descrição completa dos serviços necessários</li>" +
                "          <li>✓ Peças e insumos que serão utilizados</li>" +
                "          <li>✓ Prazo estimado para conclusão</li>" +
                "          <li>✓ Valor total dos serviços adicionais</li>" +
                "        </ul>" +
                "        <p style=\"margin-top: 15px; margin-bottom: 0;\"><strong>Validade:</strong> 30 dias</p>" +
                "      </div>" +
                "" +
                "      <div class=\"contact-box\">" +
                "        <p><strong>📞 Dúvidas? Converse com Nosso Mecânico</strong></p>" +
                "        <p>Recomendamos <strong>entrar em contato com o mecânico responsável</strong> para:</p>" +
                "        <ul style=\"margin: 10px 0; padding-left: 20px;\">" +
                "          <li>Esclarecer detalhes sobre os problemas encontrados</li>" +
                "          <li>Entender a importância de cada serviço</li>" +
                "          <li>Discutir opções e alternativas</li>" +
                "          <li>Responder qualquer dúvida técnica</li>" +
                "        </ul>" +
                "        <p style=\"margin-top: 15px; margin-bottom: 0;\"><strong>Equipe responsável está disponível:</strong></p>" +
                "        <p style=\"margin: 5px 0;\">📞 (11) 3000-0000 | 📧 mecanico@oficina.com.br</p>" +
                "      </div>" +
                "" +
                "      <p style=\"text-align: center; margin: 30px 0;\">" +
                "        <a href=\"" + linkAprovar + "\" class=\"cta-button\">✓ APROVAR ORÇAMENTO ADICIONAL</a>" +
                "      </p>" +
                "" +
                "      <p style=\"text-align: center;\">" +
                "        <a href=\"" + linkRejeitar + "\" style=\"color: #e74c3c; text-decoration: none; font-weight: bold;\">✗ Rejeitar e Conversar com o Mecânico</a>" +
                "      </p>" +
                "" +
                "      <p style=\"font-size: 12px; color: #7f8c8d; text-align: center;\">" +
                "        <small>Ou copie e cole um dos links abaixo no seu navegador:<br>Aprovar: " + linkAprovar + "<br>Rejeitar: " + linkRejeitar + "</small>" +
                "      </p>" +
                "    </div>" +
                "    <div class=\"footer\">" +
                "      <p><strong>Oficina Mecânica Premium</strong></p>" +
                "      <p>Telefone: (11) 3000-0000 | Email: contato@oficina.com.br</p>" +
                "      <p style=\"margin-top: 15px; color: #95a5a6;\">" +
                "        ✓ Garantia de qualidade em todos os serviços<br>" +
                "        ✓ Transparência total sobre custos e prazos<br>" +
                "        ✓ Atendimento especializado" +
                "      </p>" +
                "      <p style=\"margin-top: 15px; color: #95a5a6;\">Este é um e-mail automático. Para dúvidas, contate o mecânico responsável.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    private String montarTemplateEmailOrcamento(OrdemServico os, OsOrcamento orcamento, String linkAprovar,
                                                 String linkRejeitar, Cliente cliente, Veiculo veiculo) {
        return "<!DOCTYPE html>" +
                "<html lang=\"pt-BR\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }" +
                "    .content { background-color: #ecf0f1; padding: 20px; }" +
                "    .info-box { background-color: #fff; padding: 15px; margin: 10px 0; border-left: 4px solid #3498db; }" +
                "    .button { display: inline-block; padding: 12px 30px; background-color: #27ae60; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
                "    .button-reject { background-color: #e74c3c; }" +
                "    .footer { font-size: 12px; color: #7f8c8d; text-align: center; margin-top: 20px; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class=\"container\">" +
                "    <div class=\"header\">" +
                "      <h1>Seu Orçamento Está Pronto!</h1>" +
                "    </div>" +
                "    <div class=\"content\">" +
                "      <p>Olá <strong>" + cliente.getNome() + "</strong>,</p>" +
                "      <p>Temos o prazer de informar que <strong>o diagnóstico do seu veículo foi concluído</strong> e seu orçamento está pronto para análise e aprovação.</p>" +
                "      <div class=\"info-box\">" +
                "        <p><strong>Número da OS:</strong> #" + os.getId() + "</p>" +
                "        <p><strong>Veículo:</strong> " + veiculo.getModelo() + " - Placa: " + veiculo.getPlaca() + "</p>" +
                "        <p><strong>Valor Total:</strong> <strong style=\"color: #27ae60; font-size: 18px;\">R$ " + String.format("%.2f", orcamento.getValorTotal()) + "</strong></p>" +
                "        <p><strong>Prazo Estimado:</strong> " + (orcamento.getPrazoEstipulado() != null ? orcamento.getPrazoEstipulado() : "A ser confirmado") + "</p>" +
                "      </div>" +
                "      <p>O arquivo em anexo <strong>orcamento_os_" + os.getId() + ".pdf</strong> contém todos os detalhes:</p>" +
                "      <ul>" +
                "        <li>Serviços inclusos na manutenção</li>" +
                "        <li>Peças e insumos necessários</li>" +
                "        <li>Custos detalhados</li>" +
                "        <li>Prazo de execução</li>" +
                "      </ul>" +
                "      <p style=\"text-align: center; margin: 30px 0;\">" +
                "        <a href=\"" + linkAprovar + "\" class=\"button\">✓ APROVAR ORÇAMENTO</a>" +
                "      </p>" +
                "      <p style=\"text-align: center;\">" +
                "        <a href=\"" + linkRejeitar + "\" class=\"button button-reject\">✗ REJEITAR ORÇAMENTO</a>" +
                "      </p>" +
                "      <p style=\"font-size: 12px; color: #95a5a6; text-align: center;\"><small>Ou copie e cole um dos links abaixo no seu navegador:<br>Aprovar: " + linkAprovar + "<br>Rejeitar: " + linkRejeitar + "</small></p>" +
                "    </div>" +
                "    <div class=\"footer\">" +
                "      <p><strong>Oficina Mecânica Premium</strong></p>" +
                "      <p>Telefone: (11) 3000-0000 | Email: contato@oficina.com.br</p>" +
                "      <p style=\"margin-top: 15px; color: #95a5a6;\">Este é um e-mail automático. Por favor, não responda direto neste endereço.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    private String montarTemplateEmailOrcamentoAprovado(OrdemServico os, OsOrcamento orcamento,
                                                          Cliente cliente, Veiculo veiculo) {
        return "<!DOCTYPE html>" +
                "<html lang=\"pt-BR\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #27ae60; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }" +
                "    .content { background-color: #ecf0f1; padding: 20px; border-radius: 0 0 5px 5px; }" +
                "    .info-box { background-color: #fff; padding: 15px; border-left: 4px solid #27ae60; margin: 15px 0; }" +
                "    .info-box strong { color: #2c3e50; }" +
                "    .footer { margin-top: 20px; padding-top: 20px; border-top: 1px solid #bdc3c7; font-size: 12px; color: #7f8c8d; text-align: center; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class=\"container\">" +
                "    <div class=\"header\">" +
                "      <h1>✓ Orçamento Aprovado!</h1>" +
                "    </div>" +
                "    <div class=\"content\">" +
                "      <p>Olá <strong>" + cliente.getNome() + "</strong>,</p>" +
                "      <p>Confirmamos o recebimento da sua aprovação. Nossa equipe já foi notificada e os trabalhos no seu veículo serão iniciados em breve.</p>" +
                "      <div class=\"info-box\">" +
                "        <p><strong>Número da Ordem de Serviço:</strong> " + os.getId() + "</p>" +
                "        <p><strong>Veículo:</strong> " + veiculo.getModelo() + " - Placa: " + veiculo.getPlaca() + "</p>" +
                "        <p><strong>Valor Aprovado:</strong> <strong style=\"color: #27ae60; font-size: 18px;\">R$ " + String.format("%.2f", orcamento.getValorTotal()) + "</strong></p>" +
                "      </div>" +
                "      <p>Você receberá uma nova notificação assim que houver atualização no status do serviço.</p>" +
                "    </div>" +
                "    <div class=\"footer\">" +
                "      <p>Oficina Mecânica Premium<br>" +
                "      Telefone: (11) 3000-0000<br>" +
                "      E-mail: contato@oficina.com.br</p>" +
                "      <p style=\"margin-top: 15px; color: #95a5a6;\">Este é um e-mail automático. Por favor, não responda.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    private String montarTemplateEmailOrcamentoRejeitado(OrdemServico os, OsOrcamento orcamento,
                                                           Cliente cliente, Veiculo veiculo) {
        return "<!DOCTYPE html>" +
                "<html lang=\"pt-BR\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #e74c3c; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }" +
                "    .content { background-color: #ecf0f1; padding: 20px; border-radius: 0 0 5px 5px; }" +
                "    .info-box { background-color: #fff; padding: 15px; border-left: 4px solid #e74c3c; margin: 15px 0; }" +
                "    .info-box strong { color: #2c3e50; }" +
                "    .contact-box { background-color: #e3f2fd; border-left: 4px solid #2196f3; padding: 15px; margin: 15px 0; }" +
                "    .contact-box strong { color: #1565c0; }" +
                "    .footer { margin-top: 20px; padding-top: 20px; border-top: 1px solid #bdc3c7; font-size: 12px; color: #7f8c8d; text-align: center; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class=\"container\">" +
                "    <div class=\"header\">" +
                "      <h1>Orçamento Rejeitado</h1>" +
                "    </div>" +
                "    <div class=\"content\">" +
                "      <p>Olá <strong>" + cliente.getNome() + "</strong>,</p>" +
                "      <p>Registramos a rejeição do orçamento abaixo. Nenhum serviço adicional será executado sem sua aprovação.</p>" +
                "      <div class=\"info-box\">" +
                "        <p><strong>Número da Ordem de Serviço:</strong> " + os.getId() + "</p>" +
                "        <p><strong>Veículo:</strong> " + veiculo.getModelo() + " - Placa: " + veiculo.getPlaca() + "</p>" +
                "        <p><strong>Valor Rejeitado:</strong> R$ " + String.format("%.2f", orcamento.getValorTotal()) + "</p>" +
                "      </div>" +
                "      <div class=\"contact-box\">" +
                "        <p><strong>Quer discutir outras opções?</strong></p>" +
                "        <p>Entre em contato com nossa equipe para conversarmos sobre alternativas para o seu veículo.</p>" +
                "        <p style=\"margin: 5px 0;\">📞 (11) 3000-0000 | 📧 contato@oficina.com.br</p>" +
                "      </div>" +
                "    </div>" +
                "    <div class=\"footer\">" +
                "      <p>Oficina Mecânica Premium</p>" +
                "      <p style=\"margin-top: 15px; color: #95a5a6;\">Este é um e-mail automático. Por favor, não responda.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
}
