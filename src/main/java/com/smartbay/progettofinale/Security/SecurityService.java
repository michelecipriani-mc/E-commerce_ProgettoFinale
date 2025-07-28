package com.smartbay.progettofinale.Security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.UserRepository;

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

  public User getActiveUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    Object principal = authentication.getPrincipal();

    // Se è un oggetto Utente, restituire utente
    if (principal instanceof User utente) {
      return utente;

      // Se è un'altra implementazione di UserDetails, usarla per ottenere username e prendere l'ID dal db
    } else if (principal instanceof UserDetails userDetails) {
      String username = userDetails.getUsername();
      return utenteRepository.findByUsername(username).orElse(null);

      // Nè Utente né altra classe implementante UserDetails: raro, non dovrebbe accadere
    } else if (principal instanceof String username && !principal.equals("anonymousUser")) {
      // Possibilmente tentare di utilizzare lo stesso con 
      // return utenteRepository.findByUsername(username).orElse(null);
      // Oppure lanciare una eccezione

      // Opzionale: gestire un utente anonimo
    } else if (principal instanceof String username && username.equals("anonymousUser")) {
      // possibilmente return null o lanciare una eccezione
      throw new AccessDeniedException("Not authenticated");
    }

    return null;
  }
}