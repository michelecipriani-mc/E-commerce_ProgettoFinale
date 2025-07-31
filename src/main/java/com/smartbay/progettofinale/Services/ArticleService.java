package com.smartbay.progettofinale.Services;

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
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.ArticleRepository;
import com.smartbay.progettofinale.Repositories.UserRepository;

/**
 * Service che gestisce la logica di business per l'entità Article.
 * Implementa l'interfaccia CrudService per operazioni CRUD con DTO.
 * 
 * --@Service indica che è un componente di servizio gestito da Spring.
 * 
 * Dipendenze iniettate tramite @Autowired:
 * - UserRepository: per recuperare dati utenti.
 * - ArticleRepository: per persistere e recuperare gli articoli dal DB.
 * - ModelMapper: per convertire tra entità Article e DTO ArticleDTO.
 * - ImageService: per gestire il salvataggio e cancellazione di immagini
 * associate agli articoli.
 * 
 * Metodi principali:
 * 
 * - create(Article article, Principal principal, MultipartFile file):
 * Crea un nuovo articolo associandolo all'utente autenticato (se presente).
 * Salva l'articolo e, se fornita, salva anche l'immagine sul disco e nel DB.
 * L'articolo creato ha isAccepted settato a null (da approvare).
 * 
 * - delete(Long key):
 * Elimina un articolo dato l'id.
 * Se presente un'immagine associata, la cancella prima dal file system.
 * 
 * - read(Long key):
 * Recupera un articolo per id e lo mappa in ArticleDTO.
 * Lancia eccezione 404 se non trovato.
 * 
 * - readAll():
 * Recupera tutti gli articoli dal DB e li mappa in lista di DTO.
 * 
 * - update(Long key, Article updatedArticle, MultipartFile file):
 * Aggiorna un articolo esistente.
 * Gestisce la sostituzione o mantenimento dell'immagine associata.
 * Se i dati cambiano, resetta lo stato di approvazione (isAccepted=null).
 * 
 * Metodi aggiuntivi specifici:
 * 
 * - searchByCategory(Category category):
 * Cerca articoli per categoria e li converte in DTO.
 * 
 * - searchByUser(User user):
 * Cerca articoli scritti da uno specifico utente.
 * 
 * - setIsAccepted(Boolean result, Long id):
 * Imposta lo stato di approvazione (isAccepted) per un articolo.
 * 
 * - updateStatus(Long articleId, boolean isAccepted):
 * Aggiorna lo stato di accettazione con controllo di esistenza.
 * 
 * - search(String keyword):
 * Esegue una ricerca testuale su titolo, sottotitolo, username e categoria.
 */

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

    @Override
    public ArticleDTO create(Article article, Principal principal, MultipartFile file) {
        if (principal != null) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            article.setUser(user);
        }

        article.setIsAccepted(null);
        Article savedArticle = articleRepository.save(article); // salvo prima per avere ID

        if (file != null && !file.isEmpty()) {
            try {
                String imagePath = imageService.saveImageOnDisk(file).get(); // salva su disco
                imageService.saveImageOnDB(imagePath, savedArticle); // salva il path nel DB
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to save image", e);
            }
        }

        return modelMapper.map(savedArticle, ArticleDTO.class);
    }

    @Override
    public void delete(Long key) {
        Article article = articleRepository.findById(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        try {
            if (article.getImage() != null) {
                String path = article.getImage().getPath();
                article.getImage().setArticle(null); // disaccoppia
                imageService.deleteImage(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        articleRepository.deleteById(key);
    }

    @Override
    public ArticleDTO read(Long key) {
        Optional<Article> optArticle = articleRepository.findById(key);
        if (optArticle.isPresent()) {
            return modelMapper.map(optArticle.get(), ArticleDTO.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author id=" + key + " not found");
        }
    }

    @Override
    public List<ArticleDTO> readAll() {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findAll()) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));
        }
        return dtos;
    }

    @Override
    public ArticleDTO update(Long key, Article updatedArticle, MultipartFile file) {
        if (!articleRepository.existsById(key)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Article existingArticle = articleRepository.findById(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        updatedArticle.setId(key);
        updatedArticle.setUser(existingArticle.getUser());

        // Gestione immagine
        try {
            if (file != null && !file.isEmpty()) {
                // Elimina immagine precedente se esiste
                if (existingArticle.getImage() != null) {
                    imageService.deleteImage(existingArticle.getImage().getPath());
                }

                // Salva nuova immagine
                String newImagePath = imageService.saveImageOnDisk(file).get();
                imageService.saveImageOnDB(newImagePath, updatedArticle);
            } else if (existingArticle.getImage() != null) {
                // Mantieni immagine esistente
                updatedArticle.setImage(existingArticle.getImage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to handle image update", e);
        }

        // Verifica se sono stati fatti cambiamenti
        if (!updatedArticle.equals(existingArticle)) {
            updatedArticle.setIsAccepted(null); // deve essere ri-approvato
        } else {
            updatedArticle.setIsAccepted(existingArticle.getIsAccepted());
        }

        Article saved = articleRepository.save(updatedArticle);
        return modelMapper.map(saved, ArticleDTO.class);
    }

    public List<ArticleDTO> searchByCategory(Category category) {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findByCategory(category)) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));
        }
        return dtos;
    }

    public List<ArticleDTO> searchByUser(User user) {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.findByUser(user)) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));
        }
        return dtos;
    }

    public void setIsAccepted(Boolean result, Long id) {
        Article article = articleRepository.findById(id).get();
        article.setIsAccepted(result);
        articleRepository.save(article);
    }

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

    public List<ArticleDTO> search(String keyword) {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for (Article article : articleRepository.search(keyword)) {
            dtos.add(modelMapper.map(article, ArticleDTO.class));
        }
        return dtos;
    }
}
