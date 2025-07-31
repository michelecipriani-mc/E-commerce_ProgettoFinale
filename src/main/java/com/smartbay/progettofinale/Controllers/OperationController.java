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
 * Controller per la gestione delle richieste di carriera (Career Requests).
 * 
 * Funzionalità:
 * - Mostrare il form per inviare una richiesta di ruolo aggiuntivo (REVISOR o
 * SELLER).
 * - Salvare una nuova richiesta.
 * - Visualizzare il dettaglio di una richiesta.
 * - Accettare la richiesta e assegnare il nuovo ruolo all'utente (solo per
 * ADMIN).
 */

@Controller
@RequestMapping("/operations")
public class OperationController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRequestService careerRequestService;

    // Mostra il form per l'invio di una richiesta di carriera
    @GetMapping("/career/request")
    public String careerRequestCreate(Model viewModel) {
        viewModel.addAttribute("title", "Enter your Request");
        viewModel.addAttribute("careerRequest", new CareerRequest());
        // Recupera tutti i ruoli tranne ROLE_USER (ruolo base)
        List<Role> roles = roleRepository.findAll();
        roles.removeIf(e -> e.getName().equals("ROLE_USER"));
        viewModel.addAttribute("roles", roles);

        return "career/requestForm";
    }

    // Salva una richiesta di carriera inviata da un utente
    @PostMapping("/career/request/save")
    public String careerRequestStore(@ModelAttribute("careerRequest") CareerRequest careerRequest, Principal principal,
            RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(principal.getName());
        // Verifica che l'utente non abbia già il ruolo richiesto
        if (careerRequestService.isRoleAlreadyAssigned(user, careerRequest)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Role already assigned");
            return "redirect:/";
        }
        // Salva la richiesta
        careerRequestService.save(careerRequest, user);
        redirectAttributes.addFlashAttribute("successMessage", "Request forwarded successfully");

        return "redirect:/";
    }

    // Mostra il dettaglio di una richiesta di carriera (solo per revisione da parte
    // di un admin)
    @GetMapping("/career/request/detail/{id}")
    public String careerRequestDetail(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Request detail");
        viewModel.addAttribute("request", careerRequestService.find(id));
        return "career/requestDetail";
    }

    // Accetta una richiesta di carriera e assegna il nuovo ruolo all'utente (solo
    // per ADMIN)
    @PostMapping("/career/request/accept/{requestId}")
    public String careerRequestAccept(@PathVariable Long requestId, RedirectAttributes redirectAttributes) {
        careerRequestService.careerAccept(requestId);
        redirectAttributes.addFlashAttribute("successMessage", "User enabled for the requested role!");

        return "redirect:/admin/dashboard";
    }

}