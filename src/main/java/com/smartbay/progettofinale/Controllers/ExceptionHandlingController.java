package com.smartbay.progettofinale.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller per la gestione personalizzata delle eccezioni.
 * <p>
 * Mappa le richieste relative agli errori per gestire il reindirizzamento
 * in base al codice di errore.
 */
@Controller
public class ExceptionHandlingController {

    /**
     * Gestisce i reindirizzamenti per gli errori di accesso negato.
     * <p>
     * Se il codice è 403, reindirizza alla home page con un flag
     * che indica un'autorizzazione negata.
     *
     * @param number Il codice di errore HTTP.
     * @param model  Oggetto Model per la vista.
     * @return Stringa di reindirizzamento.
     */
    @GetMapping("/error/{number}")
    public String accessDenied(@PathVariable int number, Model model) {

        // Verifica se il codice di errore è 403 (Accesso Negato)
        if (number == 403) {
            return "redirect:/?notAuthorized";
        }

        // Per altri errori, reindirizza alla home page
        return "redirect:/";
    }
    
}