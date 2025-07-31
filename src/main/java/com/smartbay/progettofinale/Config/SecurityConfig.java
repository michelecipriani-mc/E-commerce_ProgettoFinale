package com.smartbay.progettofinale.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.smartbay.progettofinale.Services.CustomUserDetailsService;

/**
 * Configurazione della sicurezza dell'applicazione tramite Spring Security.
 *
 * - Definisce i permessi di accesso alle rotte in base ai ruoli utente (ADMIN,
 * REVISOR, SELLER).
 * - Configura le rotte pubbliche accessibili anche agli utenti non autenticati.
 * - Gestisce il login, il logout, la protezione CSRF, la gestione delle
 * sessioni e la pagina di accesso negato.
 * - Inietta il servizio personalizzato di autenticazione degli utenti.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disabilita la protezione CSRF (solo se necessario, es. per semplicità nei
                // test)
                .csrf(csrf -> csrf.disable())
                // Autorizzazioni basate su ruolo e percorso
                .authorizeHttpRequests((authorize) ->
                // Rotte accessibili a tutti (pubbliche)
                authorize.requestMatchers("/register/**", "/login").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // Rotte riservate agli utenti con ruolo ADMIN
                        .requestMatchers("/admin/dashboard", "/categories/create", "/categories/edit/{id}",
                                "/categories/update/{id}", "/categories/delete/{id}")
                        .hasRole("ADMIN")
                        // Rotte riservate ai revisori (REVISOR)
                        .requestMatchers("/revisor/dashboard", "/revisor/detail/{id}", "/accept").hasRole("REVISOR")
                        // Rotte riservate ai venditori (SELLER)
                        .requestMatchers("/seller/dashboard", "/articles/create", "/articles/edit/{id}",
                                "/articles/update/{id}", "/articles/delete/{id}")
                        .hasRole("SELLER")
                        // Altre rotte pubbliche
                        .requestMatchers("/register", "/", "/articles", "/images/**", "/articles/detail/**",
                                "/categories/search/{id}", "/search/{id}", "/articles/search")
                        .permitAll().anyRequest().authenticated())
                // Configura la pagina di login
                .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/")
                        .permitAll())
                // Configura il logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll())
                // Gestione degli errori di accesso negato
                .exceptionHandling(exception -> exception.accessDeniedPage("/error/403"))
                // Gestione della sessione utente
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1).expiredUrl("/login?session-expired=true"));
        return http.build();
    }

    /**
     * Configura il sistema di autenticazione con:
     * - Servizio custom che carica i dettagli dell'utente da database
     * - Password encoder per la verifica delle credenziali
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * Espone un AuthenticationManager come bean per l'iniezione in altri
     * componenti.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}