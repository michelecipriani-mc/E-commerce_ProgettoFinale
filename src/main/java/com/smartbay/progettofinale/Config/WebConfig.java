package com.smartbay.progettofinale.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configurazione MVC personalizzata per l'applicazione.
 *
 * Registra l'interceptor NotificationInterceptor, che aggiunge al ModelAndView
 * le notifiche relative a richieste di carriera e articoli da revisionare,
 * in base al ruolo dell'utente loggato (ADMIN o REVISOR).
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private NotificationInterceptor notificationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registra l'interceptor per tutte le richieste gestite da Spring MVC
        registry.addInterceptor(notificationInterceptor);
    }

}