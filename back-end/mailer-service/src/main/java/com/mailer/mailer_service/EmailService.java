package com.mailer.mailer_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService{
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public String sendMail(String receiver, String subject, String body){
        try{
            if (body == null) body = "Sem conteúdo";
            if (subject == null) subject = "Sem assunto";
            if (sender == null || sender.trim().isEmpty()) {
                System.err.println("ERRO: Remetente (spring.mail.username) não configurado!");
                return "Erro: Remetente não configurado";
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(receiver);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            return "E-mail enviado com sucesso!";
        } catch(Exception e){
            e.printStackTrace();
            return "Erro ao enviar e-mail: " + e.getMessage();
        }
    }
}