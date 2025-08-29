package com.smartbay.progettofinale.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ExceptionHandlingController {
    //gestione del errore 403 accesso negato
    @GetMapping("/error/{number}")
    public String accessDenied(@PathVariable int number, Model model) {
        //nel caso in cui ho l'errore
        if (number == 403) {
            // redireziona alla pagina notAuthorized
            return "redirect:/?notAuthorized";
        }
        //altrimenti redireziona alla pagina home
        return "redirect:/";
    }
    
}