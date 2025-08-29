package com.smartbay.progettofinale.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbay.progettofinale.Models.Role;

/** Repository per l'accesso e la gestione dei dati dell'entità Role. */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

    /** Trova un ruolo in base al suo nome. */
    Role findByName(String name);
    
}
