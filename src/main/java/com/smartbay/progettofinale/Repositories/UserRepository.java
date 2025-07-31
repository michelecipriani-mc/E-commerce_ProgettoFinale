package com.smartbay.progettofinale.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbay.progettofinale.Models.User;

/**
 * Repository per l'entità User.
 * Estende JpaRepository per fornire metodi CRUD, paginazione e ordinamento per
 * gli utenti.
 * 
 * Metodi personalizzati definiti:
 * - findByEmail(String email): restituisce un oggetto User in base all'email
 * fornita.
 * Utile per operazioni di autenticazione o verifica dell'esistenza dell'utente.
 * 
 * - findByUsername(String username): restituisce un Optional<User> in base al
 * nome utente.
 * Utile per ricerche che possono o meno trovare un utente con quel username,
 * evitando NullPointerException grazie all'uso di Optional.
 * 
 * Annotato con @Repository per essere riconosciuto da Spring come componente di
 * accesso ai dati.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    Optional<User> findByUsername(String username);

}
