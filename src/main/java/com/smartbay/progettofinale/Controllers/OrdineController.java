package com.smartbay.progettofinale.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import com.smartbay.progettofinale.DTO.OrdineDTO;
import com.smartbay.progettofinale.Security.SecurityService;
import com.smartbay.progettofinale.Services.OrdineService;

/**
 * Controller REST per la gestione degli ordini.
 *
 * Funzionalità:
 * - Confermare un ordine al termine del processo di acquisto.
 * - Recuperare la lista degli ordini effettuati dall'utente autenticato.
 *
 * Usa OrdineService per eseguire la logica applicativa.
 */

@RestController
@RequestMapping("/ordini")
public class OrdineController {

    private final OrdineService ordineService;

    // Costruttore che riceve il servizio per la gestione degli ordini
    public OrdineController(OrdineService ordineService, SecurityService securityService) {
        this.ordineService = ordineService;
    }

    /**
     * Conferma un nuovo ordine da parte dell'utente corrente.
     *
     * Se la creazione dell’ordine ha successo, mostra un messaggio di conferma.
     * In caso di errore (es. carrello vuoto), mostra un messaggio di avviso.
     */
    @PostMapping("/conferma")
    public ModelAndView confermaOrdine(RedirectAttributes redirectAttributes) {
        try {
            ordineService.creaOrdine();// logica gestita nel service
            redirectAttributes.addFlashAttribute("cartSuccess", "Ordine effettuato con successo.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("cartWarning", ex.getMessage());
        }
        return new ModelAndView("redirect:/carrello");
    }

    /**
     * Restituisce tutti gli ordini effettuati dall'utente autenticato.
     */
    @GetMapping("")
    public ResponseEntity<List<OrdineDTO>> getOrdiniUtente() {
        return ResponseEntity.ok(ordineService.getOrdiniUtente());
    }
}