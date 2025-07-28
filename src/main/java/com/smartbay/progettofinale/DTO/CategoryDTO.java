package com.smartbay.progettofinale.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CategoryDTO {

    private Long id;
    private String name;
    private Integer numberOfArticles;
    
}

