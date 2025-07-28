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

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/search/{id}")
    public String categorySearch(@PathVariable("id") Long id, Model viewModel) {
        CategoryDTO category = categoryService.read(id);

        viewModel.addAttribute("title", "All articles for category " + category.getName());
        List<ArticleDTO> articles = articleService.searchByCategory(modelMapper.map(category, Category.class));
        List<ArticleDTO> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        viewModel.addAttribute("articles", acceptedArticles);
        return "article/articles";
    }

    @GetMapping("create")
    public String categoryCreate(Model viewModel) {
        viewModel.addAttribute("title", "Create a category");
        viewModel.addAttribute("category", new Category());
        return "category/create";
    }

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

    @GetMapping("/edit/{id}")
    public String categoryEdit(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Edit category");
        viewModel.addAttribute("category", categoryService.read(id));
        return "category/update";
    }

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

    @GetMapping("delete/{id}")
    public String categoryDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category successfully deleted!");

        return "redirect:/admin/dashboard";
    }
    
}