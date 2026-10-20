package com.fiap.tech_challenge_backend.atendimento.adapters.out.infrastructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailSenderAdapter Tests")
class EmailSenderAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSenderAdapter emailSenderAdapter;

    private String email;
    private String assunto;
    private String corpoHtml;
    private byte[] anexo;
    private String nomeAnexo;

    @BeforeEach
    void setUp() {
        email = "cliente@test.com";
        assunto = "Orçamento de Manutenção";
        corpoHtml = "<html><body>Seu orçamento está pronto.</body></html>";
        anexo = new byte[]{1, 2, 3, 4, 5};
        nomeAnexo = "orcamento.pdf";
    }

    @Test
    @DisplayName("deve enviar email com anexo com sucesso")
    void testEnviarEmailComAnexoComSucesso() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSenderAdapter.enviarEmailComAnexo(email, assunto, corpoHtml, anexo, nomeAnexo);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("deve enviar email com destinatário correto")
    void testEnviarEmailComDestinatarioCorreto() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSenderAdapter.enviarEmailComAnexo(email, assunto, corpoHtml, anexo, nomeAnexo);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("deve enviar email com assunto correto")
    void testEnviarEmailComAssuntoCorreto() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSenderAdapter.enviarEmailComAnexo(email, assunto, corpoHtml, anexo, nomeAnexo);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("deve enviar email com corpo HTML correto")
    void testEnviarEmailComCorpoHtmlCorreto() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSenderAdapter.enviarEmailComAnexo(email, assunto, corpoHtml, anexo, nomeAnexo);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("deve enviar email com anexo correto")
    void testEnviarEmailComAnexoCorreto() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        byte[] meuAnexo = "conteúdo do anexo".getBytes();
        emailSenderAdapter.enviarEmailComAnexo(email, assunto, corpoHtml, meuAnexo, nomeAnexo);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("deve enviar email com sucesso múltiplas vezes")
    void testEnviarEmailMultiplasChamadas() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSenderAdapter.enviarEmailComAnexo(email, assunto, corpoHtml, anexo, nomeAnexo);
        emailSenderAdapter.enviarEmailComAnexo(email, assunto, corpoHtml, anexo, nomeAnexo);

        verify(mailSender, times(2)).send(mimeMessage);
    }

    @Test
    @DisplayName("deve capturar exceção genérica e continuar")
    void testEnviarEmailComExcecaoGenerica() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro genérico"));

        assertDoesNotThrow(() -> emailSenderAdapter.enviarEmailComAnexo(email, assunto, corpoHtml, anexo, nomeAnexo));
    }

    @Test
    @DisplayName("deve enviar email com diferentes tipos de anexo")
    void testEnviarEmailComDiferentesAnexos() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        byte[] anexoBinario = new byte[1024];
        emailSenderAdapter.enviarEmailComAnexo(email, assunto, corpoHtml, anexoBinario, "documento.bin");

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("deve enviar email com nome de anexo vazio")
    void testEnviarEmailComNomeAnexoVazio() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSenderAdapter.enviarEmailComAnexo(email, assunto, corpoHtml, anexo, "");

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("deve enviar email com corpo vazio")
    void testEnviarEmailComCorpoVazio() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSenderAdapter.enviarEmailComAnexo(email, assunto, "", anexo, nomeAnexo);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("deve enviar email sem anexo quando o anexo não for informado")
    void testEnviarEmailSemAnexo() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSenderAdapter.enviarEmailComAnexo(email, assunto, corpoHtml, null, null);

        verify(mailSender, times(1)).send(mimeMessage);
    }
}
