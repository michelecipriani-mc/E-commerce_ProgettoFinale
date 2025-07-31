package com.smartbay.progettofinale.Services;

/**
 * Interfaccia per il servizio di invio email.
 * Definisce un metodo per inviare email semplici con destinatario, oggetto e
 * testo.
 */

public interface EmailService {
    /**
     * Invia una email semplice.
     *
     * @param to      indirizzo email del destinatario
     * @param subject oggetto della email
     * @param text    contenuto testuale della email
     */
    void sendSimpleEmail(String to, String subject, String text);
}
