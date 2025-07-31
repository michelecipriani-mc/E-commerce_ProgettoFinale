package com.smartbay.progettofinale.Models;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entità JPA che rappresenta una categoria alla quale possono appartenere uno o
 * più articoli.
 *
 * Attributi:
 * - id: identificativo univoco della categoria (generato automaticamente)
 * - name: nome della categoria (es. Tecnologia, Cucina, Moda, ecc.)
 *
 * Relazioni:
 * - articles: lista degli articoli associati a questa categoria (relazione
 * OneToMany)
 *
 * Annotazioni:
 * - @Entity: indica che la classe è una entità persistente gestita da JPA
 * - @Table(name = "categories"): specifica il nome della tabella nel database
 * - @Getter, @Setter, @NoArgsConstructor (Lombok): generano automaticamente
 * metodi getter/setter
 * e costruttore vuoto
 * - @Column: definisce le restrizioni sul campo "name" nel database
 * - @NotEmpty e @Size(max = 50): validazioni sul campo "name" per assicurare
 * che non sia vuoto
 * e che rispetti una lunghezza massima
 */

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotEmpty
    @Size(max = 50)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Article> articles = new ArrayList<Article>();

}