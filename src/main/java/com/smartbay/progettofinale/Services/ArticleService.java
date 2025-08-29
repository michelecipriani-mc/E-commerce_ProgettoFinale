package com.smartbay.progettofinale.Services;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.smartbay.progettofinale.DTO.ArticleDTO;
import com.smartbay.progettofinale.Models.Article;
import com.smartbay.progettofinale.Models.Category;
import com.smartbay.progettofinale.Models.Image;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.ArticleRepository;
import com.smartbay.progettofinale.Repositories.UserRepository;

import jakarta.transaction.Transactional;

/** Servizio per la gestione della logica di business relativa agli articoli. */
@Service
public class ArticleService implements CrudService<ArticleDTO, Article, Long> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ImageService imageService;

    @Autowired
    private CarrelloService carrelloService;

    /**
     * Crea un nuovo articolo.
     * <p>
     * Associa l'articolo a un utente, una categoria e un'immagine.
     *
     * @param article   L'entità articolo da creare.
     * @param principal L'utente che sta creando l'articolo.
     * @param file      Il file immagine da associare.
     * @return Il DTO dell'articolo creato.
     */
    @Override
    public ArticleDTO create(Article article, Principal principal, MultipartFile[] files) {

        // Ottieni utente attivo
        if (principal != null) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            // Inserire riferimento all'utente nell'articolo
            article.setUser(user);
        }

        // Articolo in attesa di conferma
        article.setIsAccepted(null);

        Article savedArticle = articleRepository.save(article); // salvo prima per avere ID

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        String imagePath = imageService.saveImageOnDisk(file).get(); // salva su disco
                        imageService.saveImageOnDB(imagePath, savedArticle); // salva il path nel DB

                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Failed to save image", e);
                    }
                }
            }
        }

        return modelMapper.map(savedArticle, ArticleDTO.class);
    }

    /**
     * Elimina un articolo in base al suo ID.
     * <p>
     * Questo metodo elimina anche l'immagine associata e rimuove l'articolo dai
     * carrelli degli utenti.
     *
     * @param id L'ID dell'articolo da eliminare.
     */
    @Override
    @Transactional
    public void delete(Long key) {

        // Ottieni articolo da DB
        Article article = articleRepository.findById(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        // Se ci sono immagini associate
        if (article.getImages() != null && !article.getImages().isEmpty()) {
            for (Image img : article.getImages()) {
                try {
                    // Elimina il file fisico
                    imageService.deleteImage(img.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // Elimina articolo
        articleRepository.delete(article);

        // Rimuovi riferimento all'articolo da tutti i carrelli
        carrelloService.rimuoviArticoloDaTuttiICarrelli(key);

    }

    /** Legge un articolo in base al suo ID. */
    @Override
    public ArticleDTO read(Long key) {
        Optional<Article> optArticle = articleRepository.findById(key);
        if (optArticle.isPresent()) {
            return modelMapper.map(optArticle.get(), ArticleDTO.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author id=" + key + " not found");
        }
    }

    /** Legge tutti gli articoli. */
    @Override
    public List<ArticleDTO> readAll() {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findAll()) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));
        }
        return dtos;
    }

    /**
     * Aggiorna un articolo.
     *
     * @param id      L'ID dell'articolo da aggiornare.
     * @param article L'entità articolo con i nuovi dati.
     * @param file    Il file immagine (opzionale).
     * @return Il DTO dell'articolo aggiornato.
     */
    @Override
    public ArticleDTO update(Long key, Article updatedArticle, MultipartFile[] files) {

        // Controlla se l'articolo esiste
        if (!articleRepository.existsById(key)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Ottieni articolo
        Article existingArticle = articleRepository.findById(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        updatedArticle.setId(key);
        updatedArticle.setUser(existingArticle.getUser());

        // Gestione immagine
        try {
            if (files != null && files.length > 0) {
                // Elimina immagine precedente se esiste
                if (existingArticle.getImages() != null) {
                    for (Image img : existingArticle.getImages()) {
                        imageService.deleteImage(img.getPath());

                    }
                }
                // foreach per salvare tutte le immagini
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        // Salva nuova immagine
                        String newImagePath = imageService.saveImageOnDisk(file).get();
                        imageService.saveImageOnDB(newImagePath, updatedArticle);
                    }
                }
            } else if (existingArticle.getImages() != null) {

                // Mantieni immagine esistente
                updatedArticle.setImages(existingArticle.getImages());

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to handle image update", e);
        }

        // Verifica se sono stati fatti cambiamenti
        if (!updatedArticle.equals(existingArticle)) {
            updatedArticle.setIsAccepted(null); // deve essere ri-approvato

            // Rimuovi articolo da tutti i carrelli
            carrelloService.rimuoviArticoloDaTuttiICarrelli(key);

        } else {
            updatedArticle.setIsAccepted(existingArticle.getIsAccepted());
        }

        // Salva in DB
        Article saved = articleRepository.save(updatedArticle);

        return modelMapper.map(saved, ArticleDTO.class);
    }

    /** Cerca gli articoli in base a una categoria. */
    public List<ArticleDTO> searchByCategory(Category category) {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findByCategory(category)) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));
        }
        return dtos;
    }

    /** Cerca gli articoli scritti da un utente specifico. */
    public List<ArticleDTO> searchByUser(User user) {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findByUser(user)) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));
        }
        return dtos;
    }

    /**
     * Imposta lo stato di approvazione di un articolo.
     *
     * @param result Lo stato di approvazione (true per approvato, false per non
     *               approvato).
     * @param id     L'ID dell'articolo.
     */
    public void setIsAccepted(Boolean result, Long id) {
        Article article = articleRepository.findById(id).get();
        article.setIsAccepted(result);
        articleRepository.save(article);
    }

    /**
     * Aggiorna lo stato di accettazione di un articolo.
     *
     * @param articleId  L'ID dell'articolo da aggiornare.
     * @param isAccepted Lo stato di approvazione.
     */
    public void updateStatus(Long articleId, boolean isAccepted) {
        Optional<Article> articleOptional = articleRepository.findById(articleId);
        if (articleOptional.isPresent()) {
            Article article = articleOptional.get();
            article.setIsAccepted(isAccepted);
            articleRepository.save(article);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + articleId);
        }
    }

    /**
     * Esegue una ricerca full-text degli articoli.
     *
     * @param keyword La parola chiave per la ricerca.
     * @return Una lista di DTO degli articoli corrispondenti.
     */
    public List<ArticleDTO> search(String keyword) {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.search(keyword)) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));
        }
        return dtos;
    }
}
