package com.smartbay.progettofinale.Services;
import org.springframework.stereotype.Service;

import com.smartbay.progettofinale.DTO.CarrelloDTO;
import com.smartbay.progettofinale.Models.Carrello;
import com.smartbay.progettofinale.Repositories.ArticleRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Service
public class CarrelloService {

  private final ArticleRepository articoloRepository;

  private final Map<Long, Carrello> carrelli = new HashMap<>();

  public CarrelloService(ArticleRepository  articoloRepository) {
    this.articoloRepository = articoloRepository;
  }

  // Ottieni carrello dell'Utente, o creane uno se non esiste
  private Carrello getCarrelloFromUtente(Long idUtente) {

    if (carrelli.containsKey(idUtente)) {
      return carrelli.get(idUtente);
    }

    Carrello carrello = new Carrello(idUtente, new HashMap<>());

    carrelli.put(idUtente, carrello);

    return carrello; // Create a new cart for this user ID
  }

  public CarrelloDTO getCarrelloDTOFromUtente(Long idUtente) {
    return this.carrelloToDTO(getCarrelloFromUtente(idUtente));
  }

  public CarrelloDTO aggiornaQuantitaArticolo(Long idUtente, Long idArticolo, int cambiamentoQuantita) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    articoloRepository.findById(idArticolo)
        .orElseThrow(() -> new RuntimeException("Nessun articolo trovato con id: " + idArticolo));
    carrello.aggiornaQuantitaArticolo(idArticolo, cambiamentoQuantita);
    return this.carrelloToDTO(carrello);
  }

  public CarrelloDTO rimuoviArticolo(Long idUtente, Long idArticolo) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    carrello.rimuoviArticolo(idArticolo);
    return this.carrelloToDTO(carrello);
  }

  public void svuotaCarrello(Long idUtente) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    carrello.svuota();
  }

  // New method to get total price using the ArticleRepository
  public BigDecimal getPrezzoTotaleCarrello(Long idUtente) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    return carrello.getPrezzoTotale(articoloRepository);
  }

  public CarrelloDTO carrelloToDTO(Carrello carrello) {

    CarrelloDTO dto = new CarrelloDTO();

    if (carrello.getArticles() == null || carrello.getArticles().isEmpty()) {
      return dto;
    }

    // for (Map.Entry<Long, Integer> entry : carrello.getArticles().entrySet()) {

    //   dto.getArticoli().put(
    //       // Key (Stringa nome)
    //       this.articoloRepository.findById(entry.getKey()).orElseThrow().get, 
    //       // Value (int quantità)
    //       entry.getValue());
    // }

    return dto;
  }
}
