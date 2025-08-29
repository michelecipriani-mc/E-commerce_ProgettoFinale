package com.smartbay.progettofinale.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartbay.progettofinale.Models.CareerRequest;
import com.smartbay.progettofinale.Models.Role;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.CareerRequestRepository;
import com.smartbay.progettofinale.Repositories.RoleRepository;
import com.smartbay.progettofinale.Repositories.UserRepository;

/**
 * Implementazione del servizio per la gestione delle richieste di carriera.
 * <p>
 * Contiene la logica di business per il salvataggio, la verifica e
 * l'accettazione delle richieste.
 */
@Service
public class CareerRequestServiceImpl implements CareerRequestService{


    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    /**
     * Verifica se un ruolo è già assegnato a un utente o se esiste una richiesta
     * aperta.
     */
    @Transactional
    public boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest) {
        // Controlla se il ruolo è già stato assegnato
        List<Long> request = careerRequestRepository.findByUserId(user.getId());
        boolean isRoleAlreadyAssigned = request.contains(careerRequest.getRole().getId());
        // Controlla se esiste già una richiesta aperta per lo stesso ruolo
        boolean isRoleAlreadyRequested = careerRequestRepository
            .findByUserAndRoleAndIsCheckedFalse(user, careerRequest.getRole())
            .isPresent();

        return isRoleAlreadyAssigned || isRoleAlreadyRequested;
    }

    /**
     * Salva una nuova richiesta di carriera nel database e invia una notifica
     * email.
     */
    public void save(CareerRequest careerRequest, User user) {
        // Imposta l'utente e lo stato della richiesta
        careerRequest.setUser(user);
        careerRequest.setIsChecked(false);
        careerRequestRepository.save(careerRequest);

        // Invia notifica all'amministratore
        emailService.sendSimpleEmail("admin@smartbay.com", "Request for the role: " + careerRequest.getRole().getName(), " new request for collaboration from " + user.getUsername());
    }

    /**
     * Accetta una richiesta di carriera, assegnando il ruolo all'utente e inviando
     * una notifica.
     */
    @Override
    public void careerAccept(Long requestId) {
        // Trova la richiesta
        CareerRequest request = careerRequestRepository.findById(requestId).get();

        User user = request.getUser();
        Role role = request.getRole();

        // Aggiunge il ruolo all'utente
        List<Role> rolesUser = user.getRoles();
        Role newRole = roleRepository.findByName(role.getName());
        rolesUser.add(newRole);

        // Salva le modifiche all'utente e alla richiesta
        user.setRoles(rolesUser);
        userRepository.save(user);
        request.setIsChecked(true);
        careerRequestRepository.save(request);

        // Invia notifica all'utente
        emailService.sendSimpleEmail(user.getEmail(), "Role enabled", "Hello, we inform you that our administration has accepted your request for collaboration... Welcome!");
    }

    /** Trova una richiesta di carriera per ID. */
    @Override
    public CareerRequest find(Long id) {
        return careerRequestRepository.findById(id).get();
    }
}