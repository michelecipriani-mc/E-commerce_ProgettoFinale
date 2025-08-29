package com.smartbay.progettofinale.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//creazione della classe ArticoloQuantitàDTO
public class ArticoloQuantitaDTO {
    //che conterrà al suo interno come attributi l'oggetto DTO dell'articolo e la quantità
    private ArticleDTO articolo;
    private Integer quantita;
}
