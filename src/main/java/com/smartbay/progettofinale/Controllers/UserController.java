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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartbay.progettofinale.DTO.ArticleDTO;
import com.smartbay.progettofinale.DTO.UserDTO;
import com.smartbay.progettofinale.Models.Article;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.ArticleRepository;
import com.smartbay.progettofinale.Repositories.CareerRequestRepository;
import com.smartbay.progettofinale.Services.ArticleService;
import com.smartbay.progettofinale.Services.CategoryService;
import com.smartbay.progettofinale.Services.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private CategoryService categoryService;

        @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/")
    public String home(Model viewModel) {
        List<ArticleDTO> articles = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDTO.class));
        }
        Collections.sort(articles, Comparator.comparing(ArticleDTO::getPublishDate).reversed());
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

        return "user/dashboard"; // Corrisponde al file templates/user/dashboard.html
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDTO());
        return "auth/register";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDTO userDto, 
                                BindingResult result, Model model, 
                                RedirectAttributes redirectAttributes, 
                                HttpServletRequest request, HttpServletResponse response) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());
        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "auth/register";            
        }

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

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Request received");
        viewModel.addAttribute("requests", careerRequestRepository.findByIsCheckedFalse());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "admin/dashboard";
    }

    @GetMapping("/revisor/dashboard")
    public String revisorDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Articles to review");
        viewModel.addAttribute("articles", articleRepository.findByIsAcceptedIsNull());
        return "revisor/dashboard";
    }

    @GetMapping("/seller/dashboard")
    public String writerDashboard(Model viewModel, Principal principal) {
        viewModel.addAttribute("title", "Your articles");
        List<ArticleDTO> userArticles =articleService.readAll().stream().filter(article -> article.getUser().getEmail().equals(principal.getName())).toList();
        viewModel.addAttribute("articles", userArticles);  
        return "seller/dashboard";
    }
    
}

