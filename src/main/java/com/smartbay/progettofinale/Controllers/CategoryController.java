package com.smartbay.progettofinale.Controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartbay.progettofinale.DTO.ArticleDTO;
import com.smartbay.progettofinale.DTO.CategoryDTO;
import com.smartbay.progettofinale.Models.Category;
import com.smartbay.progettofinale.Services.ArticleService;
import com.smartbay.progettofinale.Services.CategoryService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller per la gestione delle categorie degli articoli.
 * 
 * Funzionalità:
 * - Ricerca articoli per categoria (visibile a tutti)
 * - Creazione, modifica ed eliminazione di categorie (riservata all'ADMIN)
 * 
 * Utilizza i servizi CategoryService e ArticleService per separare la logica di
 * business.
 */

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ModelMapper modelMapper;

    // Mostra tutti gli articoli accettati che appartengono a una specifica
    // categoria
    @GetMapping("/search/{id}")
    public String categorySearch(@PathVariable("id") Long id, Model viewModel) {
        CategoryDTO category = categoryService.read(id);

        viewModel.addAttribute("title", "All articles for category " + category.getName());
        // Ottiene gli articoli associati alla categoria
        List<ArticleDTO> articles = articleService.searchByCategory(modelMapper.map(category, Category.class));
        // Filtra solo quelli accettati
        List<ArticleDTO> acceptedArticles = articles.stream()
                .filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        viewModel.addAttribute("articles", acceptedArticles);
        return "article/articles";
    }

    // Mostra il form per creare una nuova categoria (solo per ADMIN)
    @GetMapping("create")
    public String categoryCreate(Model viewModel) {
        viewModel.addAttribute("title", "Create a category");
        viewModel.addAttribute("category", new Category());
        return "category/create";
    }

    // Salva una nuova categoria nel database
    @PostMapping
    public String categoryStore(@Valid @ModelAttribute("category") Category category, BindingResult result,
            RedirectAttributes redirectAttributes, Model viewModel) {
        if (result.hasErrors()) {
            // Se ci sono errori di validazione, ricarica il form
            viewModel.addAttribute("title", "Create a category");
            viewModel.addAttribute("category", new Category());
            return "category/create";
        }
        // Salva la categoria
        categoryService.create(category, null, null);
        redirectAttributes.addFlashAttribute("successMessage", "Category added successfully!");

        return "redirect:/admin/dashboard";
    }

    // Mostra il form per modificare una categoria esistente
    @GetMapping("/edit/{id}")
    public String categoryEdit(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Edit category");
        viewModel.addAttribute("category", categoryService.read(id));
        return "category/update";
    }

    // Gestisce l'aggiornamento di una categoria
    @PostMapping("/update/{id}")
    public String categoryUpdate(@PathVariable("id") Long id, @Valid @ModelAttribute("category") Category category,
            BindingResult result, RedirectAttributes redirectAttributes, Model viewModel) {
        if (result.hasErrors()) {
            // In caso di errore, ricarica il form con i dati inseriti
            viewModel.addAttribute("title", "Edit category");
            viewModel.addAttribute("category", category);
            return "category/update";
        }
        // Salva le modifiche
        categoryService.update(id, category, null);
        redirectAttributes.addFlashAttribute("successMessage", "Category added successfully!");

        return "redirect:/admin/dashboard";
    }

    // Elimina una categoria esistente
    @GetMapping("delete/{id}")
    public String categoryDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {

        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category successfully deleted!");

        return "redirect:/admin/dashboard";
    }

}