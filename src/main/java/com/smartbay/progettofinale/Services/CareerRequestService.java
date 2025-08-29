package com.smartbay.progettofinale.Services;

import com.smartbay.progettofinale.Models.CareerRequest;
import com.smartbay.progettofinale.Models.User;

/**
 * Interfaccia del servizio per la gestione delle richieste di carriera.
 * <p>
 * Definisce i metodi per la logica di business relativa all'invio,
 * alla verifica e all'accettazione delle richieste di ruolo.
 */
public interface CareerRequestService {

    /**
     * Verifica se un ruolo richiesto è già stato assegnato a un utente.
     *
     * @param user          L'utente che ha inviato la richiesta.
     * @param careerRequest La richiesta di carriera.
     * @return True se il ruolo è già assegnato, false altrimenti.
     */
    boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest);

    /**
     * Salva una nuova richiesta di carriera nel database.
     *
     * @param careerRequest La richiesta di carriera da salvare.
     * @param user          L'utente che ha inviato la richiesta.
     */
    void save(CareerRequest careerRequest, User user);

    /**
     * Accetta una richiesta di carriera e assegna il ruolo all'utente.
     *
     * @param requestId L'ID della richiesta da accettare.
     */
    void careerAccept(Long requestId);

    /**
     * Trova una richiesta di carriera in base al suo ID.
     *
     * @param id L'ID della richiesta.
     * @return L'entità CareerRequest corrispondente.
     */
    CareerRequest find(Long id);

}