package com.smartbay.progettofinale.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la richiesta di aggiornamento della quantità di un articolo nel
 * carrello.
 * 
 * Contiene:
 * - l'id dell'articolo da aggiornare
 * - la variazione (positiva o negativa) della quantità desiderata nel carrello
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggiornaArticoloCarrelloRequest {
  private long idArticolo;// Identificatore unico dell'articolo da aggiornare
  private int cambiamentoQuantita;// Quantità da aggiungere o rimuovere (es. +2 o -1)
}
