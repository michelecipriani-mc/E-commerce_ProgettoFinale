package com.smartbay.progettofinale.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * DTO che rappresenta un ordine effettuato da un utente.
 * Utilizzato per trasferire i dati dell'ordine tra i vari livelli
 * dell'applicazione.
 * 
 * Campi principali:
 * - id: identificatore univoco dell'ordine
 * - utenteId: identificatore dell'utente che ha effettuato l'ordine
 * - dataOrdine: data e ora in cui l'ordine è stato effettuato
 * - totale: importo totale dell'ordine
 * - articoli: lista degli articoli con relative quantità inclusi nell'ordine
 */

@Data
public class OrdineDTO {
    private Long id;// ID ordine
    private Long utenteId; // ID utente che ha effettuato l'ordine
    private LocalDateTime dataOrdine; // Data e ora dell'ordine
    private BigDecimal totale; // Totale dell'ordine
    private List<ArticoloQuantitaDTO> articoli;// Articoli e quantità ordinati
}
