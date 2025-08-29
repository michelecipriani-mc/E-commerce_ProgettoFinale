package com.smartbay.progettofinale.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.smartbay.progettofinale.Models.CareerRequest;
import com.smartbay.progettofinale.Models.Role;
import com.smartbay.progettofinale.Models.User;

/**
 * Repository per l'accesso e la gestione dei dati dell'entità CareerRequest.
 */
@Repository
public interface CareerRequestRepository extends CrudRepository<CareerRequest, Long>{

    /**
     * Trova tutte le richieste di carriera che non sono state ancora gestite.
     */
    List<CareerRequest> findByIsCheckedFalse();

    /**
     * Trova gli ID di tutti gli utenti con un ruolo assegnato.
     * <p>
     * Query nativa che accede alla tabella di join "users_roles", che gestisce la
     * relazione molti-a-molti tra utenti e ruoli.
     *
     * @return Una lista di ID di utenti.
     */
    @Query(value = "SELECT user_id FROM users_roles", nativeQuery = true)
    List<Long> findAllUserIds();

    /**
     * Trova tutti gli ID dei ruoli associati a un utente specifico.
     * <p>
     * Query nativa che accede alla tabella di join "users_roles", che gestisce la
     * relazione molti-a-molti tra utenti e ruoli.
     *
     * @param id L'ID dell'utente.
     * @return Una lista di ID di ruoli.
     */
    @Query(value = "SELECT role_id FROM users_roles WHERE user_id = :id", nativeQuery = true)
    List<Long> findByUserId(@Param("id") Long id);

    /**
     * Trova una richiesta di carriera per un utente e un ruolo specifici, se non è
     * stata ancora gestita.
     */
    Optional<CareerRequest> findByUserAndRoleAndIsCheckedFalse(User user, Role role);

}