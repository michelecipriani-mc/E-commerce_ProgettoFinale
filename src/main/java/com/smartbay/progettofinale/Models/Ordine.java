package com.smartbay.progettofinale.Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

/**
 * Entità JPA che rappresenta un ordine effettuato da un utente.
 *
 * Attributi:
 * - id: identificativo univoco dell'ordine (generato automaticamente)
 * - utenteId: ID dell’utente autore dell’ordine (ridondante rispetto all'entità
 * user, ma può essere utile per query semplificate)
 * - dataOrdine: data e ora in cui è stato effettuato l’ordine
 * - totale: importo complessivo dell’ordine
 * - user: riferimento all'utente che ha effettuato l’ordine (relazione
 * ManyToOne)
 * - articoli: lista degli articoli contenuti nell’ordine (relazione OneToMany
 * verso ArticoloOrdine)
 *
 * Annotazioni:
 * - @Entity: identifica la classe come entità JPA
 * - @Data: genera automaticamente getter, setter, equals, hashCode, toString
 * (Lombok)
 * - @GeneratedValue: strategia per la generazione automatica dell’ID
 * - @JoinColumn: specifica la foreign key per l'associazione con l'utente
 * - @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL): relazione
 * uno-a-molti con ArticoloOrdine,
 * dove ogni articolo è legato a un ordine specifico e le operazioni sono
 * propagate (es. salvataggio o cancellazione)
 */

@Entity
@Data
public class Ordine {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long utenteId;

  private LocalDateTime dataOrdine;

  private BigDecimal totale;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL)
  private List<ArticoloOrdine> articoli;
}