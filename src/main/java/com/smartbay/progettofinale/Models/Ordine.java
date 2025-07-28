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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long utenteId;

  private LocalDateTime dataOrdine;

  private BigDecimal totale;

  @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

  @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL)
  private List<ArticoloOrdine> articoli;
}