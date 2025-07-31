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

/**
 * Servizio per la gestione delle categorie.
 * Implementa l'interfaccia CrudService per fornire operazioni CRUD sulle entità
 * Category.
 */

@Service
public class CategoryService implements CrudService<CategoryDTO, Category, Long> {
    // Repository per accedere ai dati delle categorie
    @Autowired
    private CategoryRepository categoryRepository;
    // Mapper per convertire tra entità Category e DTO CategoryDTO

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Crea una nuova categoria nel database.
     * Ignora il parametro Principal e il file in input.
     *
     * @param model     oggetto Category da creare
     * @param principal utente autenticato (non usato)
     * @param file      file multipart (non usato)
     * @return DTO della categoria creata
     */
    @Override
    public CategoryDTO create(Category model, Principal principal, MultipartFile file) {
        return modelMapper.map(categoryRepository.save(model), CategoryDTO.class);
    }

    /**
     * Elimina una categoria dal database.
     * Prima di eliminare la categoria, se ha articoli associati,
     * disassocia la categoria da ciascun articolo impostandola a null.
     * Usa la transazione per assicurare consistenza.
     *
     * @param id id della categoria da eliminare
     * @throws ResponseStatusException con BAD_REQUEST se la categoria non esiste
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (categoryRepository.existsById(id)) {
            Category category = categoryRepository.findById(id).get();
            if (category.getArticles() != null) {
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

    /**
     * Legge una categoria dal database tramite la chiave primaria.
     *
     * @param key id della categoria da leggere
     * @return DTO della categoria trovata
     */
    @Override
    public CategoryDTO read(Long key) {
        return modelMapper.map(categoryRepository.findById(key), CategoryDTO.class);
    }

    /**
     * Restituisce tutte le categorie presenti nel database come lista di DTO.
     *
     * @return lista di CategoryDTO
     */
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
     * Se la categoria con l'id fornito esiste, aggiorna i suoi dati e la salva.
     * Altrimenti solleva un'eccezione BAD_REQUEST.
     * Ignora il parametro file.
     *
     * @param key   id della categoria da aggiornare
     * @param model nuova istanza Category con i dati aggiornati
     * @param file  file multipart (non usato)
     * @return DTO della categoria aggiornata
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