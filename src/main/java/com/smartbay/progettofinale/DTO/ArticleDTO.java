package com.smartbay.progettofinale.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.smartbay.progettofinale.Models.Category;
import com.smartbay.progettofinale.Models.Image;
import com.smartbay.progettofinale.Models.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object per l'entità Article.
 */
@Setter
@Getter
@NoArgsConstructor
public class ArticleDTO {

    /**
     * Identificativo univoco dell'articolo.
     */
    private Long id;

    /**
     * Prezzo dell'articolo.
     */
    private BigDecimal price;

    /**
     * Titolo dell'articolo.
     */
    private String title;

    /**
     * Sottotitolo dell'articolo.
     */
    private String subtitle;

    /**
     * Corpo del testo dell'articolo.
     */
    private String body;

    /**
     * Data di pubblicazione dell'articolo.
     */
    private LocalDate publishDate;

    /**
     * Stato di approvazione dell'articolo (true, false o null).
     */
    private Boolean isAccepted;

    /**
     * L'utente autore dell'articolo.
     */
    private User user;

    /**
     * La categoria a cui appartiene l'articolo.
     */
    private Category category;

    /**
     * L'immagine associata all'articolo.
     */
    private Image image;

}