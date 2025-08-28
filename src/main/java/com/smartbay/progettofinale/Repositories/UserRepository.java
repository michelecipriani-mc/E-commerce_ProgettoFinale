package com.smartbay.progettofinale.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbay.progettofinale.Models.User;

/** Repository per l'accesso e la gestione dei dati dell'entità User. */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    
    /** Trova un utente in base al suo indirizzo email. */
    User findByEmail(String email);

    /** Trova un utente in base al suo nome utente. */
    Optional<User> findByUsername(String username);
    
}
