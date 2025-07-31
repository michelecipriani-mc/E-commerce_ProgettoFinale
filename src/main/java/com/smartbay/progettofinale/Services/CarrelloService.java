package com.smartbay.progettofinale.Services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.smartbay.progettofinale.DTO.ArticleDTO;
import com.smartbay.progettofinale.DTO.ArticoloQuantitaDTO;
import com.smartbay.progettofinale.DTO.CarrelloDTO;
import com.smartbay.progettofinale.Models.Article;
import com.smartbay.progettofinale.Models.Carrello;
import com.smartbay.progettofinale.Repositories.ArticleRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servizio per la gestione del carrello della spesa degli utenti.
 * Mantiene una mappa in memoria che associa a ciascun utente un carrello
 * personale,
 * e fornisce metodi per manipolare il contenuto del carrello.
 */

@Service
public class CarrelloService {
  // Repository per accedere agli articoli dal database
  private final ArticleRepository articoloRepository;
  // Mapper per convertire entità in DTO e viceversa
  private final ModelMapper modelMapper;
  // Mappa in memoria che associa l'id utente al suo carrello
  private final Map<Long, Carrello> carrelli = new HashMap<>();

  // Costruttore con dependency injection
  public CarrelloService(ArticleRepository articoloRepository, ModelMapper modelMapper) {
    this.articoloRepository = articoloRepository;
    this.modelMapper = modelMapper;
  }

  /**
   * Ottiene il carrello associato a un utente tramite il suo id.
   * Se il carrello non esiste, ne crea uno nuovo e lo aggiunge alla mappa.
   * 
   * @param idUtente id dell'utente
   * @return il carrello dell'utente
   */
  public Carrello getCarrelloFromUtente(Long idUtente) {

    if (carrelli.containsKey(idUtente)) {
      return carrelli.get(idUtente);
    }

    Carrello carrello = new Carrello(idUtente, new HashMap<>());

    carrelli.put(idUtente, carrello);

    return carrello; // Create a new cart for this user ID
  }

  /**
   * Ottiene il DTO del carrello per un dato utente.
   * 
   * @param idUtente id dell'utente
   * @return CarrelloDTO contenente i dati del carrello
   */
  public CarrelloDTO getCarrelloDTOFromUtente(Long idUtente) {
    return this.carrelloToDTO(getCarrelloFromUtente(idUtente));
  }

  /**
   * Aggiorna la quantità di un articolo nel carrello di un utente.
   * Verifica che l'articolo esista nel repository, altrimenti lancia eccezione.
   * 
   * @param idUtente            id dell'utente
   * @param idArticolo          id dell'articolo da aggiornare
   * @param cambiamentoQuantita quantità da aggiungere o rimuovere (può essere
   *                            positiva o negativa)
   * @return CarrelloDTO aggiornato
   */
  public CarrelloDTO aggiornaQuantitaArticolo(Long idUtente, Long idArticolo,
      int cambiamentoQuantita) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    articoloRepository.findById(idArticolo)
        .orElseThrow(() -> new RuntimeException("Nessun articolo trovato con id: " + idArticolo));
    carrello.aggiornaQuantitaArticolo(idArticolo, cambiamentoQuantita);
    return this.carrelloToDTO(carrello);
  }

  /**
   * Rimuove un articolo dal carrello di un utente.
   * 
   * @param idUtente   id dell'utente
   * @param idArticolo id dell'articolo da rimuovere
   * @return CarrelloDTO aggiornato
   */
  public CarrelloDTO rimuoviArticolo(Long idUtente, Long idArticolo) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    carrello.rimuoviArticolo(idArticolo);
    return this.carrelloToDTO(carrello);
  }

  /**
   * Svuota completamente il carrello di un utente.
   * 
   * @param idUtente id dell'utente
   */
  public void svuotaCarrello(Long idUtente) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    carrello.svuota();
  }

  // New method to get total price using the ArticleRepository
  /**
   * Calcola il prezzo totale degli articoli nel carrello di un utente,
   * utilizzando i dati del repository articoli.
   * 
   * @param idUtente id dell'utente
   * @return prezzo totale come BigDecimal
   */
  public BigDecimal getPrezzoTotaleCarrello(Long idUtente) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    return carrello.getPrezzoTotale(articoloRepository);
  }

  /**
   * Converte un oggetto Carrello in CarrelloDTO per la presentazione,
   * mappando ogni articolo in un DTO con la quantità.
   * Ordina gli articoli in ordine alfabetico per titolo (case-insensitive).
   * 
   * @param carrello il carrello da convertire
   * @return CarrelloDTO corrispondente
   */
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

  /**
   * Restituisce il numero di articoli distinti nel carrello di un utente.
   * 
   * @param idUtente id dell'utente
   * @return numero di articoli distinti nel carrello
   */
  public int getNumeroArticoliNelCarrello(Long idUtente) {
    return this.carrelli.get(idUtente).getArticles().size();
  }
}
