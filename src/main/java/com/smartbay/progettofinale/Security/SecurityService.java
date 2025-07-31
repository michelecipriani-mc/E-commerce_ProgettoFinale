package com.smartbay.progettofinale.Security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.UserRepository;
import com.smartbay.progettofinale.Services.CustomUserDetails;

@Service
public class SecurityService {

    private final UserRepository utenteRepository;

    public SecurityService(UserRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public Long getActiveUserId() {
        User utente = getActiveUser();
        return utente != null ? utente.getId() : null;
    }

    /**
     * Restituisce l'utente attualmente autenticato nel contesto di sicurezza.
     *
     * <p>
     * Se l'utente è anonimo (ad esempio, non loggato ma in un contesto pubblico),
     * restituisce
     * {--@code null}. In caso di problemi con l'autenticazione (assenza di
     * contesto, oggetto
     * {--@code principal} non valido, ecc.), viene lanciata un'eccezione per
     * facilitare il debugging
     * e impedire comportamenti imprevisti.
     * </p>
     *
     *@return L'entità {--@code User} corrispondente all'utente autenticato, oppure
     *         {--@code null} se
     *         anonimo.
     * @throws IllegalStateException se il contesto di sicurezza è mal configurato o
     *                               corrotto.
     */
    public User getActiveUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifica che l'autenticazione esista
        if (auth == null) {
            throw new IllegalStateException(
                    "Authentication è null. Il contesto di sicurezza non è inizializzato correttamente.");
        }

        // Utente anonimo: restituisce null (accesso pubblico)
        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        // Utente non autenticato, ma non è anonimo: comportamento sospetto
        if (!auth.isAuthenticated()) {
            throw new IllegalStateException(
                    "Utente non autenticato ma non anonimo. Possibile configurazione errata.");
        }

        Object principal = auth.getPrincipal();

        if (principal == null) {
            throw new IllegalStateException(
                    "Il principal è null. Autenticazione corrotta o non inizializzata.");
        }

        // Se il principal è direttamente un'entità User
        if (principal instanceof User user) {
            return user;

            // Se il principal è un CustomUserDetails, usa l'ID per recuperare l'entità User
        } else if (principal instanceof CustomUserDetails customUserDetails) {
            return utenteRepository.findById(customUserDetails.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Errore di autenticazione: l'ID del CustomUserDetails non corrisponde a nessun utente."));

            // Se è un'altra implementazione di UserDetails, usa lo username
        } else if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            return utenteRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException(
                            "Errore di autenticazione: username da UserDetails non trovato nel database."));

            // Se è una stringa diversa da "anonymousUser", prova a cercare per username
        } else if (principal instanceof String username && !username.equals("anonymousUser")) {
            return utenteRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Principal \"" + username
                            + "\" non corrisponde ad alcun utente nel sistema."));
        }

        // Caso eccezionale: tipo di principal sconosciuto
        throw new IllegalStateException(
                "Tipo di principal non gestito: " + principal.getClass().getName());
    }
}
