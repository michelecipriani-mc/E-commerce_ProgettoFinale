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
 * Interceptor per l'aggiunta di notifiche al modello della vista.
 * <p>
 * Intercetta le richieste web per inserire nel modello
 * dati specifici a seconda del ruolo dell'utente autenticato,
 * come il numero di richieste da gestire o articoli da revisionare.
 */
@Component
public class NotificationInterceptor implements HandlerInterceptor{

    /**
     * Repository per le richieste di carriera
     */
    @Autowired
    CareerRequestRepository careerRequestRepository;

    /**
     * Repository per gli articoli
     */
    @Autowired
    ArticleRepository articleRepository;

    /**
     * Aggiunge il conteggio delle notifiche al modello.
     * <p>
     * Chiamato dopo che il controller ha gestito la richiesta e prima della renderizzazione della vista.
     * 
     * Conta richieste di carriera non gestite per gli ADMIN e articoli da
     * revisionare per i REVISOR.
     *
     * @param request      L'oggetto {@link HttpServletRequest} per la richiesta
     *                     corrente.
     * @param response     L'oggetto {@link HttpServletResponse} per la risposta.
     * @param handler      L'handler (controller) che ha gestito la richiesta.
     * @param modelAndView L'oggetto {@link ModelAndView} che contiene il modello e
     *                     il nome della vista.
     * @throws Exception Se si verificano errori durante l'elaborazione.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        // Se l'utente è ADMIN, contare il numero di richieste carriera aperte
        if (modelAndView != null && request.isUserInRole("ROLE_ADMIN")) {
            int careerCount = careerRequestRepository.findByIsCheckedFalse().size();
            modelAndView.addObject("careerRequests", careerCount);            
        }

        // Se l'utente è REVISOR, contare il numero di articoli da revisionare
        if (modelAndView != null && request.isUserInRole("ROLE_REVISOR")) {
            int revisedCount = articleRepository.findByIsAcceptedIsNull().size();
            modelAndView.addObject("articlesToBeRevised", revisedCount);            
        }
    }
    
}