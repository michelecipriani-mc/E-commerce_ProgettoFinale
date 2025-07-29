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



  @PostMapping("/add/{id}")
  public ResponseEntity<String> aggiungiUnArticolo(@PathVariable("id") Long idArticolo) {
    return aggiornaQuantitaCarrello(idArticolo, +1);
  }

  @PostMapping("/update")
  public ResponseEntity<String> aggiornaQuantitaCarrello(
      @RequestParam("id") Long idArticolo,
      @RequestParam("changeInQuantity") int changeInQuantity
    ) {

    Long idUtente = securityService.getActiveUserId();

    carrelloService.aggiornaQuantitaArticolo(idUtente, idArticolo,
        changeInQuantity);

    return ResponseEntity.ok("Carrello Aggiornato");
  }

  /**
   * Rimuove un articolo dal carrello dell'utente corrente. Questo endpoint è usato dal pulsante del
   * cestino in Thymeleaf.
   *
   * @param idArticolo l'ID dell'articolo da rimuovere
   * @return una redirezione alla pagina del carrello aggiornata
   */
  @PostMapping("/remove")
  public String rimuoviArticoloDaCarrello(@RequestParam("id") Long idArticolo) {
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
