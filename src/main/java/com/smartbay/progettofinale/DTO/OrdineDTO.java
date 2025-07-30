package com.smartbay.progettofinale.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrdineDTO {
    private Long id;
    private Long utenteId;
    private LocalDateTime dataOrdine;
    private BigDecimal totale;
    private List<ArticoloQuantitaDTO> articoli;
}
