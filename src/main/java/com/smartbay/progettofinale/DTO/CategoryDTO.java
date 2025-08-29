package com.smartbay.progettofinale.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object per l'entità Category.
 */
@Setter
@Getter
@NoArgsConstructor
public class CategoryDTO {

    /**
     * Identificativo univoco della categoria.
     */
    private Long id;

    /**
     * Nome della categoria.
     */
    private String name;

    /**
     * Numero di articoli associati a questa categoria.
     */
    private Integer numberOfArticles;
    
}

