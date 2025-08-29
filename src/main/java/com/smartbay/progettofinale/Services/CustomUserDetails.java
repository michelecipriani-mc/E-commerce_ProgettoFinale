package com.smartbay.progettofinale.Services;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Implementazione personalizzata dell'interfaccia UserDetails di Spring
 * Security.
 * <p>
 * Questa classe agisce da ponte tra l'entità utente del database e i requisiti
 * di Spring Security per l'autenticazione e l'autorizzazione.
 */
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails{

    /** L'ID dell'utente nel database */
    private Long id;

    /** Il nome completo dell'utente */
    private String username;

    /** L'email dell'utente, usata per l'autenticazione */
    private String email;

    /** La password dell'utente */
    private String password;

    /** La lista delle autorizzazioni (ruoli) dell'utente */
    private Collection<? extends GrantedAuthority> authorities;

    /** Indica se l'account dell'utente è scaduto. */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** Indica se l'account dell'utente è bloccato. */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** Indica se le credenziali dell'utente sono scadute. */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** Indica se l'utente è abilitato. */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /** Restituisce le autorizzazioni (ruoli) assegnate all'utente. */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /** Restituisce la password utilizzata per l'autenticazione. */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Restituisce il nome utente utilizzato per l'autenticazione.
     * In questo caso, viene utilizzata l'email.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /** Restituisce l'ID dell'utente. */
    public Long getId() {
        return id;
    }

    /** Imposta l'ID dell'utente. */
    public void setId(Long id) {
        this.id = id;
    }

    /** Restituisce il nome completo dell'utente. */
    public String getFullname() {
        return username;
    }
     
}
