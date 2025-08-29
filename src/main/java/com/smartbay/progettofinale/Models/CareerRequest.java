package com.smartbay.progettofinale.Models;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entità di persistenza per le richieste di carriera.
 * <p>
 * Mappa una richiesta di ruolo utente alla tabella "career_request" nel
 * database.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "career_request", uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "role_id"})})
public class CareerRequest {

    /** Identificativo univoco della richiesta. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Il corpo del messaggio della richiesta. */
    @Column(nullable = false, length = 1000)
    private String body;

    /** Indica se la richiesta è stata gestita. */
    @Column
    private Boolean isChecked;

    /** L'utente che ha inviato la richiesta. */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** Il ruolo richiesto. */
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    
}
