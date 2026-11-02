package com.fiap.tech_challenge_backend.atendimento.adapters.out.infrastructure;

import com.fiap.tech_challenge_backend.atendimento.application.ports.out.EmailSenderPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.MetricasNegocioPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.Duration;

@Component
public class EmailSenderAdapter implements EmailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderAdapter.class);

    private static final String INTEGRACAO = "email";

    private final JavaMailSender mailSender;
    private final MetricasNegocioPort metricas;

    public EmailSenderAdapter(JavaMailSender mailSender, MetricasNegocioPort metricas) {
        this.mailSender = mailSender;
        this.metricas = metricas;
    }

    @Override
    public void enviarEmailComAnexo(String para, String assunto, String corpoHtml, byte[] anexo, String nomeAnexo) {
        long inicio = System.nanoTime();
        boolean possuiAnexo = anexo != null && nomeAnexo != null && !nomeAnexo.isBlank();
        String operacao = possuiAnexo ? "com_anexo" : "sem_anexo";
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, possuiAnexo, "UTF-8");

            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true);
            if (possuiAnexo) {
                helper.addAttachment(nomeAnexo, () -> new java.io.ByteArrayInputStream(anexo));
            }

            mailSender.send(message);

            log.info("E-mail enviado com sucesso para: {} | Assunto: {} | Anexo: {}",
                    para, assunto, nomeAnexo);
            metricas.integracao(INTEGRACAO, operacao, true, duracaoDesde(inicio), null);
        } catch (MessagingException e) {
            log.error("Erro ao enviar e-mail para: {} | Assunto: {} | Erro: {}",
                    para, assunto, e.getMessage());
            log.warn("E-mail não pôde ser enviado, mas a operação continua. Verifique as credenciais de email no application.yml");
            metricas.integracao(INTEGRACAO, operacao, false, duracaoDesde(inicio), e.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar e-mail para: {} | Erro: {}",
                    para, e.getMessage());
            log.warn("E-mail não pôde ser enviado, mas a operação continua.");
            metricas.integracao(INTEGRACAO, operacao, false, duracaoDesde(inicio), e.getClass().getSimpleName());
        }
    }

    private Duration duracaoDesde(long inicio) {
        return Duration.ofNanos(System.nanoTime() - inicio);
    }
}
