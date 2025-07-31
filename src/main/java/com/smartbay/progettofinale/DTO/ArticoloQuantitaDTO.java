package com.smartbay.progettofinale.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO che rappresenta una coppia articolo-quantità.
 * Utilizzato per gestire articoli insieme alla quantità associata,
 * ad esempio per rappresentare la quantità di un articolo nel carrello.
 * 
 * Contiene:
 * - articolo: l'oggetto ArticleDTO che descrive l'articolo
 * - quantita: il numero di unità di quell'articolo
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticoloQuantitaDTO {
    private ArticleDTO articolo;// Articolo associato
    private Integer quantita;// Quantità dell'articolo
}
