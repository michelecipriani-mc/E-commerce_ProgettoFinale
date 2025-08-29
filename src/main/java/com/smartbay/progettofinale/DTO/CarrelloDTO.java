package com.smartbay.progettofinale.DTO;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//creazione della classe CarrelloDTO
public class CarrelloDTO {
  //unico attributo del CarrelloDTO sarà una lista di tipo ArticoloQuantitàDTO che conterrà al suo interno tutti gli articoli 
  private List<ArticoloQuantitaDTO> articoli = new ArrayList<>();
}
