package com.smartbay.progettofinale.Services;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
//creazione della classe CustomUserDetail
public class CustomUserDetails implements UserDetails{
    //Attributi della classe
    private Long id;
    private String username;
    private String email;
    private String password;
    // Collezione di ruoli/authorities dell'utente
    private Collection<? extends GrantedAuthority> authorities;

    // I seguenti metodi determinano lo stato dell'account
    @Override
    public boolean isAccountNonExpired() {
        return true; // l'account non scade mai
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // l'account non viene mai bloccato
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // le credenziali non scadono mai
    }

    @Override
    public boolean isEnabled() {
        return true; // l'account è sempre abilitato
    }

    // Restituisce la collezione di ruoli/authorities dell'utente
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    //Getter e Setter
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return username;
    }
     
}
