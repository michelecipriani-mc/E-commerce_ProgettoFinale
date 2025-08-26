package com.smartbay.progettofinale.Services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.smartbay.progettofinale.DTO.ArticleDTO;
import com.smartbay.progettofinale.DTO.ArticoloQuantitaDTO;
import com.smartbay.progettofinale.DTO.CarrelloDTO;
import com.smartbay.progettofinale.Models.Carrello;
import com.smartbay.progettofinale.Repositories.ArticleRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CarrelloService {

  private final ArticleRepository articoloRepository;

  private final ModelMapper modelMapper;

  private final Map<Long, Carrello> carrelli = new ConcurrentHashMap<>();

  public CarrelloService(ArticleRepository articoloRepository, ModelMapper modelMapper) {
    this.articoloRepository = articoloRepository;
    this.modelMapper = modelMapper;
  }

  // Ottieni carrello dell'Utente, o creane uno se non esiste
  public Carrello getCarrelloFromUtente(Long idUtente) {

    if (carrelli.containsKey(idUtente)) {
      return carrelli.get(idUtente);
    }

    Carrello carrello = new Carrello(idUtente, new ConcurrentHashMap<>());

    carrelli.put(idUtente, carrello);

    return carrello; // Create a new cart for this user ID
  }

  public CarrelloDTO getCarrelloDTOFromUtente(Long idUtente) {
    return this.carrelloToDTO(getCarrelloFromUtente(idUtente));
  }

  public CarrelloDTO aggiornaQuantitaArticolo(Long idUtente, Long idArticolo,
      int cambiamentoQuantita) {
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

  /**
   * Rimuovi ogni istanza di un articolo da tutti i carrelli.
   * Chiamato da ArticleService quando un articolo è modificato o eliminato
   *
   * @param idArticolo The ID of the article to be removed.
   *    
   */
  public void rimuoviArticoloDaTuttiICarrelli(Long idArticolo) {

    if (carrelli.isEmpty()) {
      return;
    }

    for (Carrello carrello : carrelli.values()) {
      carrello.rimuoviArticolo(idArticolo);
    }
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

    List<ArticoloQuantitaDTO> lista = carrello.getArticles().entrySet().stream()
        .map(entry -> new ArticoloQuantitaDTO(
            modelMapper
                .map(articoloRepository.findById(entry.getKey()).orElseThrow(), ArticleDTO.class),
            entry.getValue()))
        .sorted(
            Comparator.comparing(a -> a.getArticolo().getTitle(), String.CASE_INSENSITIVE_ORDER))
        .toList();

    dto.setArticoli(lista);
    return dto;
  }

  public int getNumeroArticoliNelCarrello(Long idUtente) {
    return this.carrelli.get(idUtente).getArticles().size();
  }
}
