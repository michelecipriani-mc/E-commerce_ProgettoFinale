package com.smartbay.progettofinale.Controllers;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
  public String mostraCarrello(Model model) {
    Long idUtente = securityService.getActiveUserId();
    CarrelloDTO carrello = carrelloService.getCarrelloDTOFromUtente(idUtente);
    BigDecimal totale = carrelloService.getPrezzoTotaleCarrello(idUtente);

    model.addAttribute("carrello", carrello);
    model.addAttribute("totale", totale);

    return "user/carrello"; // Corretto: templates/user/carrello.html
  }

  // Aggiorna quantità articolo nel carrello
  @PostMapping("/update")
  public String aggiornaQuantitaArticolo(@ModelAttribute AggiornaArticoloCarrelloRequest request) {
    Long idUtente = securityService.getActiveUserId();
    carrelloService.aggiornaQuantitaArticolo(idUtente, request.getIdArticolo(), request.getCambiamentoQuantita());
    return "redirect:/carrello";
  }

  // Rimuove un articolo dal carrello
  @PostMapping("/remove/{idArticolo}")
  public String rimuoviArticoloDaCarrello(@PathVariable Long idArticolo) {
    Long idUtente = securityService.getActiveUserId();
    carrelloService.rimuoviArticolo(idUtente, idArticolo);
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
