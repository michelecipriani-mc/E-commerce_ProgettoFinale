package com.smartbay.progettofinale.Services;

import java.math.BigDecimal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.smartbay.progettofinale.DTO.PersonalInfoDTO;
import com.smartbay.progettofinale.DTO.UserDTO;
import com.smartbay.progettofinale.DTO.UserInfoDTO;
import com.smartbay.progettofinale.Models.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Interfaccia per il servizio di gestione degli utenti. */
public interface UserService {

    /**
     * Salva un nuovo utente nel sistema dopo averlo validato.
     *
     * @param userDto            L'oggetto DTO contenente i dati dell'utente.
     * @param redirectAttributes Attributi di reindirizzamento per messaggi di
     *                           stato.
     * @param request            La richiesta HTTP corrente.
     * @param response           La risposta HTTP corrente.
     */
    void saveUser(UserDTO userDto, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response);

    /**
     * Trova un utente tramite il suo indirizzo email.
     *
     * @param email L'indirizzo email dell'utente.
     * @return L'oggetto User trovato.
     */
    User findUserByEmail(String email);

    /**
     * Trova un utente tramite il suo ID.
     *
     * @param id L'ID dell'utente.
     * @return L'oggetto User trovato.
     */
    User find(Long id);

    /**
     * Prepara i dati per la dashboard dell'utente.
     *
     * @return Un oggetto PersonalInfoDTO con le informazioni personali dell'utente.
     */
    PersonalInfoDTO dashboard();

    /**
     * Ottiene le informazioni dettagliate di un utente.
     *
     * @param id L'ID dell'utente.
     * @return Un oggetto UserInfoDTO con i dettagli dell'utente.
     */
    UserInfoDTO getUserInfo(Long id);

    /**
     * Aggiunge un importo al saldo dell'utente attualmente autenticato.
     *
     * @param amount L'importo da aggiungere.
     */
    void addBalance(BigDecimal amount);
}
