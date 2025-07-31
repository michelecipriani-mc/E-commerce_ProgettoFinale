package com.smartbay.progettofinale.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity JPA che rappresenta una richiesta di avanzamento di carriera da parte
 * di un utente,
 * ad esempio per ottenere un ruolo con più responsabilità (es. revisore o
 * amministratore).
 *
 * Attributi:
 * - id: identificativo univoco della richiesta (generato automaticamente)
 * - body: testo della richiesta, dove l’utente può motivare la propria
 * candidatura
 * - isChecked: flag che indica se la richiesta è già stata visionata/valutata
 * da un admin
 *
 * Relazioni:
 * - user: utente che ha effettuato la richiesta (relazione OneToOne)
 * - role: ruolo richiesto (relazione OneToOne), ad esempio "ROLE_REVISOR" o
 * "ROLE_ADMIN"
 *
 * Annotazioni:
 * - @Entity: indica che la classe è una entità JPA
 * - @Table(name = "career_request"): specifica il nome della tabella nel
 * database
 * - @Data e @NoArgsConstructor (Lombok): generano automaticamente getter,
 * setter, costruttore senza argomenti,
 * equals, hashCode, toString, ecc.
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "career_request")
public class CareerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String body;

    @Column
    private Boolean isChecked;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

}
