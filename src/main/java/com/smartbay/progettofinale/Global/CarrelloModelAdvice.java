package com.smartbay.progettofinale.Global;

import com.smartbay.progettofinale.DTO.ArticoloQuantitaDTO;
import com.smartbay.progettofinale.DTO.CarrelloDTO;
import com.smartbay.progettofinale.Security.SecurityService;
import com.smartbay.progettofinale.Services.CarrelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
/** Classe annotata con @ControllerAdvice: 
    * significa che fornisce "consigli" o configurazioni globali a tutti i controller.
    * In pratica, i metodi annotati con @ModelAttribute qui dentro rendono disponibili dei dati a livello globale (per tutte le view)**/
@ControllerAdvice
public class CarrelloModelAdvice {

    // Iniezione del servizio CarrelloService, che gestisce la logica del carrello
    @Autowired
    private CarrelloService carrelloService;

    // Iniezione del servizio SecurityService, che gestisce informazioni sull'utente autenticato
    @Autowired
    private SecurityService securityService;

    //metodo per rendere visibile globalmente l'attributo carrello
    @ModelAttribute("carrello")
    public CarrelloDTO getCarrelloGlobale(Authentication authentication) {
        // Se l'utente è autenticato
        if (authentication != null && authentication.isAuthenticated()) {
            // Otteniamo l'id dell'utente loggato tramite il securityService
            Long idUtente = securityService.getActiveUserId();
            // Recuperiamo il carrello associato all'utente
            return carrelloService.getCarrelloDTOFromUtente(idUtente);
        }
        // carrello vuoto per utenti non autenticati
        return new CarrelloDTO(); 
    }

    /* aggiungiamo un attributo di quantità totale per il carrello e la 
    rendiamo visibile globalmente, al fine di avere un badge direttamente 
    sull'icona del carrello che ci permette di avere sempre visibile la quantità di articoli presenti nel carrello */
    @ModelAttribute("quantitaTotaleCarrello")
    public int getQuantitaTotaleGlobale(Authentication authentication) {
        // Se l'utente è autenticato
        if (authentication != null && authentication.isAuthenticated()) {
            // Otteniamo l'id dell'utente loggato
            Long idUtente = securityService.getActiveUserId();
            // Recuperiamo tutti gli articoli presenti nel carrello dell'utente
            List<ArticoloQuantitaDTO> articoli = carrelloService.getCarrelloDTOFromUtente(idUtente).getArticoli();
            // Sommiamo le quantità di tutti gli articoli per ottenere il totale
            return articoli.stream().mapToInt(ArticoloQuantitaDTO::getQuantita).sum();
        }
        // Se non è autenticato, il carrello è vuoto quindi ritorniamo 0
        return 0;
    }
}
