package com.smartbay.progettofinale.Services;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.smartbay.progettofinale.Models.Role;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.UserRepository;

/**
 * Servizio per la gestione dei dettagli utente in Spring Security.
 * <p>
 * Implementa l'interfaccia UserDetailsService per caricare i dettagli
 * dell'utente
 * dal database durante il processo di autenticazione.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService{
    //inseriamo le DI

    /** Repository per l'accesso ai dati dell'utente */
    @Autowired
    private UserRepository userRepository;

    /**
     * Carica i dettagli dell'utente per l'autenticazione.
     * <p>
     * Chiamato da Spring Security per trovare un utente tramite la sua email 
     * (che funge da nome utente).
     *
     * @param username L'email dell'utente.
     * @return Un'istanza di CustomUserDetails con i dettagli dell'utente.
     * @throws UsernameNotFoundException Se l'utente non viene trovato.
     */
    @Override
    // Questo metodo viene chiamato automaticamente durante il login
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Cerchiamo l'utente nel database tramite email (usata come username)
        User user = userRepository.findByEmail(username);
        // Se l'utente non viene trovato, lanciamo l'eccezione prevista da Spring Security
        if (user == null) {
            throw new UsernameNotFoundException("Invalid credentials");
        }
        // Se l'utente esiste, creiamo e restituiamo un oggetto CustomUserDetails
        // che Spring Security userà per autenticazione e autorizzazione
        return new CustomUserDetails(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            mapRolesToAuthorities(user.getRoles())
        );
    }
    
    /**
     * Mappa i ruoli ruoli del database in oggetti GrantedAuthority.
     *
     * @param roles La collezione di ruoli dell'utente.
     * @return Una collezione di autorizzazioni.
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<? extends GrantedAuthority> mapRoles = null;
        // Se l'utente ha dei ruoli, li trasformiamo in SimpleGrantedAuthority
        // usando stream e raccogliendoli in una lista
        if (roles.size() != 0) {
            mapRoles = roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
        // Se non ci sono ruoli, assegniamo di default il ruolo "ROLE_USER"
        }else{
            mapRoles = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        //ritorniamo tutti i ruoli mappati
        return mapRoles;
    }
    
}