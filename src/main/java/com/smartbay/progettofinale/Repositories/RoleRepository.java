package com.smartbay.progettofinale.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbay.progettofinale.Models.Role;

/**
 * Repository per l'entità Role.
 * Estende JpaRepository, fornendo metodi CRUD, paginazione e sorting per i
 * ruoli.
 * 
 * Metodi ereditati includono:
 * - save(Role role)
 * - findById(Long id)
 * - findAll()
 * - deleteById(Long id)
 * - e altri metodi utili per la gestione della persistenza.
 * 
 * Metodi personalizzati definiti:
 * - findByName(String name): restituisce un oggetto Role in base al nome
 * fornito.
 * Utile per recuperare un ruolo specifico tramite il suo nome unico.
 * 
 * Annotato con @Repository per essere riconosciuto come componente Spring per
 * la gestione dei dati.
 */

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

}
