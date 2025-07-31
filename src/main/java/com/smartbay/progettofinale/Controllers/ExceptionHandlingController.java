package com.smartbay.progettofinale.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller per la gestione degli errori HTTP personalizzati.
 * 
 * Attualmente gestisce:
 * - Errori 403 (Accesso non autorizzato): redireziona l'utente alla home page
 * con parametro "notAuthorized".
 * 
 * Altri errori vengono semplicemente reindirizzati alla home page.
 */

@Controller
public class ExceptionHandlingController {

    @GetMapping("/error/{number}")
    public String accessDenied(@PathVariable int number, Model model) {
        // Se l'errore è un 403 (accesso negato), aggiunge un parametro per mostrare un
        // messaggio
        if (number == 403) {
            return "redirect:/?notAuthorized";
        }
        // Per qualsiasi altro errore, redireziona semplicemente alla home
        return "redirect:/";
    }

}