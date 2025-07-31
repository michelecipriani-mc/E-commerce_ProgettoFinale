package com.smartbay.progettofinale.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Entity JPA che rappresenta un'associazione tra un articolo e un ordine.
 * Viene usata per memorizzare i dettagli degli articoli inclusi in un ordine
 * specifico,
 * permettendo di "fotografare" le informazioni del prodotto (come titolo e
 * prezzo)
 * al momento dell'acquisto, indipendentemente da eventuali modifiche future.
 * 
 * Attributi principali:
 * - id: identificatore univoco dell'entità, generato automaticamente
 * - articoloId: ID dell'articolo acquistato (non è una relazione diretta con
 * `Article`)
 * - titoloArticolo: titolo dell'articolo (copiato al momento dell'acquisto)
 * - quantita: quantità acquistata dell'articolo
 * - prezzoSingolo: prezzo del singolo articolo al momento dell'acquisto
 * 
 * Relazioni:
 * - Molti a uno con Ordine (ogni `ArticoloOrdine` appartiene a un solo
 * `Ordine`)
 * 
 * Annotazioni:
 * - @Entity: specifica che è una classe persistente JPA
 * - @Data (Lombok): genera automaticamente getter, setter, toString, equals,
 * hashCode, ecc.
 */

@Entity
@Data
public class ArticoloOrdine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long articoloId;

    private String titoloArticolo;

    private int quantita;

    private BigDecimal prezzoSingolo;

    @ManyToOne
    @JoinColumn(name = "ordine_id")
    private Ordine ordine;
}
