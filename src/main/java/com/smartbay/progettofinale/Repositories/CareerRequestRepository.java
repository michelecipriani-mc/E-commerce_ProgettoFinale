package com.smartbay.progettofinale.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smartbay.progettofinale.Models.CareerRequest;

/**
 * Repository per l'entità CareerRequest.
 * Estende CrudRepository, fornendo operazioni CRUD di base (findById, save,
 * delete, ecc.).
 *
 * Contiene metodi personalizzati per gestire richieste di candidatura a ruoli
 * specifici da parte degli utenti.
 *
 * Metodi personalizzati:
 * - findByIsCheckedFalse(): restituisce tutte le richieste di carriera che non
 * sono ancora state esaminate.
 *
 * Query native:
 * - findAllUserIds(): restituisce tutti gli ID utente presenti nella tabella di
 * relazione `users_roles`.
 * Utile per recuperare gli utenti che hanno almeno un ruolo assegnato.
 *
 * - findByUserId(Long id): restituisce tutti gli ID dei ruoli associati a uno
 * specifico utente.
 * Serve per sapere quali ruoli ha già un determinato utente (probabilmente per
 * evitare assegnazioni duplicate).
 *
 * Annotazioni:
 * - @Repository: indica che si tratta di un componente di accesso ai dati
 * gestito da Spring.
 * - @Query con nativeQuery = true: viene utilizzato SQL nativo per interrogare
 * direttamente la tabella `users_roles`.
 */

@Repository
public interface CareerRequestRepository extends CrudRepository<CareerRequest, Long> {

    List<CareerRequest> findByIsCheckedFalse();

    @Query(value = "SELECT user_id FROM users_roles", nativeQuery = true)
    List<Long> findAllUserIds();

    @Query(value = "SELECT role_id FROM users_roles WHERE user_id = :id", nativeQuery = true)
    List<Long> findByUserId(@Param("id") Long id);
}