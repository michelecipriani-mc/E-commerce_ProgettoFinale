package com.smartbay.progettofinale.Models;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entità di persistenza per i ruoli utente.
 * <p>
 * Mappa un ruolo alla tabella "roles" nel database.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    /** Identificativo univoco del ruolo. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Nome del ruolo (es. "ROLE_ADMIN", "ROLE_USER"). */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Lista di utenti che hanno questo ruolo.
     * <p>
     * Relazione molti-a-molti.
     */
    @ManyToMany(mappedBy = "roles")
    private List<User> users;
    
    /**
     * Collegamento con le richieste di carriera associate a questo ruolo.
     * <p>
     * Relazione uno-a-molti.
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<CareerRequest> careerRequests = new ArrayList<>();
}
