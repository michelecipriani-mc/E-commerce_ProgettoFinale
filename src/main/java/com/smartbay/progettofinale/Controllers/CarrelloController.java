package com.smartbay.progettofinale.Controllers;

import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartbay.progettofinale.DTO.AggiornaArticoloCarrelloRequest;
import com.smartbay.progettofinale.DTO.CarrelloDTO;
import com.smartbay.progettofinale.Security.SecurityService;
import com.smartbay.progettofinale.Services.CarrelloService;


@RestController
@RequestMapping("/api/carrello")
public class CarrelloController {

  private final SecurityService securityService;
  private final CarrelloService carrelloService;

  public CarrelloController(SecurityService securityService, CarrelloService carrelloService) {
    this.securityService = securityService;
    this.carrelloService = carrelloService;
  }

  @GetMapping
  public ResponseEntity<CarrelloDTO> getCarrello() {
    Long idUtente = securityService.getActiveUserId();
    CarrelloDTO dto = carrelloService.getCarrelloDTOFromUtente(idUtente);
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/update")
  public ResponseEntity<CarrelloDTO> aggiornaQuantitaArticolo(
      @RequestBody AggiornaArticoloCarrelloRequest request) {
    Long idUtente = securityService.getActiveUserId();
    CarrelloDTO dto = carrelloService.aggiornaQuantitaArticolo(idUtente, request.getIdArticolo(),
        request.getCambiamentoQuantita());
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("/remove/{idArticolo}")
  public ResponseEntity<CarrelloDTO> rimuoviArticoloDaCarrello(@PathVariable Long idArticolo) {
    Long idUtente = securityService.getActiveUserId();
    CarrelloDTO dto = carrelloService.rimuoviArticolo(idUtente, idArticolo);
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("/clear")
  public ResponseEntity<Void> svuotaCarrello() {
    Long idUtente = securityService.getActiveUserId();
    carrelloService.svuotaCarrello(idUtente);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/total")
  public ResponseEntity<BigDecimal> getCarrelloTotal() {
    Long idUtente = securityService.getActiveUserId();
    BigDecimal total = carrelloService.getPrezzoTotaleCarrello(idUtente);
    return ResponseEntity.ok(total);
  }
}
