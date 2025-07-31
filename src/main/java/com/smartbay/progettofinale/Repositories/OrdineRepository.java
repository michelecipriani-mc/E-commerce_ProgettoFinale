package com.smartbay.progettofinale.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartbay.progettofinale.Models.Ordine;
import com.smartbay.progettofinale.Models.User;

/**
 * Repository per l'entità Ordine.
 * Estende JpaRepository, fornendo metodi CRUD completi, paginazione e sorting
 * per l'entità Ordine.
 * 
 * Metodi ereditati includono:
 * - save(Ordine ordine)
 * - findById(Long id)
 * - findAll()
 * - deleteById(Long id)
 * - e molti altri utili per la gestione dei dati.
 * 
 * Metodi personalizzati definiti:
 * - findByUser(User user): restituisce una lista di ordini associati a un dato
 * utente.
 * Permette quindi di recuperare tutti gli ordini effettuati da un utente
 * specifico.
 * 
 * Interfaccia contrassegnata come repository di Spring Data JPA per la gestione
 * della persistenza.
 */

public interface OrdineRepository extends JpaRepository<Ordine, Long> {
  List<Ordine> findByUser(User user);

  List<Ordine> findByUserOrderByDataOrdineDesc(User user);
}