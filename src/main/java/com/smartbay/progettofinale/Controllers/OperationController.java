package com.smartbay.progettofinale.Controllers;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartbay.progettofinale.Models.CareerRequest;
import com.smartbay.progettofinale.Models.Role;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.RoleRepository;
import com.smartbay.progettofinale.Repositories.UserRepository;
import com.smartbay.progettofinale.Services.CareerRequestService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller per la gestione delle operazioni relative alle richieste di
 * carriera.
 * <p>
 * Mappa le richieste web agli endpoint per la creazione, visualizzazione
 * e accettazione delle richieste di ruoli utente.
 */
@Controller
@RequestMapping("/operations")
public class OperationController {

    /**
     * Repository per l'accesso ai dati dei ruoli.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Repository per l'accesso ai dati degli utenti.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Servizio per la logica di business relativa alle richieste di carriera.
     */
    @Autowired
    private CareerRequestService careerRequestService;

    /**
     * Mostra il form per la creazione di una richiesta di carriera.
     * <p>
     * Vengono esclusi i ruoli già assegnati (es. "ROLE_USER").
     *
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista del form di richiesta.
     */
    @GetMapping("/career/request")
    public String careerRequestCreate(Model viewModel) {

        viewModel.addAttribute("title", "Enter your Request");
        viewModel.addAttribute("careerRequest", new CareerRequest());

        List<Role> roles = roleRepository.findAll();

        // Rimuove il ruolo di base "ROLE_USER" dalla lista
        roles.removeIf(e -> e.getName().equals("ROLE_USER"));

        viewModel.addAttribute("roles", roles);

        return "career/requestForm";
    }

    /**
     * Gestisce il salvataggio di una richiesta di carriera.
     * <p>
     * Viene controllato che il ruolo non sia già stato richiesto
     * dall'utente.
     *
     * @param careerRequest      La richiesta di carriera da salvare.
     * @param principal          Oggetto Principal dell'utente corrente.
     * @param redirectAttributes Attributi per il redirect.
     * @return Redirect alla home page.
     */
    @PostMapping("/career/request/save")
    public String careerRequestStore(@ModelAttribute("careerRequest") CareerRequest careerRequest, Principal principal, RedirectAttributes redirectAttributes) {

        User user = userRepository.findByEmail(principal.getName());

        // Controllo se il ruolo è già stato richiesto
        if (careerRequestService.isRoleAlreadyAssigned(user, careerRequest)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Role already assigned");
            return "redirect:/";
        }

        careerRequestService.save(careerRequest, user);
        redirectAttributes.addFlashAttribute("successMessage", "Request forwarded successfully");
        
        return "redirect:/";
    }

    /**
     * Mostra i dettagli di una specifica richiesta di carriera.
     *
     * @param id        L'ID della richiesta.
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista per il dettaglio della richiesta.
     */
    @GetMapping("/career/request/detail/{id}")
    public String careerRequestDetail(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Request detail");
        viewModel.addAttribute("request", careerRequestService.find(id));
        return "career/requestDetail";
    }

    /**
     * Accetta una richiesta di carriera.
     * <p>
     * Viene abilitato l'utente per il ruolo richiesto.
     *
     * @param requestId          L'ID della richiesta da accettare.
     * @param redirectAttributes Attributi per il redirect.
     * @return Redirect alla dashboard dell'admin.
     */
    @PostMapping("/career/request/accept/{requestId}")
    public String careerRequestAccept(@PathVariable Long requestId, RedirectAttributes redirectAttributes) {
        careerRequestService.careerAccept(requestId);
        redirectAttributes.addFlashAttribute("successMessage", "User enabled for the requested role!");
        
        return "redirect:/admin/dashboard";
    }
    
}