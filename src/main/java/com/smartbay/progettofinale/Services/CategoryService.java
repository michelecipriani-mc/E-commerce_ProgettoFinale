package com.smartbay.progettofinale.Services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.smartbay.progettofinale.DTO.CategoryDTO;
import com.smartbay.progettofinale.Models.Article;
import com.smartbay.progettofinale.Models.Category;
import com.smartbay.progettofinale.Repositories.CategoryRepository;

import jakarta.transaction.Transactional;

/** Servizio per la gestione della logica di business relativa alle categorie. */
@Service
public class CategoryService  implements CrudService<CategoryDTO, Category, Long>{

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Crea una nuova categoria.
     *
     * @param model     La categoria da creare.
     * @param principal Non utilizzato in questo metodo.
     * @param file      Non utilizzato in questo metodo.
     * @return Il DTO della categoria creata.
     */
    @Override
    public CategoryDTO create(Category model, Principal principal, MultipartFile file) {
        return modelMapper.map(categoryRepository.save(model), CategoryDTO.class);
    }

    /**
     * Elimina una categoria e scollega gli articoli ad essa associati.
     *
     * @param id L'ID della categoria da eliminare.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (categoryRepository.existsById(id)) {
            Category category = categoryRepository.findById(id).get();
            if (category.getArticles() != null) {
                // Scollega gli articoli dalla categoria prima di eliminarla
                Iterable<Article> articles = category.getArticles();
                for (Article article : articles) {
                    article.setCategory(null);                    
                }                
            }
            categoryRepository.deleteById(id);            
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    /** Legge una categoria in base al suo ID. */
    @Override
    public CategoryDTO read(Long key) {
        return modelMapper.map(categoryRepository.findById(key), CategoryDTO.class);
    }

    /** Legge tutte le categorie. */
    @Override
    public List<CategoryDTO> readAll() {
        List<CategoryDTO> dtos = new ArrayList<CategoryDTO>();
        for (Category category : categoryRepository.findAll()) {
            dtos.add(modelMapper.map(category, CategoryDTO.class));
        }
        return dtos;
    }

    /**
     * Aggiorna una categoria esistente.
     *
     * @param key   L'ID della categoria da aggiornare.
     * @param model La categoria con i nuovi dati.
     * @return Il DTO della categoria aggiornata.
     */
    @Override
    public CategoryDTO update(Long key, Category model, MultipartFile file) {
        if (categoryRepository.existsById(key)) {
            model.setId(key);
            return modelMapper.map(categoryRepository.save(model), CategoryDTO.class);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    
}