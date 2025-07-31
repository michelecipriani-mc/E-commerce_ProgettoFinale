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

/**
 * Classe annotata con @ControllerAdvice per aggiungere attributi comuni
 * ai modelli di tutte le view gestite dai controller dell'applicazione.
 * 
 * In particolare, fornisce dati relativi al carrello della spesa (shopping
 * cart):
 * - un oggetto CarrelloDTO che rappresenta il carrello corrente dell'utente
 * autenticato
 * - la quantità totale degli articoli presenti nel carrello
 * 
 * Se l'utente non è autenticato, restituisce un carrello vuoto e quantità
 * totale pari a zero.
 * 
 * Gli attributi sono resi disponibili in tutte le view tramite
 * l'annotazione @ModelAttribute.
 */

@ControllerAdvice
public class CarrelloModelAdvice {

    @Autowired
    private CarrelloService carrelloService;

    @Autowired
    private SecurityService securityService;

    /**
     * Metodo che ritorna il carrello corrente dell'utente autenticato.
     * Se l'utente non è autenticato, ritorna un carrello vuoto.
     *
     * @param authentication contesto di sicurezza con i dati di autenticazione
     *                       dell'utente
     * @return CarrelloDTO con gli articoli del carrello oppure carrello vuoto
     */

    @ModelAttribute("carrello")
    public CarrelloDTO getCarrelloGlobale(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Long idUtente = securityService.getActiveUserId();
            return carrelloService.getCarrelloDTOFromUtente(idUtente);
        }
        return new CarrelloDTO(); // carrello vuoto per utenti non autenticati
    }

    /**
     * Metodo che calcola la quantità totale degli articoli nel carrello dell'utente
     * autenticato.
     * Ritorna zero se l'utente non è autenticato.
     *
     * @param authentication contesto di sicurezza con i dati di autenticazione
     *                       dell'utente
     * @return quantità totale degli articoli presenti nel carrello
     */
    @ModelAttribute("quantitaTotaleCarrello")
    public int getQuantitaTotaleGlobale(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Long idUtente = securityService.getActiveUserId();
            List<ArticoloQuantitaDTO> articoli = carrelloService.getCarrelloDTOFromUtente(idUtente).getArticoli();
            return articoli.stream().mapToInt(ArticoloQuantitaDTO::getQuantita).sum();
        }
        return 0;
    }
}
