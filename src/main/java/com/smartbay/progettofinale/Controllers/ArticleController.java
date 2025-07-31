package com.smartbay.progettofinale.Controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.smartbay.progettofinale.DTO.AggiornaArticoloCarrelloRequest;
import com.smartbay.progettofinale.DTO.ArticleDTO;
import com.smartbay.progettofinale.DTO.CategoryDTO;
import com.smartbay.progettofinale.Models.Article;
import com.smartbay.progettofinale.Models.Category;
import com.smartbay.progettofinale.Repositories.ArticleRepository;
import com.smartbay.progettofinale.Services.ArticleService;
import com.smartbay.progettofinale.Services.CrudService;
import com.smartbay.progettofinale.Services.ImageService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller che gestisce tutte le operazioni relative agli articoli.
 * 
 * Rotte principali:
 * - Visualizzazione articoli pubblici (/articles)
 * - Creazione, modifica ed eliminazione articoli (ruolo SELLER)
 * - Revisione articoli (ruolo REVISOR)
 * - Ricerca articoli pubblicati
 *
 * Utilizza i servizi ArticleService, ImageService e CategoryService per
 * separare la logica dal controller.
 */

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    @Qualifier("categoryService")
    private CrudService<CategoryDTO, Category, Long> categoryService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ImageService imageService;

    // Mostra tutti gli articoli accettati pubblicamente visibili
    @GetMapping
    public String articlesIndex(Model viewModel) {
        viewModel.addAttribute("title", "All Article");
        List<ArticleDTO> articles = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDTO.class));
        }
        // Ordina gli articoli dalla data più recente alla più vecchia
        Collections.sort(articles, Comparator.comparing(ArticleDTO::getPublishDate).reversed());
        viewModel.addAttribute("articles", articles);
        return "article/articles";
    }

    // Mostra il form di creazione di un nuovo articolo (accessibile da SELLER)
    @GetMapping("create")
    public String articleCreate(Model viewModel) {
        viewModel.addAttribute("title", "Create Article");
        viewModel.addAttribute("article", new Article());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/create";
    }

    // Gestisce il salvataggio di un nuovo articolo
    @PostMapping
    public String articlesStore(@Valid @ModelAttribute("article") Article article,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Principal principal,
            @RequestParam("file") MultipartFile file,
            Model viewModel) {
        if (result.hasErrors()) {
            // Ricarica il form in caso di errori di validazione
            viewModel.addAttribute("title", "Create an Article");
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/create";
        }
        // Imposta lo stato come "da revisionare"
        article.setIsAccepted(null);
        articleService.create(article, principal, file); // logica immagine gestita nel service
        redirectAttributes.addFlashAttribute("successMessage", "Article added and awaiting review");

        return "redirect:/";
    }

    // Mostra i dettagli pubblici di un articolo
    @GetMapping("detail/{id}")
    public String detailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Article detail");
        viewModel.addAttribute("article", articleService.read(id));
        return "article/detail";
    }

    // Mostra il form per modificare un articolo esistente (accessibile da SELLER)
    @GetMapping("/edit/{id}")
    public String editArticle(@PathVariable("id") Long id, Model viewModel) {
        ArticleDTO articleDto = articleService.read(id);
        if (articleDto != null) {
            viewModel.addAttribute("title", "Article update");
            viewModel.addAttribute("article", modelMapper.map(articleDto, Article.class));
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/edit";
        } else {
            return "redirect:/seller/dashboard";
        }
    }

    // Gestisce l'aggiornamento di un articolo
    @PostMapping("/update/{id}")
    public String articleUpdate(@PathVariable("id") Long id,
            @Valid @ModelAttribute("article") Article article,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Principal principal,
            @RequestParam("file") MultipartFile file,
            Model viewModel) {
        if (result.hasErrors()) {
            // In caso di errore, ricarica il form con i valori già inseriti
            viewModel.addAttribute("title", "Article update");
            article.setImage(articleService.read(id).getImage());
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/edit";// Se l'articolo era già stato accettato, lo imposta come "da revisionare"
        }

        ArticleDTO existingArticleDto = articleService.read(id);
        Article existingArticle = modelMapper.map(existingArticleDto, Article.class);

        if (existingArticle != null && Boolean.TRUE.equals(existingArticle.getIsAccepted())) {
            article.setIsAccepted(false);
            redirectAttributes.addFlashAttribute("warningMessage",
                    "This article has been modified and needs re-approval.");
        } else {
            article.setIsAccepted(existingArticle.getIsAccepted());
            redirectAttributes.addFlashAttribute("successMessage", "Article successfully edited!");
        }

        articleService.update(id, article, file); // file gestito nel service
        return "redirect:/seller/dashboard";
    }

    // Elimina un articolo (solo per SELLER)
    @GetMapping("/delete/{id}")
    public String articleDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Article successfully deleted!");
        return "redirect:/seller/dashboard";
    }

    // Dashboard del revisore: articoli da revisionare e già revisionati
    @GetMapping("/revisor/dashboard")
    public String revisorDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Article to review");
        List<ArticleDTO> articlesToReview = new ArrayList<>();
        for (Article article : articleRepository.findByIsAcceptedIsNull()) {
            articlesToReview.add(modelMapper.map(article, ArticleDTO.class));
        }
        viewModel.addAttribute("articles", articlesToReview);
        viewModel.addAttribute("titleReviewed", "Reviewed articles");
        List<ArticleDTO> reviewedArticles = new ArrayList<>();
        List<Article> acceptedAndRejected = articleRepository.findByIsAcceptedIsNotNull();
        for (Article article : acceptedAndRejected) {
            reviewedArticles.add(modelMapper.map(article, ArticleDTO.class));
        }
        viewModel.addAttribute("reviewedArticles", reviewedArticles);

        return "revisor/dashboard";
    }

    // Dettaglio di un articolo specifico per la revisione
    @GetMapping("revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Article detail");
        viewModel.addAttribute("article", articleService.read(id));
        return "revisor/detail";
    }

    // Revisore accetta o rifiuta un articolo
    @PostMapping("/accept")
    public String articleSetAccepted(@RequestParam("action") String action, @RequestParam("articleId") Long articleId,
            RedirectAttributes redirectAttributes) {
        if (action.equals("accept")) {
            articleService.setIsAccepted(true, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Article accepted!");
        } else if (action.equals("reject")) {
            articleService.setIsAccepted(false, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Article rejected!");
        } else {
            redirectAttributes.addFlashAttribute("resultMessage", "Incorrect action...");
        }

        return "redirect:/articles/revisor/dashboard";
    }

    // Ricerca articoli pubblicati per parola chiave
    @GetMapping("/search")
    public String articleSearch(@Param("keyword") String keyword, Model viewModel) {
        viewModel.addAttribute("title", "All articles found");
        List<ArticleDTO> articles = articleService.search(keyword);
        List<ArticleDTO> acceptedArticles = articles.stream()
                .filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        viewModel.addAttribute("articles", acceptedArticles);
        return "article/articles";
    }

}