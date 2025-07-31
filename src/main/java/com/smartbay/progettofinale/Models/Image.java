package com.smartbay.progettofinale.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entità JPA che rappresenta un'immagine associata a un articolo.
 * 
 * Attributi:
 * - id: identificativo univoco dell'immagine (generato automaticamente)
 * - path: percorso pubblico dell'immagine (es: /images/uuid_nomefile.jpg)
 * - article: riferimento all'articolo a cui l'immagine è associata (relazione
 * ManyToOne)
 *
 * Annotazioni:
 * - @Entity: definisce la classe come entità persistente JPA
 * - @Table(name = "images"): specifica il nome della tabella nel database
 * - @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder (Lombok): generano
 * automaticamente
 * metodi getter/setter, costruttori e builder pattern
 * - @Column(nullable = false, unique = true): assicura che ogni immagine abbia
 * un percorso univoco
 * - @ManyToOne(fetch = FetchType.LAZY, optional = false): relazione con
 * l'entità Article,
 * caricata in modo "lazy" per migliorare le performance
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Percorso pubblico dell'immagine, es: /images/uuid_nomefile.jpg
     */
    @Column(nullable = false, unique = true)
    private String path;

    /**
     * Articolo associato all'immagine.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
}