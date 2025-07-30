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
  @ResponseBody
  public String aggiungiUnArticolo(@PathVariable("id") Long idArticolo, Model model) {
    try {
      aggiornaQuantitaCarrello(idArticolo, +1);

      // Return di frammento Thymeleaf (notifica dissolvente) come conferma
      System.out.println("added to cart");
      return "<div>aggiunto</div>";
      //return toastMessage("✅ Aggiunto al carrello");

    } catch (Exception e) {
      System.out.println("too many articles");
      return "<div>non aggiunto</div>";
      //return toastError(e.getMessage());
    }

  }

  
 /* @PostMapping("/add")
  public String addToCart(@RequestParam Long id, Model model) {
    try {
      aggiornaQuantitaCarrello(id, +1);

      model.addAttribute("message", "Product added to cart successfully!");
      model.addAttribute("type", "success");
    } catch (Exception e) {
      model.addAttribute("message", "Error adding product to cart: " + e.getMessage());
      model.addAttribute("type", "error");
    }
    // This will resolve to src/main/resources/templates/fragments/cart-message.html
    return "fragments/cart-message";
  }*/



  private String toastMessage(String message) {
    return """
        <div class='toast align-items-center text-white bg-success border-0 show mb-2 fade show'
             role='alert' aria-live='assertive' aria-atomic='true'
             style='position: fixed; bottom: 2rem; right: 2rem; min-width: 200px; z-index: 1080;
                    animation: fadeout 0.5s ease-out 3s forwards, slideup 0.5s ease-out 3s forwards;'>
            <div class='d-flex'>
                <div class='toast-body'>"""
        + message + """
                    </div>
                </div>
            </div>
            """;
  }

  private String toastError(String message) {
    return """
        <div class='toast align-items-center text-white bg-danger border-0 show mb-2 fade show'
             role='alert' aria-live='assertive' aria-atomic='true'
             style='position: fixed; bottom: 2rem; right: 2rem; min-width: 200px; z-index: 1080;
                    animation: fadeout 0.5s ease-out 3s forwards, slideup 0.5s ease-out 3s forwards;'>
            <div class='d-flex'>
                <div class='toast-body'>
                    ❌ Errore: """
        + message + """
                    </div>
                </div>
            </div>
            """;
  }


  @PostMapping("/update")
  @ResponseBody
  public String aggiornaQuantitaCarrello(@RequestParam("id") Long idArticolo,
      @RequestParam("changeInQuantity") int changeInQuantity) {

    Long idUtente = securityService.getActiveUserId();

    carrelloService.aggiornaQuantitaArticolo(idUtente, idArticolo, changeInQuantity);

    return "✓ Carrello Aggiornato";
  }


  @PostMapping("/htmx/add")
  public String aggiungiArticoloHTMX(@RequestParam("id") Long idArticolo,
      @RequestParam("changeInQuantity") int quantita, Model model) {

    Long idUtente = securityService.getActiveUserId();
    carrelloService.aggiornaQuantitaArticolo(idUtente, idArticolo, quantita);

    int numeroArticoli = carrelloService.getNumeroArticoliNelCarrello(idUtente);
    model.addAttribute("numeroArticoli", numeroArticoli);

    return "fragments/cart-badge :: badge"; // Return the updated badge
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
