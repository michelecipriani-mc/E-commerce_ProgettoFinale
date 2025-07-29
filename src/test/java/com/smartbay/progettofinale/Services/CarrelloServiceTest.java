package com.smartbay.progettofinale.Services;

import com.smartbay.progettofinale.Models.Carrello;
import com.smartbay.progettofinale.Repositories.ArticleRepository;
import com.smartbay.progettofinale.DTO.CarrelloDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CarrelloServiceTest {

    private ArticleRepository articleRepository;
    private CarrelloService carrelloService;

    private final Long mockUserId = 1L;

    @BeforeEach
    void setUp() {
        articleRepository = mock(ArticleRepository.class);
        carrelloService = new CarrelloService(articleRepository);
    }

    @Test
    void testGetCarrelloFromUtente_createsNewCarrelloIfNotExists() {
        Carrello carrello = carrelloService.getCarrelloFromUtente(mockUserId);
        assertThat(carrello).isNotNull();
        assertThat(carrello.getUserId()).isEqualTo(mockUserId);
    }

    @Test
    void testGetCarrelloDTOFromUtente_returnsEmptyDTOForEmptyCart() {
        CarrelloDTO dto = carrelloService.getCarrelloDTOFromUtente(mockUserId);
        assertThat(dto).isNotNull();
        assertThat(dto.getArticoli()).isEmpty();
    }

    @Test
    void testSvuotaCarrello_doesNotThrow() {
        carrelloService.getCarrelloFromUtente(mockUserId).aggiornaQuantitaArticolo(100L, 2); // populate
        carrelloService.svuotaCarrello(mockUserId);
        Carrello carrello = carrelloService.getCarrelloFromUtente(mockUserId);
        assertThat(carrello.getArticles()).isEmpty();
    }

    @Test
    void testRimuoviArticoloFromCart() {
        Long articleId = 101L;
        carrelloService.getCarrelloFromUtente(mockUserId).aggiornaQuantitaArticolo(articleId, 3);

        carrelloService.rimuoviArticolo(mockUserId, articleId);
        Carrello carrello = carrelloService.getCarrelloFromUtente(mockUserId);
        assertThat(carrello.getArticles()).doesNotContainKey(articleId);
    }

    @Test
    void testAggiornaQuantitaArticolo_throwsIfArticleMissing() {
        Long articleId = 999L;
        when(articleRepository.findById(articleId)).thenReturn(java.util.Optional.empty());

        RuntimeException ex = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> carrelloService.aggiornaQuantitaArticolo(mockUserId, articleId, 1));

        assertThat(ex.getMessage()).contains("Nessun articolo trovato");
    }
}

