package com.smartbay.progettofinale.Models;

import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entità JPA che rappresenta un ruolo all'interno dell'applicazione (es:
 * ROLE_USER, ROLE_ADMIN, ROLE_REVISOR, ecc.).
 *
 * Attributi:
 * - id: identificativo univoco del ruolo, generato automaticamente.
 * - name: nome del ruolo, deve essere univoco (es. "ROLE_ADMIN") e non nullo.
 * - users: lista degli utenti che possiedono questo ruolo (relazione ManyToMany
 * inversa rispetto a User).
 *
 * Annotazioni:
 * - @Entity: indica che la classe è un'entità JPA.
 * - @Table(name = "roles"): specifica il nome della tabella nel database.
 * - @Data, @NoArgsConstructor, @AllArgsConstructor: annotazioni Lombok per
 * generare boilerplate code (getter, setter, costruttori, ecc.).
 * - @Id, @GeneratedValue: l'ID è la chiave primaria ed è generato
 * automaticamente.
 * - @Column(nullable = false, unique = true): il campo `name` è obbligatorio e
 * deve essere univoco.
 * - @ManyToMany(mappedBy = "roles"): relazione molti-a-molti con la classe
 * `User`, definita in modo inverso (cioè la gestione è dalla parte della classe
 * `User`).
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

}
