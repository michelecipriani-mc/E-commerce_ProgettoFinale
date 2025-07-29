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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping
    public String articlesIndex(Model viewModel) {
        viewModel.addAttribute("title", "All Article");
        List<ArticleDTO> articles = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDTO.class));
        }
        Collections.sort(articles, Comparator.comparing(ArticleDTO::getPublishDate).reversed());
        viewModel.addAttribute("articles", articles);
        return "article/articles";
    }
    

    @GetMapping("create")
    public String articleCreate(Model viewModel) {
        viewModel.addAttribute("title", "Create Article");
        viewModel.addAttribute("article", new Article());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/create";
    }
    
    @PostMapping
    public String articlesStore(@Valid @ModelAttribute("article") Article article,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Principal principal, MultipartFile file, Model viewModel) {
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Create an Article");
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll()); 
            return "article/create";          
        }
        article.setIsAccepted(null);
        articleService.create(article, principal, file);
        redirectAttributes.addFlashAttribute("successMessage", "Article added and awaiting review");
        
        return "redirect:/";
    }

    @GetMapping("detail/{id}")
    public String detailArticle(@PathVariable("id") Long id, Model viewModel) {

        ArticleDTO article = articleService.read(id);

        viewModel.addAttribute("title", "Article detail");
        viewModel.addAttribute("article", article);
        viewModel.addAttribute("aggiornaRequest", new AggiornaArticoloCarrelloRequest(article.getId(), 1));
        return "article/detail";  
    }

    @GetMapping("/edit/{id}")
    public String editArticle(@PathVariable("id") Long id, Model viewModel) {
        ArticleDTO articleDto = articleService.read(id);
        if (articleDto != null) {
            viewModel.addAttribute("title", "Article update");
            viewModel.addAttribute("article", modelMapper.map(articleDto, Article.class));
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/edit";  
        } else {
            return "redirect:/writer/dashboard";
        }
    }

    @PostMapping("/update/{id}")
    public String articleUpdate(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("article") Article article,
                                BindingResult result, RedirectAttributes redirectAttributes,
                                Principal principal, MultipartFile file, Model viewModel) {
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
            article.setIsAccepted(false); 
            redirectAttributes.addFlashAttribute("warningMessage", "This article has been modified and needs re-approval.");
        } else {
            article.setIsAccepted(existingArticle.getIsAccepted()); 
            redirectAttributes.addFlashAttribute("successMessage", "Article successfully edited!");
        }

        articleService.update(id, article, file);
        return "redirect:/writer/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String articleDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Article successfully deleted!");
        return "redirect:/writer/dashboard";
    }

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
    
    
    @GetMapping("revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Article detail");
        viewModel.addAttribute("article", articleService.read(id));
        return "revisor/detail";  
    }
    
    @PostMapping("/accept")
    public String articleSetAccepted(@RequestParam("action") String action, @RequestParam("articleId") Long articleId, RedirectAttributes redirectAttributes) {
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

    @GetMapping("/search")
    public String articleSearch(@Param("keyword") String keyword, Model viewModel) {
        viewModel.addAttribute("title", "All articles found");
        List<ArticleDTO> articles = articleService.search(keyword);
        List<ArticleDTO> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        viewModel.addAttribute("articles", acceptedArticles);
        return "article/articles";
    }

}