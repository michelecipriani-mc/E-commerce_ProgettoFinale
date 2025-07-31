package com.smartbay.progettofinale.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO che rappresenta una categoria di articoli.
 * Utilizzato per trasferire dati relativi a una categoria tra i vari livelli
 * dell'applicazione.
 * 
 * Campi principali:
 * - id: identificatore univoco della categoria
 * - name: nome della categoria
 * - numberOfArticles: numero di articoli associati a questa categoria (utile
 * per visualizzazioni/statistiche)
 */

@Setter
@Getter
@NoArgsConstructor
public class CategoryDTO {

    private Long id;// ID categoria
    private String name;// Nome della categoria
    private Integer numberOfArticles;// Numero di articoli nella categoria

}
