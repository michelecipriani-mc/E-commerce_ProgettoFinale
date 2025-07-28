package com.smartbay.progettofinale.Services;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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

@Service
public class ArticleService implements CrudService<ArticleDTO, Article, Long>{

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
        String url = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = (userRepository.findById(userDetails.getId())).get();
            article.setUser(user);
        }

        if (file != null && !file.isEmpty()) {
            try {
                CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                url = futureUrl.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        article.setIsAccepted(null);

        ArticleDTO dto = modelMapper.map(articleRepository.save(article), ArticleDTO.class);
        if (file != null && !file.isEmpty()) {
            imageService.saveImageOnDB(url, article);
        }
        
        return dto;
    }

    @Override
    public void delete(Long key) {
        if (articleRepository.existsById(key)) {
            Article article = articleRepository.findById(key).get();
            try {
                if (article.getImage() != null) {
                    String path = article.getImage().getPath();
                    article.getImage().setArticle(null);
                    imageService.deleteImage(path); 
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            articleRepository.deleteById(key);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
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
        String url = "";
        if (articleRepository.existsById(key)) {
            updatedArticle.setId(key);
            Article article = articleRepository.findById(key).get();
            updatedArticle.setUser(article.getUser());
            if (file != null && !file.isEmpty()) {
                try {
                    if (article.getImage() != null) {
                        imageService.deleteImage(article.getImage().getPath());
                    }
                    CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                    url = futureUrl.get();
                    imageService.saveImageOnDB(url, updatedArticle);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to update image", e);
                }
            } else if (article.getImage() != null) {
                updatedArticle.setImage(article.getImage());
            }

            if (!updatedArticle.equals(article)) {
                updatedArticle.setIsAccepted(null);
            } else {
                updatedArticle.setIsAccepted(article.getIsAccepted());
            }
            return modelMapper.map(articleRepository.save(updatedArticle), ArticleDTO.class);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
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
