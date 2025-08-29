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
  //Inserimento delle DI
  private final ArticleRepository articoloRepository;
  private final ModelMapper modelMapper;
  // Mappa in memoria che associa l'ID utente al relativo carrello
  // ConcurrentHashMap permette accessi in sicurezza
  private final Map<Long, Carrello> carrelli = new ConcurrentHashMap<>();
  //costruttore
  public CarrelloService(ArticleRepository articoloRepository, ModelMapper modelMapper) {
    this.articoloRepository = articoloRepository;
    this.modelMapper = modelMapper;
  }

  // Ottieni carrello dell'Utente, o creane uno se non esiste
  public Carrello getCarrelloFromUtente(Long idUtente) {
    if (carrelli.containsKey(idUtente)) {
      return carrelli.get(idUtente);
    }
    // Se l'utente non ha ancora un carrello, lo creiamo e lo inseriamo nella mappa
    Carrello carrello = new Carrello(idUtente, new ConcurrentHashMap<>(), false);

    carrelli.put(idUtente, carrello);

    return carrello;
  }
  // Restituisce il carrello dell'utente come DTO (per inviarlo al client)
  public CarrelloDTO getCarrelloDTOFromUtente(Long idUtente) {
    return this.carrelloToDTO(getCarrelloFromUtente(idUtente));
  }
  // Aggiorna la quantità di un articolo nel carrello dell'utente
  public CarrelloDTO aggiornaQuantitaArticolo(Long idUtente, Long idArticolo,
      int cambiamentoQuantita) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    // Controlla che l'articolo esista nel database, altrimenti lancia un'eccezione
    articoloRepository.findById(idArticolo)
        .orElseThrow(() -> new RuntimeException("Nessun articolo trovato con id: " + idArticolo));
    // Aggiorna la quantità nel carrello
    carrello.aggiornaQuantitaArticolo(idArticolo, cambiamentoQuantita);
    // Restituisce il carrello aggiornato come DTO
    return this.carrelloToDTO(carrello);
  }
  // Rimuove un articolo specifico dal carrello dell'utente
  public CarrelloDTO rimuoviArticolo(Long idUtente, Long idArticolo) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    carrello.rimuoviArticolo(idArticolo);
    return this.carrelloToDTO(carrello);
  }

  /**
   * Rimuovi ogni istanza di un articolo da tutti i carrelli.
   * Chiamato da {@code ArticoloService} quando un articolo è modificato o eliminato
   *
   * @param idArticolo ID dell'articolo da rimuovere.
   * @see ArticoloService
   */
  public void rimuoviArticoloDaTuttiICarrelli(Long idArticolo) {

    if (carrelli.isEmpty()) {
      return;
    }
    // Scorriamo tutti i carrelli e rimuoviamo l'articolo
    for (Carrello carrello : carrelli.values()) {
      carrello.rimuoviArticolo(idArticolo);
      carrello.setModified(true); // segnala che il carrello è stato modificato
    }
  }
  // Restituisce true se il carrello dell'utente è stato modificato
  public boolean CarrelloIsModified(Long idUtente) {
    return getCarrelloFromUtente(idUtente).isModified();
  }
  // Reset del flag "modified" del carrello
  public void ResetCarrelloModifiedFlag(Long idUtente) {
    getCarrelloFromUtente(idUtente).setModified(false);
  }
  // Svuota completamente il carrello dell'utente
  public void svuotaCarrello(Long idUtente) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    carrello.svuota();
  }

  // Calcola il prezzo totale del carrello dell'utente
  public BigDecimal getPrezzoTotaleCarrello(Long idUtente) {
    Carrello carrello = getCarrelloFromUtente(idUtente);
    return carrello.getPrezzoTotale(articoloRepository);
  }
  // Converte un Carrello in CarrelloDTO, pronto per il frontend
  public CarrelloDTO carrelloToDTO(Carrello carrello) {
    CarrelloDTO dto = new CarrelloDTO();
    // Se il carrello è vuoto, ritorniamo un DTO vuoto
    if (carrello.getArticles() == null || carrello.getArticles().isEmpty()) {
      return dto;
    }
    // Creiamo la lista di ArticoloQuantitaDTO ordinata per titolo articolo
    List<ArticoloQuantitaDTO> lista = carrello.getArticles().entrySet().stream()
        /**Per ogni entry della mappa:
         * recuperiamo l'entità Article dal repository tramite ID
         * la convertiamo in ArticleDTO usando ModelMapper
         * creiamo un oggetto ArticoloQuantitaDTO che unisce il DTO dell'articolo con la quantità presente nel carrello 
        **/
        .map(entry -> new ArticoloQuantitaDTO(
            modelMapper
                .map(articoloRepository.findById(entry.getKey()).orElseThrow(), ArticleDTO.class),
            entry.getValue()))
        // Ordiniamo la lista dei DTO in base al titolo dell'articolo, ignorando maiuscole/minuscole
        .sorted(
            Comparator.comparing(a -> a.getArticolo().getTitle(), String.CASE_INSENSITIVE_ORDER))
        .toList(); // Convertiamo lo stream in lista concreta
    // Impostiamo la lista ordinata di articoli nel DTO del carrello
    dto.setArticoli(lista);
    // Restituiamo il DTO pronto per il frontend
    return dto;
  }
  // Restituisce il numero di articoli diversi presenti nel carrello dell'utente
  public int getNumeroArticoliNelCarrello(Long idUtente) {
    return this.carrelli.get(idUtente).getArticles().size();
  }
}
