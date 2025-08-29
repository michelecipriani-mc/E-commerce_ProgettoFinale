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
 * Entità di persistenza che rappresenta un articolo.
 * <p>
 * Mappa i dati di un articolo alla tabella "articles" nel database.
 */
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

    /** Identificativo univoco dell'articolo. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titolo dell'articolo. */
    @Column(nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String title;

    /** Sottotitolo dell'articolo */
    @Column(nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String subtitle;

    /** Corpo del testo dell'articolo. */
    @Column(nullable = false, length = 1000)
    @NotNull
    @Size(max = 1000)
    private String body;

    /** Prezzo dell'articolo. */
    @Column(nullable = false)
    @NotNull
    @Min(value = 1)
    @Max(value = 1000)
    private BigDecimal price;

    @Column(nullable = true, length = 8)
    @NotNull
    private LocalDate publishDate;

    /**
     * Stato di approvazione dell'articolo.
     * <p>
     * Null: in attesa di revisione.
     * True: approvato.
     * False: non approvato.
     */
    @Column(nullable = true)
    private Boolean isAccepted;

    /**
     * Restituisce lo stato di approvazione dell'articolo.
     *
     * @return Lo stato di approvazione.
     */
    public Boolean getIsAccepted() {
        return isAccepted;
    }
    
    /**
     * Imposta lo stato di approvazione dell'articolo.
     *
     * @param accepted Lo stato di approvazione.
     */
    public void setIsAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    /**
     * Utente autore dell'articolo.
     * <p>
     * Relazione molti-a-uno: molti articoli possono avere un utente.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"articles"})
    private User user;

    /**
     * Categoria a cui appartiene l'articolo.
     * <p>
     * Relazione molti-a-uno: molti articoli possono avere una categoria.
     */
    @ManyToOne
    @JsonIgnoreProperties({"articles"})
    private Category category;

    /**
     * Immagine associata all'articolo.
     * <p>
     * Relazione uno-a-uno: un articolo può avere una sola immagine.
     */
    @OneToOne(mappedBy = "article")
    @JsonIgnoreProperties({"article"})
    private Image image;

    /**
     * Confronta se due oggetti Article sono uguali.
     * <p>
     * L'uguaglianza è basata su titolo, sottotitolo, corpo, data di pubblicazione,
     * nome della categoria e percorso dell'immagine.
     *
     * @param obj L'oggetto da confrontare.
     * @return True se gli oggetti sono uguali, false altrimenti.
     */
    @Override
    public boolean equals(Object obj) {
        Article article = (Article) obj;
        if (title.equals(article.getTitle()) && subtitle.equals(article.getSubtitle()) &&
            body.equals(article.getBody()) && publishDate.equals(article.getPublishDate()) &&
            category.getName().equals(article.getCategory().getName()) && image.getPath().equals(article.getImage().getPath())) {
            return true; 
        }
        return false;
    }
    
}