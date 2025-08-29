package com.smartbay.progettofinale.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Classe di configurazione per Spring MVC.
 * <p>
 * Abilita la registrazione di interceptor e altre personalizzazioni.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer{

    /**
     * Interceptor per la gestione delle notifiche.
     */
    @Autowired
    private NotificationInterceptor notificationInterceptor;

    /**
     * Registra l'interceptor personalizzato nel registry.
     * <p>
     * L'interceptor viene aggiunto alla catena di gestione delle richieste.
     *
     * @param registry Il registry degli interceptor.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(notificationInterceptor);
    }
    
}