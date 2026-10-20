package com.fiap.tech_challenge_backend.atendimento.application.ports.out;

public interface EmailSenderPort {

    void enviarEmailComAnexo(String para, String assunto, String corpoHtml, byte[] anexo, String nomeAnexo);

    default void enviarEmail(String para, String assunto, String corpoHtml) {
        enviarEmailComAnexo(para, assunto, corpoHtml, null, null);
    }
}
