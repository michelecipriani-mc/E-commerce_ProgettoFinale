package com.smartbay.progettofinale.DTO;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO che rappresenta il carrello della spesa.
 * Contiene una lista di articoli con le relative quantità,
 * utilizzata per trasferire i dati del carrello tra livelli dell'applicazione.
 * 
 * La lista 'articoli' contiene oggetti ArticoloQuantitaDTO,
 * ognuno dei quali associa un articolo alla sua quantità nel carrello.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarrelloDTO {
  private List<ArticoloQuantitaDTO> articoli = new ArrayList<>();// Lista di articoli con quantità
}
