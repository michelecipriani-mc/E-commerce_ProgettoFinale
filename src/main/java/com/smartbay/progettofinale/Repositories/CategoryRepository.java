package com.smartbay.progettofinale.Repositories;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import com.smartbay.progettofinale.Models.Category;

/**
 * Repository per l'entità Category.
 * Estende ListCrudRepository, che fornisce operazioni CRUD di base (create,
 * read, update, delete)
 * e supporta il ritorno dei risultati come liste (invece di Iterable).
 *
 * In questa versione non sono definiti metodi personalizzati, ma si eredita
 * tutta la funzionalità
 * standard offerta da ListCrudRepository, ad esempio:
 * - findAll()
 * - findById(Long id)
 * - save(Category category)
 * - deleteById(Long id)
 *
 * Annotazione:
 * - @Repository: indica che questa interfaccia è un componente Spring che
 * accede ai dati,
 * permettendo l'iniezione automatica e la gestione delle eccezioni legate al
 * database.
 */

@Repository
public interface CategoryRepository extends ListCrudRepository<Category, Long> {

}