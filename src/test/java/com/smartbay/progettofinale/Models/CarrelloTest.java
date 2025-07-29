package com.smartbay.progettofinale.Models;

import com.smartbay.progettofinale.Repositories.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarrelloTest {

    private Carrello carrello;

    @BeforeEach
    void setUp() {
        carrello = new Carrello();
        carrello.setUserId(1L);
    }

    @Test
    void testAggiuntaArticolo() {
        carrello.aggiornaQuantitaArticolo(1L, 2);
        assertEquals(2, carrello.getArticles().get(1L));
    }

    @Test
    void testRimozioneQuantitaArticolo() {
        carrello.aggiornaQuantitaArticolo(1L, 3); // add 3
        carrello.aggiornaQuantitaArticolo(1L, -1); // remove 1
        assertEquals(2, carrello.getArticles().get(1L));
    }

    @Test
    void testRimozioneArticoloSeQuantitaDiventaZero() {
        carrello.aggiornaQuantitaArticolo(1L, 1);
        carrello.aggiornaQuantitaArticolo(1L, -1);
        assertFalse(carrello.getArticles().containsKey(1L));
    }

    @Test
    void testAggiuntaOltreMaxLimit() {
        carrello.aggiornaQuantitaArticolo(2L, 10); // max is 5
        assertEquals(Carrello.maxArticle, carrello.getArticles().get(2L));
    }

    @Test
    void testRimuoviArticolo() {
        carrello.aggiornaQuantitaArticolo(3L, 2);
        carrello.rimuoviArticolo(3L);
        assertFalse(carrello.getArticles().containsKey(3L));
    }

    @Test
    void testSvuotaCarrello() {
        carrello.aggiornaQuantitaArticolo(4L, 1);
        carrello.aggiornaQuantitaArticolo(5L, 1);
        carrello.svuota();
        assertTrue(carrello.getArticles().isEmpty());
    }

    @Test
    void testPrezzoTotale() {
        ArticleRepository mockRepo = mock(ArticleRepository.class);

        Article article1 = new Article();
        article1.setId(1L);
        article1.setPrice(new BigDecimal("10.00"));

        Article article2 = new Article();
        article2.setId(2L);
        article2.setPrice(new BigDecimal("20.00"));

        when(mockRepo.findById(1L)).thenReturn(Optional.of(article1));
        when(mockRepo.findById(2L)).thenReturn(Optional.of(article2));

        carrello.aggiornaQuantitaArticolo(1L, 2); // 2 * 10 = 20
        carrello.aggiornaQuantitaArticolo(2L, 1); // 1 * 20 = 20

        BigDecimal total = carrello.getPrezzoTotale(mockRepo);

        assertEquals(new BigDecimal("40.00"), total);
    }

    @Test
    void testPrezzoTotaleConArticoloNonTrovato() {
        ArticleRepository mockRepo = mock(ArticleRepository.class);

        when(mockRepo.findById(anyLong())).thenReturn(Optional.empty());

        carrello.aggiornaQuantitaArticolo(1L, 3); // Add something, but article not found

        BigDecimal total = carrello.getPrezzoTotale(mockRepo);

        assertEquals(BigDecimal.ZERO, total);
    }
}

