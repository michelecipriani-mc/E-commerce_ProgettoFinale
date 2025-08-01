package com.smartbay.progettofinale.DTO;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarrelloDTO {
  private List<ArticoloQuantitaDTO> articoli = new ArrayList<>();
}
