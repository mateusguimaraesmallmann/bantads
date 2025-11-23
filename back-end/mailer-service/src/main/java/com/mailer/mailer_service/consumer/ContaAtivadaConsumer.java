package com.mailer.mailer_service.consumer;

import com.mailer.mailer_service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ContaAtivadaConsumer {

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = "conta-ativada")
    public void enviarEmailAtivacao(Map<String, String> payload) {
        String para = payload.get("email");
        String assunto = payload.get("assunto");
        String mensagem = payload.get("mensagem");

        emailService.sendMail(para, assunto, mensagem);
        System.out.println("E-mail de ativação enviado para: " + para);
    }
}