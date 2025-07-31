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
 * Implementazione del servizio per la gestione delle richieste di carriera
 * (CareerRequest).
 * Contiene la logica per verificare l'assegnazione dei ruoli, salvare
 * richieste,
 * accettare richieste e notificare gli utenti tramite email.
 */

@Service
public class CareerRequestServiceImpl implements CareerRequestService {

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Controlla se un ruolo specifico è già assegnato all'utente.
     * Viene controllato se l'utente ha già fatto richieste per quel ruolo.
     * 
     * @param user          l'utente da verificare
     * @param careerRequest la richiesta di carriera contenente il ruolo da
     *                      controllare
     * @return true se il ruolo è già assegnato (o richiesto), false altrimenti
     */

    @Transactional
    public boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest) {
        List<Long> allUserIds = careerRequestRepository.findAllUserIds();
        // Se l'utente non ha richieste, ritorna false
        if (!allUserIds.contains(user.getId())) {
            return false;
        }
        // Recupera i ruoli richiesti dall'utente e verifica se quello specifico è
        // presente
        List<Long> request = careerRequestRepository.findByUserId(user.getId());
        return request.stream().anyMatch(roleId -> roleId.equals(careerRequest.getRole().getId()));
    }

    /**
     * Salva una nuova richiesta di carriera associata a un utente e imposta lo
     * stato come non verificato.
     * Invia una mail di notifica all'amministratore per la nuova richiesta.
     * 
     * @param careerRequest la richiesta di carriera da salvare
     * @param user          l'utente che effettua la richiesta
     */
    public void save(CareerRequest careerRequest, User user) {
        careerRequest.setUser(user);
        careerRequest.setIsChecked(false);
        careerRequestRepository.save(careerRequest);
        // Invia notifica email all'admin
        emailService.sendSimpleEmail("admin@smartbay.com", "Request for the role: " + careerRequest.getRole().getName(),
                "new request for collaboration from" + user.getUsername());
    }

    /**
     * Accetta una richiesta di carriera identificata dall'id.
     * Aggiunge il ruolo richiesto all'utente, aggiorna lo stato della richiesta e
     * invia notifica via email all'utente.
     * 
     * @param requestId l'id della richiesta di carriera da accettare
     */
    @Override
    public void careerAccept(Long requestId) {
        CareerRequest request = careerRequestRepository.findById(requestId).get();

        User user = request.getUser();
        Role role = request.getRole();
        // Aggiunge il nuovo ruolo all'utente
        List<Role> rolesUser = user.getRoles();
        Role newRole = roleRepository.findByName(role.getName());
        rolesUser.add(newRole);

        user.setRoles(rolesUser);
        userRepository.save(user);
        // Segna la richiesta come verificata
        request.setIsChecked(true);
        careerRequestRepository.save(request);
        // Invia email di conferma all'utente
        emailService.sendSimpleEmail(user.getEmail(), "Role enabled",
                "Hello, we inform you that our administration has accepted your request for collaboration... Welcome!");
    }

    /**
     * Recupera una richiesta di carriera tramite il suo id.
     * 
     * @param id l'id della richiesta da trovare
     * @return la richiesta corrispondente (lancia eccezione se non trovata)
     */
    @Override
    public CareerRequest find(Long id) {
        return careerRequestRepository.findById(id).get();
    }

}