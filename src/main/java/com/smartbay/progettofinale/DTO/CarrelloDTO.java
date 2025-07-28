package com.smartbay.progettofinale.DTO;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarrelloDTO {
  private Map<ArticleDTO, Integer> articoli = new HashMap<>();
}
