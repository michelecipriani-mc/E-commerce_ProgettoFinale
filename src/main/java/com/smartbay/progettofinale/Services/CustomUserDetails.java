package com.smartbay.progettofinale.Services;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Implementazione personalizzata dell'interfaccia UserDetails di Spring
 * Security.
 * Rappresenta i dettagli dell'utente autenticato nel contesto di sicurezza.
 * 
 * Utilizza le annotazioni Lombok @AllArgsConstructor e @NoArgsConstructor per
 * generare
 * automaticamente costruttori con e senza argomenti.
 */

@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Long id;// Identificatore univoco dell'utente
    private String username; // Nome utente o nome completo dell'utente
    private String email;// Email dell'utente, usata come username per l'autenticazione
    private String password;// Password cifrata dell'utente
    private Collection<? extends GrantedAuthority> authorities;// Collezione delle autorità (ruoli/permissoni) assegnate
                                                               // all'utente

    /**
     * Indica se l'account dell'utente non è scaduto.
     * 
     * @return sempre true (l'account non scade mai in questa implementazione)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica se l'account dell'utente non è bloccato.
     * 
     * @return sempre true (l'account non viene mai bloccato in questa
     *         implementazione)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica se le credenziali dell'utente non sono scadute.
     * 
     * @return sempre true (le credenziali non scadono mai in questa
     *         implementazione)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se l'utente è abilitato.
     * 
     * @return sempre true (l'utente è sempre abilitato in questa implementazione)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Restituisce le autorità (ruoli/permessi) assegnate all'utente.
     * 
     * @return collection di GrantedAuthority
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Restituisce la password cifrata dell'utente.
     * 
     * @return password
     */
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Restituisce l'identificativo dell'utente.
     * 
     * @return id utente
     */
    public Long getId() {
        return id;
    }

    /**
     * Imposta l'identificativo dell'utente.
     * 
     * @param id id utente
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Restituisce il nome completo dell'utente.
     * 
     * @return username (nome completo)
     */
    public String getFullname() {
        return username;
    }

}
