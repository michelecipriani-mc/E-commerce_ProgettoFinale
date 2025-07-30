package com.smartbay.progettofinale.Services;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

import com.smartbay.progettofinale.Models.Article;

public interface ImageService {

    /**
     * Salva il path dell'immagine nel database associandola a un articolo.
     * @param url percorso pubblico dell'immagine (es. /images/nomefile.jpg)
     * @param article entità Article associata
     */
    void saveImageOnDB(String url, Article article);

    /**
     * Salva l'immagine nel file system locale (in /static/images) e restituisce il path pubblico.
     * @param file immagine ricevuta come MultipartFile
     * @return path accessibile via browser (es. /images/nomefile.jpg)
     * @throws IOException se fallisce il salvataggio
     */
    CompletableFuture<String> saveImageOnDisk(MultipartFile file) throws IOException;

    /**
     * Elimina l'immagine dal file system locale e dal database.
     * @param imagePath path dell'immagine da eliminare (es. /images/nomefile.jpg)
     * @throws IOException se fallisce l'eliminazione
     */
    void deleteImage(String imagePath) throws IOException;
}
