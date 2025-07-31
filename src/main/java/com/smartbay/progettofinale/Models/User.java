package com.smartbay.progettofinale.Models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entità JPA che rappresenta un utente dell'applicazione.
 *
 * Attributi:
 * - id: identificativo univoco dell'utente, generato automaticamente.
 * - username: nome utente visibile, obbligatorio.
 * - email: indirizzo email univoco dell’utente, obbligatorio.
 * - password: password dell’utente (di norma cifrata), obbligatoria.
 * - balance: saldo disponibile dell’utente (opzionale), ad esempio per
 * l'acquisto di articoli.
 * - roles: lista dei ruoli associati all’utente (es: ROLE_USER, ROLE_ADMIN,
 * ecc.).
 * Mappata come relazione ManyToMany con la tabella di join `users_roles`.
 *
 * Annotazioni:
 * - @Entity: indica che questa è un'entità persistente JPA.
 * - @Table(name = "users"): specifica il nome della tabella nel database.
 * - @Data: genera automaticamente getter/setter, `equals()`, `hashCode()` e
 * `toString()` (Lombok).
 * - @NoArgsConstructor, @AllArgsConstructor: generano i costruttori senza e con
 * argomenti.
 * - @Id, @GeneratedValue: l’ID è la chiave primaria ed è generata in
 * automatico.
 * - @Column(nullable = false, unique = true): garantisce che email e username
 * siano obbligatori (e l'email univoca).
 * - @ManyToMany: definisce una relazione molti-a-molti tra utenti e ruoli, con
 * fetch EAGER (i ruoli vengono caricati insieme all'utente).
 * La tabella di join si chiama `users_roles`.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private BigDecimal balance;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles", joinColumns = {
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
                    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") })

    private List<Role> roles = new ArrayList<>();

}
