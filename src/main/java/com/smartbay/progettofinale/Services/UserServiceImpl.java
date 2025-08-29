package com.smartbay.progettofinale.Services;

import java.math.BigDecimal;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.smartbay.progettofinale.DTO.PersonalInfoDTO;
import com.smartbay.progettofinale.DTO.UserDTO;
import com.smartbay.progettofinale.DTO.UserInfoDTO;
import com.smartbay.progettofinale.Models.Role;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.RoleRepository;
import com.smartbay.progettofinale.Repositories.UserRepository;
import com.smartbay.progettofinale.Security.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

/**
 * Implementazione del servizio di gestione degli utenti.
 * <p>
 * Fornisce la logica di business per le operazioni sugli utenti come
 * registrazione,
 * autenticazione, aggiornamento del saldo e recupero dei dati.
 */
@Service
public class UserServiceImpl implements UserService{

    /** Repository per l'accesso ai dati degli utenti */
    @Autowired
    private UserRepository userRepository;

    /** Repository per l'accesso ai dati dei ruoli */
    @Autowired
    private RoleRepository roleRepository;

    /** Codificatore per le password */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /** ervizio per caricare i dettagli utente */
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /** Gestore dell'autenticazione di Spring Security */
    @Autowired
    private AuthenticationManager authenticationManager;

    /** Servizio per ottenere l'utente autenticato */
    @Autowired
    private SecurityService securityService;

    /** Mapper per la conversione tra entità e DTO */
    @Autowired
    private ModelMapper modelMapper;

    /** Istanza del codificatore della password */
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /** Trova un utente tramite la sua email */
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Salva un nuovo utente nel database e lo autentica automaticamente.
     *
     * @param userDto            L'oggetto DTO contenente i dati dell'utente.
     * @param redirectAttributes Attributi per messaggi di reindirizzamento.
     * @param request            La richiesta HTTP corrente.
     * @param response           La risposta HTTP corrente.
     */
    @Override
    public void saveUser(UserDTO userDto, RedirectAttributes redirectAttributes, HttpServletRequest request,
            HttpServletResponse response) {
        User user = new User();

        // Combina nome e cognome
        user.setUsername(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        // Codifica la password
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Assegna il ruolo di default all'utente
        Role role = roleRepository.findByName("ROLE_USER");
        user.setRoles(List.of(role));

        // Imposta il saldo iniziale
        if (user.getBalance() == null) {
            user.setBalance(BigDecimal.valueOf(0L));
        }

        // Salva l'utente nel database
        userRepository.save(user);

        // Autentica l'utente e crea una sessione
        authenticateUserAndSetSession(user, userDto, request);       
    }

    /**
     * Autentica un utente e crea una sessione.
     *
     * @param user    L'entità dell'utente.
     * @param userDto L'oggetto DTO dell'utente.
     * @param request La richiesta HTTP corrente.
     */
    private void authenticateUserAndSetSession(User user, UserDTO userDto, HttpServletRequest request) {
        try {
            // Carica i dettagli utente
            CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
            // Crea un token di autenticazione
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDto.getPassword());
            // Autentica il token
            Authentication authentication = authenticationManager.authenticate(authToken);
            // Imposta l'autenticazione nel contesto di sicurezza
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Ottiene la sessione HTTP e imposta il contesto di sicurezza
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restituisce le informazioni personali dell'utente attualmente autenticato.
     * <p>
     * Questo metodo viene utilizzato per popolare la dashboard dell'utente. Il DTO risultante
     * contiene informazioni sensibili come il saldo, ed è pensato solo per l'utente attivo.
     *
     * @return Un oggetto {@link PersonalInfoDTO} contenente le informazioni personali 
     * dell'utente attivo.
     */
    @Override
    public PersonalInfoDTO dashboard() {

        // Ottieni l'utente attualmente autenticato tramite il SecurityService
        User user = securityService.getActiveUser();

        // Converte l'entità User in PersonalInfoDTO (include saldo e altri dati sensibili)
        return modelMapper.map(user, PersonalInfoDTO.class);
    }

    /**
     * Aggiunge un importo al saldo dell'utente.
     *
     * @param amount L'importo da aggiungere.
     */
    @Transactional
    public void addBalance(BigDecimal amount) {

        // Lancia un'eccezione se l'importo è negativo
        if (amount.compareTo(BigDecimal.valueOf(0)) <= 0) {
            throw new IllegalArgumentException("Cannot add negative balance.");
        }

        // Ottiene l'utente autenticato
        User user = securityService.getActiveUser();

        // Lancia un'eccezione se l'utente non è autenticato
        if (user == null) {
            throw new IllegalStateException("Cannot add balance as anonymous user.");
        }

        // Recupera l'oggetto utente gestito da JPA per la persistenza 
        // (automatico con @Transactional)
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Ottiene il saldo. Inizializza se nullo
        BigDecimal balance = managedUser.getBalance();
        if (balance == null) {
            balance = BigDecimal.valueOf(0L);
        }

        // Aggiorna il saldo
        managedUser.setBalance(managedUser.getBalance().add(amount));
    }

    /**
     * Restituisce le informazioni pubbliche di un utente specificato tramite ID.
     * <p>
     * Questo metodo viene utilizzato, ad esempio, per visualizzare il profilo pubblico
     * di un altro utente. Non include dati sensibili come il saldo.
     *
     * @param id L'identificativo dell'utente di cui recuperare le informazioni.
     * @return Un oggetto {@link UserInfoDTO} con le informazioni pubbliche dell'utente.
     * @throws RuntimeException se l'utente con l'ID specificato non esiste.
     */
    @Override
    public UserInfoDTO getUserInfo(Long id) {

        // Recupera l'utente dal repository, lancia un'eccezione se non trovato
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato."));

        // Converte l'entità User in UserInfoDTO (esclude dati sensibili)
        return modelMapper.map(user, UserInfoDTO.class);
    }

    /** Trova un utente tramite il suo ID. */
    @Override
    public User find(Long id) {
        return userRepository.findById(id).get();
    }

}

