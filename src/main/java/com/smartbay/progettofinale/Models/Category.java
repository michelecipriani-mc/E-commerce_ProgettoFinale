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
 * Entità di persistenza per le categorie.
 * <p>
 * Mappa i dati di una categoria alla tabella "categories" nel database.
 */
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    /** Identificativo univoco della categoria. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome della categoria. */
    @Column(nullable = false, length = 100)
    @NotEmpty
    @Size(max = 50)
    private String name;

    /**
     * Lista di articoli associati a questa categoria.
     * <p>
     * Relazione uno-a-molti: una categoria può avere più articoli.
     */
    @OneToMany(mappedBy = "category")
    private List<Article> articles = new ArrayList<Article>();
    
}