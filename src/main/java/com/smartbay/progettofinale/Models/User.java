package com.smartbay.progettofinale.Models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entità di persistenza per gli utenti dell'applicazione.
 * <p>
 * Mappa i dati di un utente alla tabella "users" nel database, gestendo
 * anche le relazioni con ruoli e richieste di carriera.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    /** Identificativo univoco dell'utente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome utente. */
    @Column(nullable = false)
    private String username;

    /** Indirizzo email, deve essere univoco. */
    @Column(nullable = false, unique = true)
    private String email;

    /** Password dell'utente. */
    @Column(nullable = false)
    private String password;

    /** Saldo dell'utente. */
    @Column(nullable = true)
    private BigDecimal balance;

    /**
     * Lista di ruoli associati all'utente.
     * <p>
     * Relazione molti-a-molti con l'entità Role.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
        inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")}
    )

    private List<Role> roles = new ArrayList<>();

    /**
     * Collegamento con le richieste di carriera inviate dall'utente.
     * <p>
     * Relazione uno-a-molti con l'entità CareerRequest.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CareerRequest> careerRequests = new ArrayList<>();
    
}
