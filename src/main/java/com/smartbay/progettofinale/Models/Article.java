package com.smartbay.progettofinale.Models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @JsonIgnoreProperties({ "articles" })
    private User user;

    /**
     * Categoria a cui appartiene l'articolo.
     * <p>
     * Relazione molti-a-uno: molti articoli possono avere una categoria.
     */
    @ManyToOne
    @JsonIgnoreProperties({ "articles" })
    private Category category;

    /**
     * Immagine associata all'articolo.
     * <p>
     * Relazione uno-a-uno: un articolo può avere una sola immagine.
     */
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({ "article" })
    private List<Image> images = new ArrayList<>();

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
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Article article = (Article) obj;

        if (!title.equals(article.getTitle()))
            return false;
        if (!subtitle.equals(article.getSubtitle()))
            return false;
        if (!body.equals(article.getBody()))
            return false;
        if (!publishDate.equals(article.getPublishDate()))
            return false;
        if (!category.getName().equals(article.getCategory().getName()))
            return false;

        // Confronta liste di immagini per path
        if (images.size() != article.getImages().size())
            return false;
        for (int i = 0; i < images.size(); i++) {
            if (!images.get(i).getPath().equals(article.getImages().get(i).getPath())) {
                return false;
            }
        }

        return true;
    }

}