package com.smartbay.progettofinale.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
//creazione della classe ArticoloOrdine
public class ArticoloOrdine {
    //attributi della classe
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long articoloId;
    private String titoloArticolo;
    private int quantita;
    private BigDecimal prezzoSingolo;
    //relazione N-1 tra ArticoloOrdine e Ordine
    @ManyToOne
    @JoinColumn(name = "ordine_id")
    private Ordine ordine;
}
