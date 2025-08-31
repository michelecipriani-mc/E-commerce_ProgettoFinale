package com.smartbay.progettofinale.Services;
import java.security.Principal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaccia generica per i servizi che gestiscono operazioni CRUD.
 * <p>
 * Questa interfaccia definisce un contratto standard per la creazione, lettura,
 * aggiornamento ed eliminazione di entità, utilizzando DTO per la lettura.
 *
 * @param <ReadDto> Il tipo di DTO per la lettura dei dati.
 * @param <Model>   Il tipo dell'entità di dominio.
 * @param <Key>     Il tipo della chiave primaria.
 */
public interface CrudService<ReadDto, Model, Key> {

    /**
     * Legge tutti gli elementi e li restituisce come una lista di DTO.
     *
     * @return Una lista di DTO.
     */
    List<ReadDto> readAll();

    /**
     * Legge un singolo elemento in base alla sua chiave primaria.
     *
     * @param key La chiave primaria dell'elemento.
     * @return Il DTO dell'elemento trovato.
     */
    ReadDto read(Key key);

    /**
     * Crea un nuovo elemento.
     *
     * @param model     L'entità da creare.
     * @param principal L'utente che sta eseguendo l'operazione.
     * @param files     Files da associare all'elemento (opzionale).
     * @return Il DTO dell'elemento creato.
     */
    ReadDto create(Model model, Principal principal, MultipartFile[] files);

    /**
     * Aggiorna un elemento esistente in base alla sua chiave primaria.
     *
     * @param key    La chiave primaria dell'elemento da aggiornare.
     * @param model  L'entità con i nuovi dati.
     * @param files  Files da aggiornare (opzionale).
     * @return Il DTO dell'elemento aggiornato.
     */
    ReadDto update(Key key, Model model, MultipartFile[] files);

    /**
     * Elimina un elemento in base alla sua chiave primaria.
     *
     * @param key La chiave primaria dell'elemento da eliminare.
     */
    void delete(Key key);

}
