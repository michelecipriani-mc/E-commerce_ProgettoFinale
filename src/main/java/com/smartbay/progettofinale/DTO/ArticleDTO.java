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
 * Data Transfer Object (DTO) per rappresentare un articolo.
 * Utilizzato per trasferire dati dell'articolo tra i vari livelli
 * dell'applicazione,
 * senza esporre direttamente l'entità di persistenza.
 * 
 * Contiene le informazioni principali di un articolo, come:
 * - id univoco
 * - prezzo
 * - titolo e sottotitolo
 * - testo del corpo dell'articolo
 * - data di pubblicazione
 * - stato di accettazione (null = in attesa, true = accettato, false =
 * rifiutato)
 * - utente autore dell'articolo
 * - categoria a cui appartiene l'articolo
 * - immagine associata all'articolo
 */

@Setter
@Getter
@NoArgsConstructor
public class ArticleDTO {

    private Long id;// Identificatore univoco dell'articolo
    private BigDecimal price;// Prezzo dell'articolo
    private String title; // Titolo principale
    private String subtitle;// Sottotitolo opzionale
    private String body;// Contenuto principale dell'articolo
    private LocalDate publishDate;// Data di pubblicazione
    private Boolean isAccepted;// Stato di accettazione/revisione dell'articolo
    private User user;// Autore dell'articolo (utente)
    private Category category;// Categoria di appartenenza
    private Image image; // Immagine associata all'articolo

}