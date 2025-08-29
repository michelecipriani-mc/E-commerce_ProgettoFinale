package com.smartbay.progettofinale.Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Ordine {
  //attributi della classe
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long utenteId;
  private LocalDateTime dataOrdine;
  private BigDecimal totale;
  //relazione N-1 con l'entità user
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
  //relazione 1-N con l'entità ArticoloOrdine
  @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL)
  private List<ArticoloOrdine> articoli;
}