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

@ControllerAdvice
public class CarrelloModelAdvice {

    @Autowired
    private CarrelloService carrelloService;

    @Autowired
    private SecurityService securityService;

    @ModelAttribute("carrello")
    public CarrelloDTO getCarrelloGlobale(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Long idUtente = securityService.getActiveUserId();
            return carrelloService.getCarrelloDTOFromUtente(idUtente);
        }
        return new CarrelloDTO(); // carrello vuoto per utenti non autenticati
    }

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
