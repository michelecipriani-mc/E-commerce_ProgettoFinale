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
 * Classe di configurazione per Spring Security.
 * <p>
 * Abilita la sicurezza web e definisce le regole di autorizzazione
 * per gli endpoint dell'applicazione.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Servizio utilizzato per il recupero dei dati dell'utente.
     */
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Encoder per la cifratura e verifica delle password.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Crea e configura la catena di filtri di sicurezza.
     * <p>
     * Sono definite le regole di autorizzazione, il form di login,
     * il processo di logout e la gestione delle sessioni.
     *
     * @param http L'oggetto per la configurazione della sicurezza HTTP.
     * @return La catena di filtri di sicurezza costruita.
     * @throws Exception se si verificano errori di configurazione.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            // CSRF disabilitato
            .csrf(csrf -> csrf.disable()) 

            .authorizeHttpRequests((authorize) -> 
                // Accesso consentito a registrazione e login
                authorize.requestMatchers("/register/**", "/login").permitAll()

                // Risorse statiche accessibili a tutti
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                // Endpoint per i ruoli:
                // -- ADMIN
                .requestMatchers("/admin/dashboard", "/categories/create", "/categories/edit/{id}", "/categories/update/{id}", "/   categories/delete/{id}").hasRole("ADMIN")

                // -- REVISOR
                .requestMatchers("/revisor/dashboard", "/revisor/detail/{id}", "/accept").hasRole("REVISOR")

                // -- SELLER
                .requestMatchers("/seller/dashboard", "/articles/create", "/articles/edit/{id}", "/articles/update/{id}", "/articles/   delete/{id}").hasRole("SELLER")

                // Altri endpoint pubblici e default autenticazione
                .requestMatchers("/register", "/", "/articles",  "/images/**", "/articles/detail/**", "/categories/search/{id}", "/ search/{id}", "/articles/search").permitAll().anyRequest().authenticated())

            // Configurazione del form di login
            .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/").permitAll())

            // URL di logout accessibile a tutti
            .logout(logout -> logout
                .logoutUrl("/logout")
                .permitAll()
            )

            // Pagina di accesso negato
            .exceptionHandling(exception -> exception.accessDeniedPage("/error/403"))

            // Gestione delle sessioni
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).maximumSessions(1).expiredUrl("/login?session-expired=true"));

        return http.build();
    }

    /**
     * Configura il gestore dell'autenticazione globale.
     * <p>
     * Associa il servizio utente personalizzato e l'encoder password.
     *
     * @param auth Il builder per la configurazione dell'autenticazione.
     * @throws Exception se si verificano errori di configurazione.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * Espone l'AuthenticationManager come bean.
     * <p>
     * Utilizzato per l'autenticazione manuale.
     *
     * @param authenticationConfiguration L'oggetto di configurazione dell'autenticazione.
     * @return L'istanza di AuthenticationManager.
     * @throws Exception se si verificano errori.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
}