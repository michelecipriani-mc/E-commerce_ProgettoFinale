package com.smartbay.progettofinale.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementazione del servizio di invio email semplice.
 * Utilizza JavaMailSender per inviare email in modo asincrono.
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Invia una email semplice in modo asincrono.
     *
     * @param to      indirizzo email del destinatario
     * @param subject oggetto della email
     * @param text    contenuto testuale della email
     */

    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("admin@smartbay.com");// Mittente fisso
        message.setTo(to);// Destinatario
        message.setSubject(subject);// Oggetto
        message.setText(text);// Testo
        mailSender.send(message);// Invio email
    }

}
