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
 * Controller per la gestione delle operazioni sugli articoli.
 * <p>
 * Mappa le richieste web agli endpoint per la visualizzazione, creazione,
 * modifica, eliminazione e revisione degli articoli.
 */
@Controller
@RequestMapping("/articles")
public class ArticleController {

    /**
     * Servizio per la gestione delle categorie.
     */
    @Autowired
    @Qualifier("categoryService")
    private CrudService<CategoryDTO, Category, Long> categoryService;

    /**
     * Servizio per la logica di business relativa agli articoli.
     */
    @Autowired
    private ArticleService articleService;

    /**
     * Repository per l'accesso ai dati degli articoli.
     */
    @Autowired
    private ArticleRepository articleRepository;

    /**
     * Mapper per la conversione tra DTO e entità.
     */
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Servizio per la gestione delle immagini.
     */
    @Autowired
    private ImageService imageService;

    /**
     * Visualizza la lista degli articoli approvati.
     * <p>
     * Gli articoli vengono recuperati e ordinati per data di pubblicazione.
     *
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista per la pagina degli articoli.
     */
    @GetMapping
    public String articlesIndex(Model viewModel) {
        viewModel.addAttribute("title", "All Article");
        List<ArticleDTO> articles = new ArrayList<ArticleDTO>();

        // Recupero degli articoli approvati dal database
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDTO.class));
        }

        // Ordinamento degli articoli per data di pubblicazione in ordine decrescente
        Collections.sort(articles, Comparator.comparing(ArticleDTO::getPublishDate).reversed());
        viewModel.addAttribute("articles", articles);

        return "article/articles";
    }
    
    /**
     * Mostra il form per la creazione di un nuovo articolo.
     *
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista del form di creazione.
     */
    @GetMapping("create")
    public String articleCreate(Model viewModel) {
        viewModel.addAttribute("title", "Create Article");
        viewModel.addAttribute("article", new Article());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/create";
    }
    
    /**
     * Gestisce il salvataggio di un nuovo articolo.
     * <p>
     * Se la validazione fallisce, il form viene ricaricato. Altrimenti, l'articolo
     * viene salvato con stato in attesa di revisione.
     *
     * @param article            L'articolo da salvare.
     * @param result             Risultato della validazione.
     * @param redirectAttributes Attributi per il redirect.
     * @param principal          Oggetto Principal dell'utente corrente.
     * @param file               Il file immagine caricato.
     * @param viewModel          Oggetto Model per la vista.
     * @return Redirect alla home o al form in caso di errore.
     */
    @PostMapping
    public String articlesStore(@Valid @ModelAttribute("article") Article article,
                            BindingResult result,
                            RedirectAttributes redirectAttributes,
                            Principal principal,
                            @RequestParam("file") MultipartFile file,
                            Model viewModel) {
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Create an Article");
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll()); 
            return "article/create";          
        }

        // Impostazione stato articolo in attesa di revisione
        article.setIsAccepted(null);
        articleService.create(article, principal, file); // logica immagine gestita nel service
        redirectAttributes.addFlashAttribute("successMessage", "Article added and awaiting review");

        return "redirect:/";
    }

    /**
     * Mostra la pagina di dettaglio di un articolo.
     *
     * @param id        L'ID dell'articolo.
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista del dettaglio articolo.
     */
    @GetMapping("detail/{id}")
    public String detailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Article detail");
        viewModel.addAttribute("article", articleService.read(id));
        return "article/detail";  
    }

    /**
     * Mostra il form per la modifica di un articolo.
     *
     * @param id        L'ID dell'articolo da modificare.
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista del form di modifica o redirect.
     */
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

    /**
     * Gestisce l'aggiornamento di un articolo esistente.
     * <p>
     * Dopo la modifica, se l'articolo era approvato, il suo stato
     * viene resettato per richiedere una nuova approvazione.
     *
     * @param id                 L'ID dell'articolo da aggiornare.
     * @param article            I dati aggiornati dell'articolo.
     * @param result             Risultato della validazione.
     * @param redirectAttributes Attributi per il redirect.
     * @param principal          Oggetto Principal dell'utente corrente.
     * @param file               Il nuovo file immagine.
     * @param viewModel          Oggetto Model per la vista.
     * @return Redirect alla dashboard del venditore o al form in caso di errore.
     */
    @PostMapping("/update/{id}")
    public String articleUpdate(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("article") Article article,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Principal principal,
                                @RequestParam("file") MultipartFile file,
                                Model viewModel) {
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Article update");
            article.setImage(articleService.read(id).getImage());
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/edit";
        }

        ArticleDTO existingArticleDto = articleService.read(id);
        Article existingArticle = modelMapper.map(existingArticleDto, Article.class);

        if (existingArticle != null && Boolean.TRUE.equals(existingArticle.getIsAccepted())) {

            // Se un articolo approvato viene modificato, deve essere ri-approvato
            article.setIsAccepted(false); 
            redirectAttributes.addFlashAttribute("warningMessage", "This article has been modified and needs re-approval.");
        } else {
            article.setIsAccepted(existingArticle.getIsAccepted()); 
            redirectAttributes.addFlashAttribute("successMessage", "Article successfully edited!");
        }

        articleService.update(id, article, file); // file gestito nel service
        return "redirect:/seller/dashboard";
    }

    /**
     * Elimina un articolo.
     *
     * @param id                 L'ID dell'articolo da eliminare.
     * @param redirectAttributes Attributi per il redirect.
     * @return Redirect alla dashboard del venditore.
     */
    @GetMapping("/delete/{id}")
    public String articleDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Article successfully deleted!");
        return "redirect:/seller/dashboard";
    }

    /**
     * Visualizza la dashboard del revisore con gli articoli da revisionare.
     * <p>
     * Gli articoli vengono suddivisi in "da revisionare" (stato null) e
     * "già revisionati" (stato true o false).
     *
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista della dashboard del revisore.
     */
    @GetMapping("/revisor/dashboard")
    public String revisorDashboard(Model viewModel) {
        viewModel.addAttribute("title", "Article to review");
        List<ArticleDTO> articlesToReview = new ArrayList<>();

        // Recupero degli articoli in attesa di revisione
        for (Article article : articleRepository.findByIsAcceptedIsNull()) {
            articlesToReview.add(modelMapper.map(article, ArticleDTO.class));
        }
        viewModel.addAttribute("articles", articlesToReview); 
        viewModel.addAttribute("titleReviewed", "Reviewed articles");
        List<ArticleDTO> reviewedArticles = new ArrayList<>();

        // Recupero degli articoli già revisionati
        List<Article> acceptedAndRejected = articleRepository.findByIsAcceptedIsNotNull();
        for (Article article : acceptedAndRejected) {
            reviewedArticles.add(modelMapper.map(article, ArticleDTO.class));
        }
        viewModel.addAttribute("reviewedArticles", reviewedArticles);
    
        return "revisor/dashboard";
    }
    
    /**
     * Mostra la pagina di dettaglio di un articolo per il revisore.
     *
     * @param id        L'ID dell'articolo.
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista del dettaglio articolo per il revisore.
     */
    @GetMapping("revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Article detail");
        viewModel.addAttribute("article", articleService.read(id));
        return "revisor/detail";  
    }
    
    /**
     * Gestisce l'approvazione o il rifiuto di un articolo da parte di un revisore.
     * <p>
     * Aggiorna lo stato di accettazione dell'articolo in base all'azione
     * ricevuta.
     *
     * @param action             Azione da eseguire ("accept" o "reject").
     * @param articleId          L'ID dell'articolo da revisionare.
     * @param redirectAttributes Attributi per il redirect.
     * @return Redirect alla dashboard del revisore.
     */
    @PostMapping("/accept")
    public String articleSetAccepted(@RequestParam("action") String action, @RequestParam("articleId") Long articleId, RedirectAttributes redirectAttributes) {

        // Articolo approvato
        if (action.equals("accept")) {
            articleService.setIsAccepted(true, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Article accepted!");

        // Articolo rifiutato
        } else if (action.equals("reject")) {
            articleService.setIsAccepted(false, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Article rejected!");

        // Azione non supportata
        } else {
            redirectAttributes.addFlashAttribute("resultMessage", "Incorrect action...");
        }

        return "redirect:/articles/revisor/dashboard";
    }

    /**
     * Esegue una ricerca di articoli basata su una parola chiave.
     * <p>
     * Vengono mostrati solo gli articoli che sono stati approvati.
     *
     * @param keyword   La parola chiave per la ricerca.
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista per la lista degli articoli.
     */
    @GetMapping("/search")
    public String articleSearch(@Param("keyword") String keyword, Model viewModel) {

        viewModel.addAttribute("title", "All articles found");
        
        List<ArticleDTO> articles = articleService.search(keyword);

        List<ArticleDTO> acceptedArticles = articles.stream()
                // Filtro degli articoli per mostrare solo quelli approvati
                .filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());

        viewModel.addAttribute("articles", acceptedArticles);

        return "article/articles";
    }

}