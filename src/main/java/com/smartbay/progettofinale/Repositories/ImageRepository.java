package com.smartbay.progettofinale.Repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import com.smartbay.progettofinale.Models.Image;
import jakarta.transaction.Transactional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Elimina un'immagine in base al path.
     * Usa la query derivata dal nome del metodo (non nativa).
     */
    @Transactional
    @Modifying
    void deleteByPath(String path);

    /**
     * Recupera un'immagine dal path (opzionale, utile per validazioni o controlli).
     */
    Optional<Image> findByPath(String path);
}
