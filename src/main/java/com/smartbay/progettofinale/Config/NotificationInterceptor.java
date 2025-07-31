package com.smartbay.progettofinale.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.smartbay.progettofinale.Repositories.ArticleRepository;
import com.smartbay.progettofinale.Repositories.CareerRequestRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor che aggiunge al ModelAndView le notifiche per l'amministrazione e
 * la revisione.
 *
 * Viene eseguito dopo il completamento del controller, ma prima del rendering
 * della vista.
 * 
 * - Se l'utente ha ruolo ROLE_ADMIN: aggiunge il numero di richieste di
 * carriera non ancora verificate.
 * - Se l'utente ha ruolo ROLE_REVISOR: aggiunge il numero di articoli da
 * revisionare (isAccepted = null).
 * 
 * Questi dati vengono usati, ad esempio, per mostrare badge di notifica nella
 * dashboard o nella barra di navigazione.
 */
@Component
public class NotificationInterceptor implements HandlerInterceptor {

    @Autowired
    CareerRequestRepository careerRequestRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // Controlla che la vista non sia nulla e che l'utente sia un amministratore
        if (modelAndView != null && request.isUserInRole("ROLE_ADMIN")) {
            // Conta le richieste di carriera non ancora controllate
            int careerCount = careerRequestRepository.findByIsCheckedFalse().size();
            // Aggiunge il conteggio al model per mostrarlo nella vista
            modelAndView.addObject("careerRequests", careerCount);
        }
        // Controlla che la vista non sia nulla e che l'utente sia un revisore
        if (modelAndView != null && request.isUserInRole("ROLE_REVISOR")) {
            // Conta gli articoli che devono ancora essere revisionati
            int revisedCount = articleRepository.findByIsAcceptedIsNull().size();
            // Aggiunge il conteggio al model per mostrarlo nella vista
            modelAndView.addObject("articlesToBeRevised", revisedCount);
        }
    }

}