package com.smartbay.progettofinale.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.smartbay.progettofinale.Models.Article;
import com.smartbay.progettofinale.Models.Image;
import com.smartbay.progettofinale.Repositories.ImageRepository;
import jakarta.transaction.Transactional;

/**
 * Implementazione del servizio per la gestione delle immagini associate agli
 * articoli.
 * Supporta il salvataggio delle immagini sia su disco che nel database, e la
 * loro cancellazione.
 */
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Value("${image.upload.dir:src/main/resources/static/images}")
    private String uploadDir;

    public void saveImageOnDB(String url, Article article) {
        imageRepository.save(Image.builder().path(url).article(article).build());
    }

    @Async
    public CompletableFuture<String> saveImageOnDisk(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Genera un nome univoco
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path imagePath = Paths.get(uploadDir, filename);

        // Crea le directory se non esistono
        Files.createDirectories(imagePath.getParent());

        // Scrivi il file nel filesystem
        Files.write(imagePath, file.getBytes());

        // Path accessibile via web, es: /images/uuid_nomefile.jpg
        String publicPath = "/images/" + filename;

        return CompletableFuture.completedFuture(publicPath);
    }

    @Async
    @Transactional
    public void deleteImage(String imagePath) throws IOException {
        // Rimuove dal DB
        imageRepository.deleteByPath(imagePath);

        // Converti il path web in path reale su disco
        String filename = Paths.get(imagePath).getFileName().toString();
        Path fullPath = Paths.get(uploadDir, filename);

        Files.deleteIfExists(fullPath);
    }
}