package com.smartbay.progettofinale.Controllers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.smartbay.progettofinale.DTO.ArticleDTO;
import com.smartbay.progettofinale.DTO.OrdineDTO;
import com.smartbay.progettofinale.DTO.UserDTO;
import com.smartbay.progettofinale.Models.Article;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.ArticleRepository;
import com.smartbay.progettofinale.Repositories.CareerRequestRepository;
import com.smartbay.progettofinale.Services.ArticleService;
import com.smartbay.progettofinale.Services.CategoryService;
import com.smartbay.progettofinale.Services.OrdineService;
import com.smartbay.progettofinale.Services.UserService;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller per la gestione delle operazioni utente.
 * <p>
 * Mappa le richieste web agli endpoint per la home page, le dashboard utente,
 * la registrazione, il login e la gestione dei ruoli.
 */
@Controller
public class UserController {

    /**
     * Servizio per la gestione degli utenti.
     */
    @Autowired
    private UserService userService;

    /**
     * Servizio per la gestione degli articoli.
     */
    @Autowired
    private ArticleService articleService;

    /**
     * Repository per l'accesso ai dati degli articoli.
     */
    @Autowired
    private ArticleRepository articleRepository;

    /**
     * Repository per l'accesso ai dati delle richieste di carriera.
     */
    @Autowired
    private CareerRequestRepository careerRequestRepository;

    /**
     * Servizio per la gestione delle categorie.
     */
    @Autowired
    private CategoryService categoryService;

    /**
     * Mapper per la conversione tra DTO e entità.
     */
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Servizio per la gestione degli ordini.
     */
    @Autowired
    private OrdineService ordineService;

    /**
     * Visualizza la home page con gli ultimi tre articoli approvati.
     *
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista "home".
     */
    @GetMapping("/")
    public String home(Model viewModel) {
        List<ArticleDTO> articles = new ArrayList<ArticleDTO>();

        // Recupero degli articoli approvati
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDTO.class));
        }

        // Ordinamento per data di pubblicazione
        Collections.sort(articles, Comparator.comparing(ArticleDTO::getPublishDate).reversed());

        // Limita a 3 articoli
        List<ArticleDTO> lastThreeArticles = articles.stream().limit(3).collect(Collectors.toList());

        viewModel.addAttribute("articles", lastThreeArticles);

        return "home";
    }

    /**
     * Gestisce la richiesta GET per visualizzare le informazioni di un utente specifico.
     *
     * @param id L'identificativo dell'utente da visualizzare.
     * @param viewModel L'oggetto Model usato per passare dati alla vista.
     * @return Il nome del template Thymeleaf da renderizzare, ovvero "user".
     */
    @GetMapping("/userinfo/{id}")
    public String getUserInfo(@PathVariable("id") Long id, Model viewModel) {
        // Recupera le informazioni dell'utente dal servizio e le aggiunge al modello
        viewModel.addAttribute("user", userService.getUserInfo(id));

        return "user"; // Corrisponde al file templates/user.html
    }

    /**
     * Gestisce la richiesta GET per la dashboard dell'utente corrente.
     *
     * @param viewModel L'oggetto Model usato per passare dati alla vista.
     * @return Il nome del template Thymeleaf da renderizzare, ovvero "dashboard".
     */
    @GetMapping("/user/dashboard")
    public String dashboard(Model viewModel) {
        // Aggiunge al modello le informazioni dell'utente necessarie per la dashboard
        viewModel.addAttribute("user", userService.dashboard());

        // Recupera gli ordini dell'utente loggato
        List<OrdineDTO> ordiniUtente = ordineService.getOrdiniUtente(); // o con userId se necessario
        viewModel.addAttribute("ordini", ordiniUtente);

        return "user/dashboard"; // Corrisponde al file templates/user/dashboard.html
    }

    /**
     * Aggiunge un importo al saldo dell'utente.
     *
     * @param amount             L'importo da aggiungere.
     * @param viewModel          Oggetto Model per la vista.
     * @param redirectAttributes Attributi per il redirect.
     * @return Redirect alla dashboard dell'utente.
     */
    @PostMapping("/addbalance")
    public String addBalance(@RequestParam("amount") BigDecimal amount, Model viewModel, 
            RedirectAttributes redirectAttributes) {

        try {
            userService.addBalance(amount);
            redirectAttributes.addFlashAttribute("paymentSuccess", "Balance Updated");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("paymentWarning", ex.getMessage());
        }

        // Aggiunge al modello le informazioni dell'utente necessarie per la dashboard
        viewModel.addAttribute("user", userService.dashboard());

        // Dopo l'esecuzione, redireziona alla dashboard
        return "redirect:/user/dashboard";
    }

    /**
     * Mostra il form di registrazione.
     *
     * @param model Oggetto Model per la vista.
     * @return Nome della vista per il form di registrazione.
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDTO());
        return "auth/register";
    }

    /**
     * Mostra la pagina di login.
     *
     * @return Nome della vista per la pagina di login.
     */
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    /**
     * Gestisce il salvataggio dei dati di registrazione.
     * <p>
     * Se l'email è già registrata o la validazione fallisce, il form viene ricaricato.
     *
     * @param userDto L'oggetto DTO contenente i dati dell'utente.
     * @param result Risultato della validazione.
     * @param model Oggetto Model per la vista.
     * @param redirectAttributes Attributi per il redirect.
     * @param request Oggetto HttpServletRequest.
     * @param response Oggetto HttpServletResponse.
     * @return Redirect alla home page o al form in caso di errore.
     */
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDTO userDto, 
                                BindingResult result, Model model, 
                                RedirectAttributes redirectAttributes, 
                                HttpServletRequest request, HttpServletResponse response) {

        // Controllo se l'utente è già registrato.
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        // In tal caso, rifiutare la richiesta di iscrizione
        if (existingUser != null && 
                existingUser.getEmail() != null && 
                !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already an account registered with the same email");
        }

        // In ogni caso di errore, restituire il form errato
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "auth/register";            
        }

        // In assenza di errori, la registrazione può continuare
        userService.saveUser(userDto, redirectAttributes, request, response);

        redirectAttributes.addFlashAttribute("successMessage", "Registration successful!");

        return "redirect:/";
    }

    @GetMapping("/search/{id}")
    public String userArticlesSearch(@PathVariable("id") Long id, Model viewModel) {
        User user = userService.find(id);
        viewModel.addAttribute("title", "All articles for user " + user.getUsername());

        List<ArticleDTO> articles = articleService.searchByUser(user);
        List<ArticleDTO> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        viewModel.addAttribute("articles", acceptedArticles);
        return "article/articles";
    }

    /**
     * Visualizza la dashboard dell'amministratore.
     *
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista per la dashboard dell'amministratore.
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Request received");

        // Recupero delle richieste di carriera non ancora gestite
        viewModel.addAttribute("requests", careerRequestRepository.findByIsCheckedFalse());

        // Recupero categorie
        viewModel.addAttribute("categories", categoryService.readAll());

        return "admin/dashboard";
    }

    /**
     * Visualizza la dashboard del revisore.
     * <p>
     * Gli articoli da revisionare e quelli già revisionati sono mostrati
     * separatamente.
     *
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista per la dashboard del revisore.
     */
    @GetMapping("/revisor/dashboard")
    public String revisorDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Articles to review");
        viewModel.addAttribute("titleReviewed", "Reviewed articles");

        // Articoli da revisionare (null)
        viewModel.addAttribute("articles", articleRepository.findByIsAcceptedIsNull());

        // Articoli già revisionati (non null)
        viewModel.addAttribute("reviewedArticles", articleRepository.findByIsAcceptedIsNotNull());

        return "revisor/dashboard";
    }
    
    /**
     * Visualizza la dashboard del venditore.
     * <p>
     * Mostra solo gli articoli pubblicati dall'utente corrente.
     *
     * @param viewModel Oggetto Model per la vista.
     * @param principal L'oggetto Principal dell'utente.
     * @return Nome della vista per la dashboard del venditore.
     */
    @GetMapping("/seller/dashboard")
    public String writerDashboard(Model viewModel, Principal principal) {
        viewModel.addAttribute("title", "Your articles");
        List<ArticleDTO> userArticles = articleService.readAll().stream()
                // Filtro degli articoli per mostrare solo quelli dell'utente corrente
                .filter(article -> 
                        article.getUser().getEmail().equals(principal.getName()))
                .toList();

        viewModel.addAttribute("articles", userArticles);  

        return "seller/dashboard";
    }
    
}

