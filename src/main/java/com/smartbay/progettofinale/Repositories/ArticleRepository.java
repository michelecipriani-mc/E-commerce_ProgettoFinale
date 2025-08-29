package com.smartbay.progettofinale.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smartbay.progettofinale.Models.Article;
import com.smartbay.progettofinale.Models.Category;
import com.smartbay.progettofinale.Models.User;

/**
 * Repository per l'accesso e la gestione dei dati dell'entità Article.
 */
@Repository
public interface ArticleRepository extends ListCrudRepository<Article, Long>{

    /** rova tutti gli articoli appartenenti a una specifica categoria. */
    List<Article> findByCategory(Category category);

    /** Trova tutti gli articoli scritti da un utente specifico. */
    List<Article> findByUser(User user);

    /** Trova tutti gli articoli che sono stati approvati. */
    List<Article> findByIsAcceptedTrue();

    /** Trova tutti gli articoli che non sono stati approvati. */
    List<Article> findByIsAcceptedFalse();

    /** Trova tutti gli articoli in attesa di revisione. */
    List<Article> findByIsAcceptedIsNull();

    /** Trova tutti gli articoli che sono stati già revisionati. */
    List<Article> findByIsAcceptedIsNotNull();

    /**
     * Esegue una ricerca di articoli basata su un termine.
     * <p>
     * La ricerca viene eseguita su titolo, sottotitolo, nome utente e nome categoria.
     *
     * @param searchTerm Il termine di ricerca.
     * @return Una lista di articoli che corrispondono al termine di ricerca.
     */
    @Query("SELECT a FROM Article a WHERE " + 
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " + 
            "LOWER(a.subtitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " + 
            "LOWER(a.user.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " + 
            "LOWER(a.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Article> search(@Param("searchTerm") String searchTerm);

}
