package com.smartbay.progettofinale.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class ArticoloOrdine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long articoloId;

    private String titoloArticolo;

    private int quantita;

    private BigDecimal prezzoSingolo;

    @ManyToOne
    @JoinColumn(name = "ordine_id")
    private Ordine ordine;
}
