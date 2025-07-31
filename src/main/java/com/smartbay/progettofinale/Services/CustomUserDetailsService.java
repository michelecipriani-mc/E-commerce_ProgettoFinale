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
 * Service che implementa l'interfaccia UserDetailsService di Spring Security.
 * È responsabile del caricamento dei dettagli utente per l'autenticazione.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {
    // Repository per accedere ai dati degli utenti dal database
    @Autowired
    private UserRepository userRepository;

    /**
     * Metodo principale per caricare i dettagli utente dato uno username (email).
     * Viene usato da Spring Security durante il processo di login.
     * 
     * @param username la username (in questo caso email) fornita dall'utente
     * @return un'istanza di CustomUserDetails contenente le informazioni di
     *         sicurezza dell'utente
     * @throws UsernameNotFoundException se l'utente non viene trovato nel database
     */
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            // Se l'utente non esiste, solleva eccezione per invalid credentials
            throw new UsernameNotFoundException("Invalid credentials");
        }
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    /**
     * Mappa la collezione di ruoli (Role) dell'utente in una collezione di
     * GrantedAuthority
     * utilizzata da Spring Security per il controllo degli accessi.
     * 
     * Se l'utente non ha ruoli, assegna un ruolo di default "ROLE_USER".
     * 
     * @param roles lista dei ruoli dell'utente
     * @return lista delle autorità corrispondenti ai ruoli
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<? extends GrantedAuthority> mapRoles = null;
        if (roles.size() != 0) {
            // Converte ogni ruolo in un oggetto SimpleGrantedAuthority
            mapRoles = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());
        } else {
            // Se non ci sono ruoli, assegna il ruolo di default "ROLE_USER"
            mapRoles = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return mapRoles;
    }

}