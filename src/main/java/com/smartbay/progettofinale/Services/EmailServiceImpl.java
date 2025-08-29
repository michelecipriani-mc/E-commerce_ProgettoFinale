package com.smartbay.progettofinale.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementazione del servizio di invio email.
 * <p>
 * Questa classe gestisce l'invio effettivo delle email utilizzando
 * la configurazione di Spring per la gestione della posta.
 */
@Service
public class EmailServiceImpl implements EmailService{
    
    /** Componente per l'invio delle email */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Invia un'email di testo semplice in modo asincrono.
     * <p>
     * L'invio asincrono impedisce il blocco del thread principale
     * dell'applicazione durante l'operazione.
     *
     * @param to      L'indirizzo email del destinatario.
     * @param subject L'oggetto dell'email.
     * @param text    Il corpo del testo dell'email.
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("admin@smartbay.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }


}
