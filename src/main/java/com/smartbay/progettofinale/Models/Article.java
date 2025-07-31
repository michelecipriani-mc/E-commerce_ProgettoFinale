package com.smartbay.progettofinale.Models;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity JPA che rappresenta un articolo nel sistema.
 * Mappata sulla tabella "articles" nel database.
 * 
 * Attributi principali:
 * - id: identificatore univoco generato automaticamente
 * - title: titolo dell'articolo (non nullo, max 100 caratteri)
 * - subtitle: sottotitolo dell'articolo (non nullo, max 100 caratteri)
 * - body: contenuto principale dell'articolo (non nullo, max 1000 caratteri)
 * - price: prezzo associato all'articolo (non nullo, valore tra 1 e 1000)
 * - publishDate: data di pubblicazione (nullable, non annotata come @NotNull
 * coerentemente)
 * - isAccepted: stato di accettazione dell'articolo (nullable)
 * 
 * Relazioni:
 * - Molti a uno con User (autore dell'articolo)
 * - Molti a uno con Category (categoria dell'articolo)
 * - Uno a uno con Image (immagine associata all'articolo)
 * 
 * Note:
 * - Usa Lombok per generare getter, setter e costruttore senza argomenti
 * - Alcuni campi annotati con validazioni di Bean Validation
 * (@NotNull, @Size, @Min, @Max)
 * - Override del metodo equals per confrontare articoli basandosi su titolo,
 * sottotitolo, body, data pubblicazione,
 * nome della categoria e path dell'immagine. Attenzione: non considera nullità
 * e tipo dell'oggetto in equals.
 * - Usa @JsonIgnoreProperties per evitare problemi di serializzazione JSON
 * ciclica nelle relazioni
 */

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String title;

    @Column(nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String subtitle;

    @Column(nullable = false, length = 1000)
    @NotNull
    @Size(max = 1000)
    private String body;

    @Column(nullable = false)
    @NotNull
    @Min(value = 1)
    @Max(value = 1000)
    private BigDecimal price;

    @Column(nullable = true, length = 8)
    @NotNull
    private LocalDate publishDate;

    @Column(nullable = true)
    private Boolean isAccepted;

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "articles" })
    private User user;

    @ManyToOne
    @JsonIgnoreProperties({ "articles" })
    private Category category;

    @OneToOne(mappedBy = "article")
    @JsonIgnoreProperties({ "article" })
    private Image image;

    /**
     * Override di equals per confrontare due articoli.
     * Confronta titolo, sottotitolo, body, data di pubblicazione,
     * nome della categoria e path dell'immagine.
     * ATTENZIONE: non verifica nullità né tipo con instanceof,
     * potrebbe causare eccezioni se usato con oggetti non Article o con valori
     * nulli.
     */
     //@param obj oggetto da confrontare
     //@return true se considerati uguali, false altrimenti
     

    @Override
    public boolean equals(Object obj) {
        Article article = (Article) obj;
        if (title.equals(article.getTitle()) && subtitle.equals(article.getSubtitle()) &&
                body.equals(article.getBody()) && publishDate.equals(article.getPublishDate()) &&
                category.getName().equals(article.getCategory().getName())
                && image.getPath().equals(article.getImage().getPath())) {
            return true;
        }
        return false;
    }

}