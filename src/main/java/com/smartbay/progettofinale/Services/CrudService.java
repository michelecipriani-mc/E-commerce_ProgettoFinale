package com.smartbay.progettofinale.Services;

import java.security.Principal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaccia generica per i servizi CRUD (Create, Read, Update, Delete).
 * 
 * @param <ReadDto> tipo del Data Transfer Object (DTO) restituito nelle
 *                  operazioni di lettura
 * @param <Model>   tipo dell'entità o modello di dominio su cui operare
 * @param <Key>     tipo della chiave primaria utilizzata per identificare
 *                  un'entità
 */

public interface CrudService<ReadDto, Model, Key> {
    /**
     * Recupera tutte le entità presenti.
     * 
     * @return lista di DTO rappresentanti tutte le entità
     */
    List<ReadDto> readAll();

    /**
     * Recupera una singola entità tramite la sua chiave primaria.
     * 
     * @param key chiave primaria dell'entità da leggere
     * @return DTO rappresentante l'entità trovata
     */
    ReadDto read(Key key);

    /**
     * Crea una nuova entità nel sistema.
     * 
     * @param model     entità o modello con i dati da salvare
     * @param principal utente autenticato che effettua la richiesta (opzionale)
     * @param file      file multipart eventualmente associato alla creazione
     *                  (opzionale)
     * @return DTO rappresentante l'entità creata
     */
    ReadDto create(Model model, Principal principal, MultipartFile file);

    /**
     * Aggiorna un'entità esistente identificata dalla chiave primaria.
     * 
     * @param key   chiave primaria dell'entità da aggiornare
     * @param model entità o modello con i nuovi dati
     * @param file  file multipart eventualmente associato all'aggiornamento
     *              (opzionale)
     * @return DTO rappresentante l'entità aggiornata
     */
    ReadDto update(Key key, Model model, MultipartFile file);

    /**
     * Elimina un'entità dal sistema tramite la sua chiave primaria.
     * 
     * @param key chiave primaria dell'entità da eliminare
     */
    void delete(Key key);

}
