package com.smartbay.progettofinale.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggiornaArticoloCarrelloRequest {
  private long idArticolo;
  private int cambiamentoQuantita;
}
