package com.smartbay.progettofinale.Controllers;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.smartbay.progettofinale.DTO.AggiornaArticoloCarrelloRequest;
import com.smartbay.progettofinale.DTO.CarrelloDTO;
import com.smartbay.progettofinale.Models.Article;
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


    if (carrelloService.CarrelloIsModified(idUtente)) {
      model.addAttribute("warning", "Warning: one or more items in your cart have been removed.");
      carrelloService.ResetCarrelloModifiedFlag(idUtente);
    }

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
  public ResponseEntity<String> aggiornaQuantitaCarrello(@RequestParam("id") Long idArticolo,
      @RequestParam("changeInQuantity") int changeInQuantity) {

    Long idUtente = securityService.getActiveUserId();

    carrelloService.aggiornaQuantitaArticolo(idUtente, idArticolo, changeInQuantity);

    return ResponseEntity.ok("Carrello Aggiornato");
  }

  /**
   * Metodo per aggiungere un articolo dalla lista di tutti gli articoli. 
   * Accoglie request HTMX.
   */
  @PostMapping("/htmx-add/{id}")
  @ResponseBody
  public String aggiungiUnoDaArticoli(@PathVariable("id") Long idArticolo) {
    Long idUtente = securityService.getActiveUserId();
    String notificationHtml = "";

    try {
      carrelloService.aggiornaQuantitaArticolo(idUtente, idArticolo, +1);

      // Success HTML: add classes and the remove-me attribute for a 3-second life
      notificationHtml =
          "<div class='alert alert-success toast-notification toast-success' remove-me='3s'>"
              + "<p>Item added to cart successfully!</p>" + "</div>";

    } catch (Exception ex) {
      // Error HTML: add classes and the remove-me attribute for a 5-second life
      notificationHtml =
          "<div class='alert alert-warning toast-notification toast-error' remove-me='5s'>"
              + "<p>Error: " + ex.getMessage() + "</p>" + "</div>";
    }

    return notificationHtml;
  }

  // aggiunta articoli da menu a tendina in pagina details
  @PostMapping("/dropdownadd/{id}")
  public String addArticleFromDropdown(@PathVariable("id") Long idArticolo,
      @RequestParam("quantity") int quantity, RedirectAttributes redirectAttributes) {

    Long idUtente = securityService.getActiveUserId();

    try {
      carrelloService.aggiornaQuantitaArticolo(idUtente, idArticolo, quantity);
      redirectAttributes.addFlashAttribute("cartSuccess", "Cart updated");

    } catch (RuntimeException ex) {
      redirectAttributes.addFlashAttribute("cartWarning", ex.getMessage());
    }

    // Redirect back to the article detail page using its ID
    // The flash attributes will be available on the redirected request.
    return "redirect:/articles/detail/" + idArticolo;
  }

  /**
   * Rimuove tutte le copie di un articolo dal carrello dell'utente corrente. Questo endpoint è
   * usato dal pulsante del cestino in Thymeleaf.
   *
   * @param idArticolo l'ID dell'articolo da rimuovere
   * @return una redirezione alla pagina del carrello aggiornata
   */
  @PostMapping("/removeall/{id}")
  public String rimuoviArticoloDaCarrello(@PathVariable("id") Long idArticolo,
  RedirectAttributes redirectAttributes) {
    Long idUtente = securityService.getActiveUserId();
    carrelloService.rimuoviArticolo(idUtente, idArticolo);
    redirectAttributes.addFlashAttribute("cartSuccess", "Cart Updated");

    // Dopo la rimozione, redireziona alla pagina del carrello
    return "redirect:/carrello";
  }

  /**
   * Rimuove un singolo articolo dal carrello dell'utente corrente. Questo endpoint è usato dal
   * pulsante (-) nella pagina carrello
   *
   * @param idArticolo l'ID dell'articolo da rimuovere
   * @return una redirezione alla pagina del carrello aggiornata
   */
  @PostMapping("/removeone/{id}")
  public String rimuoviUnoDaCarrello(@PathVariable("id") Long idArticolo,
  RedirectAttributes redirectAttributes) {
    Long idUtente = securityService.getActiveUserId();
    carrelloService.aggiornaQuantitaArticolo(idUtente, idArticolo, -1);
    redirectAttributes.addFlashAttribute("cartSuccess", "Cart Updated");

    // Dopo la rimozione, redireziona alla pagina del carrello
    return "redirect:/carrello";
  }

  /**
   * Aggiunge un singolo articolo dal carrello dell'utente corrente. Questo endpoint è usato dal
   * pulsante (+) nella pagina carrello
   *
   * @param idArticolo l'ID dell'articolo da rimuovere
   * @return una redirezione alla pagina del carrello aggiornata
   */
  @PostMapping("/addone/{id}")
  public String aggiungiUnoDaCarrello(@PathVariable("id") Long idArticolo, 
      RedirectAttributes redirectAttributes) {
    Long idUtente = securityService.getActiveUserId();
    redirectAttributes.addFlashAttribute("cartSuccess", "Cart Updated");

    try {
      carrelloService.aggiornaQuantitaArticolo(idUtente, idArticolo, +1);
    } catch (Exception ex) {
      redirectAttributes.addFlashAttribute("cartWarning", ex.getMessage());
    }

    // Dopo la rimozione, redireziona alla pagina del carrello
    return "redirect:/carrello";
  }

  // Svuota l'intero carrello
  @PostMapping("/clear")
  public String svuotaCarrello(RedirectAttributes redirectAttributes) {
    Long idUtente = securityService.getActiveUserId();
    carrelloService.svuotaCarrello(idUtente);
    redirectAttributes.addFlashAttribute("cartSuccess", "Cart Updated");

    return "redirect:/carrello";
  }
}
