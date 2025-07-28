package com.smartbay.progettofinale.Services;

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

@Service
public class UserServiceImpl implements UserService{

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

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUser(UserDTO userDto, RedirectAttributes redirectAttributes, HttpServletRequest request,
            HttpServletResponse response) {
        User user = new User();
        user.setUsername(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER");
        user.setRoles(List.of(role));

        userRepository.save(user);
        authenticateUserAndSetSession(user, userDto, request);       
    }

    private void authenticateUserAndSetSession(User user, UserDTO userDto, HttpServletRequest request) {
        try {
            CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDto.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restituisce le informazioni personali dell'utente attualmente autenticato.
     * 
     * Questo metodo viene utilizzato per popolare la dashboard dell'utente. Il DTO risultante
     * contiene informazioni sensibili come il saldo, ed è pensato solo per l'utente attivo.
     *
     * @return Un oggetto {@link PersonalInfoDTO} contenente le informazioni personali dell'utente
     *         attivo.
     */
    @Override
    public PersonalInfoDTO dashboard() {

        // Ottieni l'utente attualmente autenticato tramite il SecurityService
        User user = securityService.getActiveUser();

        // Converte l'entità User in PersonalInfoDTO (include saldo e altri dati sensibili)
        return modelMapper.map(user, PersonalInfoDTO.class);
    }

    /**
     * Restituisce le informazioni pubbliche di un utente specificato tramite ID.
     * 
     * Questo metodo viene utilizzato, ad esempio, per visualizzare il profilo pubblico di un altro
     * utente. Non include dati sensibili come il saldo.
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

    @Override
    public User find(Long id) {
        return userRepository.findById(id).get();
    }

}

