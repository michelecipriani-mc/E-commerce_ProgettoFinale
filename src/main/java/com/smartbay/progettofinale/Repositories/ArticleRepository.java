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
 * Repository per l'entità Article.
 * Estende ListCrudRepository, fornendo le operazioni CRUD basilari per gli
 * articoli (findAll, save, delete, ecc.).
 * 
 * Sono inoltre definiti diversi metodi custom per interrogazioni specifiche
 * basate su convenzioni Spring Data JPA.
 * 
 * Metodi principali:
 * - findByCategory(Category category): restituisce tutti gli articoli
 * appartenenti a una categoria specifica.
 * - findByUser(User user): restituisce tutti gli articoli creati da un
 * determinato utente.
 * - findByIsAcceptedTrue(): articoli approvati (isAccepted = true).
 * - findByIsAcceptedFalse(): articoli rifiutati (isAccepted = false).
 * - findByIsAcceptedIsNull(): articoli ancora da revisionare (isAccepted non
 * settato).
 * - findByIsAcceptedIsNotNull(): articoli che hanno già ricevuto una revisione
 * (accettati o rifiutati).
 * 
 * Query personalizzata:
 * - search(String searchTerm): esegue una ricerca full-text (case-insensitive)
 * su titolo, sottotitolo,
 * nome utente autore e nome categoria. Usa JPQL con `LIKE` e `CONCAT` per
 * simulare una ricerca parziale.
 * 
 * Annotazioni:
 * - @Repository: indica che si tratta di un componente di accesso ai dati
 * gestito da Spring.
 * - @Query: consente di definire una query JPQL personalizzata.
 */

@Repository
public interface ArticleRepository extends ListCrudRepository<Article, Long> {

    List<Article> findByCategory(Category category);

    List<Article> findByUser(User user);

    List<Article> findByIsAcceptedTrue();

    List<Article> findByIsAcceptedFalse();

    List<Article> findByIsAcceptedIsNull();

    List<Article> findByIsAcceptedIsNotNull();

    @Query("SELECT a FROM Article a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.subtitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.user.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Article> search(@Param("searchTerm") String searchTerm);

}
