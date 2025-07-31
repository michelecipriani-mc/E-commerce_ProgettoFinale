package com.smartbay.progettofinale.Services;

import com.smartbay.progettofinale.Models.CareerRequest;
import com.smartbay.progettofinale.Models.User;

/**
 * Interfaccia del servizio per la gestione delle richieste di carriera
 * (CareerRequest).
 * Definisce i metodi fondamentali per controllare e gestire le richieste di
 * ruolo degli utenti.
 */
public interface CareerRequestService {
    /**
     * Controlla se un determinato ruolo è già stato assegnato all'utente.
     * 
     * @param user          l'utente da verificare
     * @param careerRequest la richiesta di carriera contenente il ruolo da
     *                      controllare
     * @return true se il ruolo è già assegnato all'utente, false altrimenti
     */
    boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest);

    /**
     * Salva una nuova richiesta di carriera associata a un utente.
     * 
     * @param careerRequest la richiesta di carriera da salvare
     * @param user          l'utente che ha fatto la richiesta
     */
    void save(CareerRequest careerRequest, User user);

    /**
     * Accetta una richiesta di carriera identificata dall'id.
     * Questo metodo può aggiornare lo stato della richiesta e assegnare il ruolo
     * all'utente.
     * 
     * @param requestId l'id della richiesta di carriera da accettare
     */
    void careerAccept(Long requestId);

    /**
     * Trova una richiesta di carriera dato il suo id.
     * 
     * @param id l'id della richiesta di carriera da recuperare
     * @return la richiesta di carriera corrispondente, oppure null o eccezione se
     *         non trovata
     */
    CareerRequest find(Long id);

}