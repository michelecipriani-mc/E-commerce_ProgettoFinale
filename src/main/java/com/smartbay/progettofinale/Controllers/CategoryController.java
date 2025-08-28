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
 * Controller per la gestione delle categorie.
 * <p>
 * Mappa le richieste web agli endpoint per la gestione di categorie,
 * inclusa creazione, modifica, eliminazione e ricerca.
 */
@Controller
@RequestMapping("/categories")
public class CategoryController {

    /**
     * Servizio per la gestione degli articoli.
     */
    @Autowired
    private ArticleService articleService;

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
     * Cerca e visualizza gli articoli di una categoria specifica.
     * <p>
     * Vengono mostrati solo gli articoli approvati.
     *
     * @param id        L'ID della categoria.
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista per la pagina degli articoli.
     */
    @GetMapping("/search/{id}")
    public String categorySearch(@PathVariable("id") Long id, Model viewModel) {
        CategoryDTO category = categoryService.read(id);

        viewModel.addAttribute("title", "All articles for category " + category.getName());

        // Ricerca articoli per categoria
        List<ArticleDTO> articles = articleService.searchByCategory(modelMapper.map(category, Category.class));

        // Filtro degli articoli per mostrare solo quelli approvati
        List<ArticleDTO> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());

        viewModel.addAttribute("articles", acceptedArticles);

        return "article/articles";
    }

    /**
     * Mostra il form per la creazione di una nuova categoria.
     *
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista del form di creazione.
     */
    @GetMapping("create")
    public String categoryCreate(Model viewModel) {
        viewModel.addAttribute("title", "Create a category");
        viewModel.addAttribute("category", new Category());
        return "category/create";
    }

    /**
     * Gestisce il salvataggio di una nuova categoria.
     * <p>
     * Se la validazione fallisce, il form viene ricaricato.
     *
     * @param category           La categoria da salvare.
     * @param result             Risultato della validazione.
     * @param redirectAttributes Attributi per il redirect.
     * @param viewModel          Oggetto Model per la vista.
     * @return Redirect alla dashboard dell'admin.
     */
    @PostMapping
    public String categoryStore(@Valid @ModelAttribute("category") Category category, BindingResult result,
                                RedirectAttributes redirectAttributes, Model viewModel) {
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Create a category");
            viewModel.addAttribute("category", new Category());
            return "category/create";
        }

        categoryService.create(category, null, null);
        redirectAttributes.addFlashAttribute("successMessage", "Category added successfully!");
        
        return "redirect:/admin/dashboard";
    }

    /**
     * Mostra il form per la modifica di una categoria.
     *
     * @param id        L'ID della categoria da modificare.
     * @param viewModel Oggetto Model per la vista.
     * @return Nome della vista del form di modifica.
     */
    @GetMapping("/edit/{id}")
    public String categoryEdit(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Edit category");
        viewModel.addAttribute("category", categoryService.read(id));
        return "category/update";
    }

    /**
     * Gestisce l'aggiornamento di una categoria esistente.
     *
     * @param id                 L'ID della categoria da aggiornare.
     * @param category           La categoria con i dati aggiornati.
     * @param result             Risultato della validazione.
     * @param redirectAttributes Attributi per il redirect.
     * @param viewModel          Oggetto Model per la vista.
     * @return Redirect alla dashboard dell'admin.
     */
    @PostMapping("/update/{id}")
    public String categoryUpdate(@PathVariable("id") Long id, @Valid @ModelAttribute("category") Category category, 
                                BindingResult result, RedirectAttributes redirectAttributes, Model viewModel) {
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Edit category");
            viewModel.addAttribute("category", category);
            return "category/update";
        }

        categoryService.update(id, category, null);
        redirectAttributes.addFlashAttribute("successMessage", "Category added successfully!");
                
        return "redirect:/admin/dashboard";
    }

    /**
     * Elimina una categoria.
     *
     * @param id                 L'ID della categoria da eliminare.
     * @param redirectAttributes Attributi per il redirect.
     * @return Redirect alla dashboard dell'admin.
     */
    @GetMapping("delete/{id}")
    public String categoryDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category successfully deleted!");

        return "redirect:/admin/dashboard";
    }
    
}