package com.mailer.mailer_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController{
    @Autowired
    private EmailService emailService;

    @GetMapping("") //Definir URI
    public String sendMail(
        @RequestParam("para") String mailTo,
        @RequestParam(value = "assunto") String subject,
        @RequestParam(value = "corpo") String body
    ){
        return emailService.sendMail(mailTo, subject, body);
    }
}