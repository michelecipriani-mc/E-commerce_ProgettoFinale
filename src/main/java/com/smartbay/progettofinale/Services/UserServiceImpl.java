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

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Metodo factory per il PasswordEncoder, usa BCrypt per l'hashing sicuro delle
     * password.
     * 
     * @return nuova istanza di BCryptPasswordEncoder
     */
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cerca un utente tramite email.
     * 
     * @param email email dell'utente da cercare
     * @return entità User trovata o null se non esiste
     */

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Salva un nuovo utente a partire dai dati ricevuti nel DTO.
     * Effettua l'hashing della password, assegna il ruolo di default ("ROLE_USER"),
     * inizializza il bilancio a zero se non presente,
     * salva l'utente nel database e autentica l'utente nella sessione HTTP.
     * 
     * @param userDto            dati dell'utente da salvare
     * @param redirectAttributes per messaggi di redirect (non usato qui)
     * @param request            richiesta HTTP, usata per creare la sessione
     * @param response           risposta HTTP (non usata qui)
     */

    @Override
    public void saveUser(UserDTO userDto, RedirectAttributes redirectAttributes, HttpServletRequest request,
            HttpServletResponse response) {
        User user = new User();
        user.setUsername(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));// Hash della password

        Role role = roleRepository.findByName("ROLE_USER");// Assegna ruolo di base ROLE_USER
        user.setRoles(List.of(role));
        // Inizializza balance a 0 se null
        if (user.getBalance() == null) {
            user.setBalance(BigDecimal.valueOf(0L));
        }

        userRepository.save(user);// Salvataggio utente nel DB
        authenticateUserAndSetSession(user, userDto, request); // Autenticazione e impostazione della sessione Spring
                                                               // Security
    }

    /**
     * Metodo helper privato che effettua l'autenticazione dell'utente appena creato
     * e salva il contesto di sicurezza nella sessione HTTP.
     * 
     * @param user    utente appena creato
     * @param userDto DTO contenente le credenziali in chiaro
     * @param request richiesta HTTP per accedere alla sessione
     */
    private void authenticateUserAndSetSession(User user, UserDTO userDto, HttpServletRequest request) {
        try {
            // Carica dettagli utente tramite email
            CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
            // Crea token di autenticazione con username e password non ancora criptata
            // (passata dal DTO)
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(), userDto.getPassword());
            // Autentica il token tramite AuthenticationManager di Spring Security
            Authentication authentication = authenticationManager.authenticate(authToken);
            // Imposta il contesto di sicurezza nella SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Ottiene o crea la sessione HTTP e salva il contesto di sicurezza
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        } catch (AuthenticationException e) {
            // Stampa la stack trace in caso di errore di autenticazione
            e.printStackTrace();
        }
    }

    /**
     * Restituisce le informazioni personali dell'utente attualmente autenticato.
     * 
     * Questo metodo viene utilizzato per popolare la dashboard dell'utente. Il DTO
     * risultante
     * contiene informazioni sensibili come il saldo, ed è pensato solo per l'utente
     * attivo.
     *
     * @return Un oggetto {@link PersonalInfoDTO} contenente le informazioni
     *         personali dell'utente
     *         attivo.
     */
    @Override
    public PersonalInfoDTO dashboard() {

        // Ottieni l'utente attualmente autenticato tramite il SecurityService
        User user = securityService.getActiveUser();

        // Converte l'entità User in PersonalInfoDTO (include saldo e altri dati
        // sensibili)
        return modelMapper.map(user, PersonalInfoDTO.class);
    }

    @Transactional
    public void addBalance(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.valueOf(0)) <= 0) {
            throw new IllegalArgumentException("Cannot add negative balance.");
        }

        // Obtain active user from SecurityContext
        User user = securityService.getActiveUser();

        if (user == null) {
            throw new IllegalStateException("Cannot add balance as anonymous user.");
        }

        // Obtain JPA-managed user instance for persistence (automatic with
        // @Transactional)
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Avoid null values
        BigDecimal balance = managedUser.getBalance();
        if (balance == null) {
            balance = BigDecimal.valueOf(0L);
        }

        // Add balance for customer
        managedUser.setBalance(managedUser.getBalance().add(amount));
    }

    /**
     * Restituisce le informazioni pubbliche di un utente specificato tramite ID.
     * 
     * Questo metodo viene utilizzato, ad esempio, per visualizzare il profilo
     * pubblico di un altro
     * utente. Non include dati sensibili come il saldo.
     *
     * @param id L'identificativo dell'utente di cui recuperare le informazioni.
     * @return Un oggetto {@link UserInfoDTO} con le informazioni pubbliche
     *         dell'utente.
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

    @Override
    public User find(Long id) {
        return userRepository.findById(id).get();
    }

}
