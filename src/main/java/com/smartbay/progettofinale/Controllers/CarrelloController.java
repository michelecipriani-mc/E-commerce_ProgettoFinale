package com.smartbay.progettofinale.Controllers;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.smartbay.progettofinale.DTO.AggiornaArticoloCarrelloRequest;
import com.smartbay.progettofinale.DTO.CarrelloDTO;
import com.smartbay.progettofinale.DTO.AggiornaArticoloCarrelloRequest;
import com.smartbay.progettofinale.Security.SecurityService;
import com.smartbay.progettofinale.Services.CarrelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/carrello")
public class CarrelloController {

  private final SecurityService securityService;
  private final CarrelloService carrelloService;

  public CarrelloController(SecurityService securityService, CarrelloService carrelloService) {
    this.securityService = securityService;
    this.carrelloService = carrelloService;
  }

  // Mostra la pagina del carrello
  @GetMapping
  public String getCarrello(Model model) {
    Long idUtente = securityService.getActiveUserId();

    // Ottiene il carrello DTO relativo all'utente attivo
    CarrelloDTO carrello = carrelloService.getCarrelloDTOFromUtente(idUtente);

    // Calcola il prezzo totale del carrello
    BigDecimal totale = carrelloService.getPrezzoTotaleCarrello(idUtente);

    // Aggiunge il carrello e il totale al model per Thymeleaf
    model.addAttribute("carrello", carrello);
    model.addAttribute("totale", totale);

    // Ritorna il template carrello.html in templates/user/
    return "user/carrello";
  }


  /**
   * Metodo per aggiungere un articolo al carrello dalla pagina articolo.
   * 
   * Questa azione viene invocata quando l'utente aggiunge un nuovo prodotto dalla pagina di
   * dettaglio articolo.
   * 
   * Dopo l'aggiunta, si effettua il redirect alla pagina articolo per mantenere il contesto e
   * permettere all'utente di continuare la navigazione da lì.
   * 
   * @param idArticolo ID dell'articolo da aggiungere
   * @param quantita Quantità da aggiungere
   * @return redirect alla pagina articolo
   */
  @PostMapping("/add")
  public String aggiungiAlCarrelloDaArticolo(
      @ModelAttribute AggiornaArticoloCarrelloRequest request,
      RedirectAttributes redirectAttributes) {

    Long idUtente = securityService.getActiveUserId();
    carrelloService.aggiornaQuantitaArticolo(idUtente, request.getIdArticolo(),
        request.getCambiamentoQuantita());
    redirectAttributes.addFlashAttribute("aggiuntaSuccess", true);

    // Redirect per rimanere nella pagina dettaglio articolo dopo l'aggiunta
    return "redirect:/articles/detail/" + request.getIdArticolo() + "#dettaglio-articolo";
  }

  /**
   * Metodo per aggiornare la quantità di un articolo nel carrello, dalla pagina carrello stessa.
   * 
   * Questa azione viene invocata quando l'utente modifica la quantità di un articolo direttamente
   * nella pagina carrello.
   * 
   * Dopo l'aggiornamento, si effettua il redirect alla pagina carrello per permettere di
   * visualizzare subito il carrello aggiornato senza cambiare contesto.
   * 
   * @param idArticolo ID dell'articolo da aggiornare
   * @param cambiamentoQuantita Nuova quantità impostata dall'utente
   * @return redirect alla pagina carrello
   */
  @PostMapping("/update")
  public String aggiornaQuantitaCarrello(@ModelAttribute AggiornaArticoloCarrelloRequest request,
      RedirectAttributes redirectAttributes) {

    Long idUtente = securityService.getActiveUserId();
    carrelloService.aggiornaQuantitaArticolo(idUtente, request.getIdArticolo(),
        request.getCambiamentoQuantita());
    redirectAttributes.addFlashAttribute("aggiuntaSuccess", true);

    // Redirect alla pagina carrello dopo l'aggiornamento
    return "redirect:/carrello";
  }

  /**
   * Rimuove un articolo dal carrello dell'utente corrente. Questo endpoint è usato dal pulsante del
   * cestino in Thymeleaf.
   *
   * @param idArticolo l'ID dell'articolo da rimuovere
   * @return una redirezione alla pagina del carrello aggiornata
   */
  @PostMapping("/remove/{idArticolo}")
  public String rimuoviArticoloDaCarrello(@PathVariable Long idArticolo) {
    Long idUtente = securityService.getActiveUserId();
    carrelloService.rimuoviArticolo(idUtente, idArticolo);

    // Dopo la rimozione, redireziona alla pagina del carrello
    return "redirect:/carrello";
  }

  // Svuota l'intero carrello
  @PostMapping("/clear")
  public String svuotaCarrello() {
    Long idUtente = securityService.getActiveUserId();
    carrelloService.svuotaCarrello(idUtente);
    return "redirect:/carrello";
  }
}
