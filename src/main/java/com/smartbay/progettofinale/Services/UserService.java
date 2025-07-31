package com.smartbay.progettofinale.Services;

import java.math.BigDecimal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.smartbay.progettofinale.DTO.PersonalInfoDTO;
import com.smartbay.progettofinale.DTO.UserDTO;
import com.smartbay.progettofinale.DTO.UserInfoDTO;
import com.smartbay.progettofinale.Models.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Salva un nuovo utente a partire da un DTO.
 * Può gestire redirect e interazioni HTTP (es. cookie o sessioni) tramite
 * RedirectAttributes, HttpServletRequest e HttpServletResponse.
 * 
 * @param userDto            dati dell'utente da salvare
 * @param redirectAttributes per gestire messaggi di redirect o attributi flash
 * @param request            richiesta HTTP (utile per sessione, cookie, ecc.)
 * @param response           risposta HTTP (utile per impostare cookie, header,
 *                           ecc.)
 */

public interface UserService {
    void saveUser(UserDTO userDto, RedirectAttributes redirectAttributes, HttpServletRequest request,
            HttpServletResponse response);

    /**
     * Cerca e restituisce un utente tramite la sua email.
     * 
     * @param email email dell'utente da cercare
     * @return l'entità User corrispondente o null se non trovata
     */
    User findUserByEmail(String email);

    /**
     * Cerca e restituisce un utente tramite il suo id.
     * 
     * @param id identificativo dell'utente
     * @return entità User corrispondente
     */
    User find(Long id);

    /**
     * Recupera le informazioni personali dell'utente attualmente autenticato
     * per visualizzarle nella dashboard personale.
     * 
     * @return PersonalInfoDTO con i dati personali da mostrare in dashboard
     */
    PersonalInfoDTO dashboard();

    /**
     * Ottiene le informazioni di un utente dato il suo id.
     * 
     * @param id identificativo dell'utente
     * @return UserInfoDTO con i dettagli dell'utente
     */
    UserInfoDTO getUserInfo(Long id);

    /**
     * Aggiunge una certa somma di denaro al bilancio (balance) dell'utente attivo.
     * 
     * @param amount importo da aggiungere al saldo
     */
    void addBalance(BigDecimal amount);
}
