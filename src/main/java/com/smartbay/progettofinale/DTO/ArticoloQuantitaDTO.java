package com.smartbay.progettofinale.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticoloQuantitaDTO {
    private ArticleDTO articolo;
    private Integer quantita;
}
