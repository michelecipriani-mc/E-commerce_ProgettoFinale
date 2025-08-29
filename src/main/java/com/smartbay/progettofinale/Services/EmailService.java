package com.smartbay.progettofinale.Services;

/**
 * Interfaccia per il servizio di invio email.
 * <p>
 * Definisce i metodi per l'invio di notifiche via email.
 */
public interface EmailService {

    /**
     * Invia un'email di testo semplice a un destinatario.
     *
     * @param to      L'indirizzo email del destinatario.
     * @param subject L'oggetto dell'email.
     * @param text    Il corpo del testo dell'email.
     */
    void sendSimpleEmail(String to, String subject, String text);
}
