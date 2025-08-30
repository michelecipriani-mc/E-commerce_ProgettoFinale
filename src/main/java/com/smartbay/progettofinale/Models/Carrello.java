package com.smartbay.progettofinale.Models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.smartbay.progettofinale.Repositories.ArticleRepository;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carrello {
  // Costante inserita qui solo temporaneamente
  public static final int maxArticle = 5;
  // Variabili d'istanza
  private Long userId; // ID dell'utente al quale appartiene questo carrello
  // Mappa: (key: ID articolo) -> (value: quantità)
  private Map<Long, Integer> articles = new HashMap<>();
  // Flag per indicare se un articolo è stato rimosso dal carrello di un utente
  private boolean isModified = false;
  
  // --- Metodi
  
  /**
   * Metodo per aggiungere articoli al carrello o rimuoverne uno alla volta.
   * Attraverso Thymeleaf:
   * <ul>
   * <li>Nella pagina dell'articolo, l'utente avrà a disposizione un menù a
   * tendina dal quale scegliere il quantitativo di prodotti di quel tipo da
   * aggiungere al carrello.</li>
   * <li>Dalla pagina del carrello, L'utente avrà a disposizione una interfaccia
   * con tasti (+), (-) e (cestino), che richiamerà indirettamente questo metodo
   * quando preme (+) o (-), con {@code cambiamentoQuantità} valorizzato in +1 o
   * -1.</li>
   * </ul>
   *
   * N.B.: questo metodo non controlla se esiste un articolo associato a
   * idArticolo.
   * 
   * @param idArticolo          ID dell'articolo del quale modificare la quantità
   *                            nel
   *                            carrello dell'utente
   * @param cambiamentoQuantita Cambiamento nella quantità
   */
  public void aggiornaQuantitaArticolo(Long idArticolo, int cambiamentoQuantita) {
    // Inizializza carrello se ancora non esiste
    if (this.articles == null) {
      this.articles = new HashMap<>();
    }
    // --- L'utente ha cliccato (-) dalla pagina del carrello:
    if (cambiamentoQuantita <= 0) {
      // Controllare prima se l'articolo è effettivamente presente nel carrello,
      // altrimenti .get() restituirebbe null
      if (!articles.containsKey(idArticolo)) {
        // L'articolo non era presente.
        return;
      }
      // Rimuovere l'articolo se la sua quantità raggiungerebbe zero
      if (articles.get(idArticolo) + cambiamentoQuantita < 1) {
        articles.remove(idArticolo);
        // Altrimenti, sottrai il quantitativo (cambiamentoQuantità è negativo)
      } else {
        articles.merge(idArticolo, cambiamentoQuantita, Integer::sum);
      }
      // --- L'utente ha cliccato su (+) dalla pagina del carrello,
      // o ha selezionato un quantitativo dalla pagina dell'articolo
    } else if (cambiamentoQuantita > 0) {
      // Se l'articolo non era già presente, .merge() lo aggiunge automaticamente con il nuovo
      // valore.
      // Se invece era già presente, aggiunge il quantitativo richiesto
      articles.merge(idArticolo, cambiamentoQuantita, Integer::sum);
      // Controllare se il numero di prodotti eccede il massimo per singolo articolo
      if (articles.get(idArticolo) > maxArticle) {
        // Reimpostare il numero di prodotti al valore massimo consentito
        articles.put(idArticolo, maxArticle);
        throw new RuntimeException("Limite di " + maxArticle);
      }
    }
  }



  /**
   * Rimuovi del tutto un articolo dal carrello.
   * 
   * Chiamato attraverso la pagina del carrello, quando l'utente clicca sul tasto cestino.
   * 
   * @param idArticolo
   */
  public void rimuoviArticolo(Long idArticolo) {
    articles.remove(idArticolo);
  }

  /**
   * Rimuovi tutti gli articoli dal carrello.
   */
  public void svuota() {
    articles.clear();
  }

  // Passa dipendenza repository
  public BigDecimal getPrezzoTotale(ArticleRepository articoloRepository) { 
    // Inizializziamo il totale a 0 (BigDecimal.ZERO per evitare problemi con i double)
    BigDecimal total = BigDecimal.ZERO;
    // Se la mappa degli articoli è vuota, il totale è 0
    if (articles.isEmpty()) {
      return total;
    }
    // Scorriamo la mappa degli articoli (idArticolo -> quantita)
    for (Map.Entry<Long, Integer> entry : articles.entrySet()) {
      Long idArticolo = entry.getKey(); // otteniamo l'ID dell'articolo
      Integer quantita = entry.getValue(); // otteniamo la quantità
      // Recuperiamo l'articolo dal database tramite l'ID, se non viene trovato, restituiamo null
      Article articolo = articoloRepository.findById(idArticolo).orElse(null); 
      //se l'articolo è stato trovato, quindi diverso da null
      if (articolo != null) {
        //aggiungiamo a total il prezzo dell'articolo * quantità
        total = total.add(articolo.getPrice().multiply(BigDecimal.valueOf(quantita)));
      }
    }
    // ritorniamo il totale degli articoli nel carrello
    return total;
  }

}
