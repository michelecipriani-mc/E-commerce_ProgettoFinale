package com.smartbay.progettofinale.Services;

import com.smartbay.progettofinale.Models.*;
import com.smartbay.progettofinale.Repositories.ArticleRepository;
import com.smartbay.progettofinale.Repositories.OrdineRepository;
import com.smartbay.progettofinale.Repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrdineServiceTest {

    private CarrelloService carrelloService;
    private ArticleRepository articleRepository;
    private OrdineRepository ordineRepository;
    private UserRepository userRepository;
    private OrdineService ordineService;

    private User user;
    private Carrello carrello;

    @BeforeEach
    void setUp() {
        carrelloService = mock(CarrelloService.class);
        articleRepository = mock(ArticleRepository.class);
        ordineRepository = mock(OrdineRepository.class);
        userRepository = mock(UserRepository.class);
        ordineService = new OrdineService(carrelloService, articleRepository, ordineRepository,
                userRepository);

        user = new User();
        user.setId(1L);
        user.setBalance(new BigDecimal("100.00"));

        carrello = new Carrello(user.getId(), new HashMap<>());
    }

    @Test
    void creaOrdine_successfullyCreatesOrderAndUpdatesBalance() {
        // Setup test data
        Long articoloId = 10L;
        int quantita = 2;
        BigDecimal prezzo = new BigDecimal("20.00");

        carrello.getArticles().put(articoloId, quantita);

        Article articolo = new Article();
        articolo.setId(articoloId);
        articolo.setTitle("Test Articolo");
        articolo.setPrice(prezzo);

        BigDecimal expectedTotale = prezzo.multiply(BigDecimal.valueOf(quantita));

        when(carrelloService.getCarrelloFromUtente(user.getId())).thenReturn(carrello);
        when(articleRepository.findById(articoloId)).thenReturn(Optional.of(articolo));

        // Act
        Ordine ordine = ordineService.creaOrdine(user);

        // Assert
        assertNotNull(ordine);
        assertEquals(user, ordine.getUser());
        assertEquals(expectedTotale, ordine.getTotale());
        assertEquals(1, ordine.getArticoli().size());

        ArticoloOrdine voce = ordine.getArticoli().get(0);
        assertEquals("Test Articolo", voce.getTitoloArticolo());
        assertEquals(quantita, voce.getQuantita());
        assertEquals(prezzo, voce.getPrezzoSingolo());

        verify(ordineRepository).save(any(Ordine.class));
        verify(userRepository).save(user);
        verify(carrelloService).svuotaCarrello(user.getId());

        // Check user balance updated
        assertEquals(new BigDecimal("60.00"), user.getBalance());
    }

    @Test
    void creaOrdine_throwsWhenCarrelloIsEmpty() {
        when(carrelloService.getCarrelloFromUtente(user.getId())).thenReturn(carrello);

        RuntimeException ex =
                assertThrows(RuntimeException.class, () -> ordineService.creaOrdine(user));
        assertEquals("Il carrello è vuoto", ex.getMessage());
    }

    @Test
    void creaOrdine_throwsWhenSaldoInsufficiente() {
        Long articoloId = 5L;
        carrello.getArticles().put(articoloId, 1);

        Article articolo = new Article();
        articolo.setId(articoloId);
        articolo.setTitle("Too Expensive");
        articolo.setPrice(new BigDecimal("999.00"));

        when(carrelloService.getCarrelloFromUtente(user.getId())).thenReturn(carrello);
        when(articleRepository.findById(articoloId)).thenReturn(Optional.of(articolo));

        RuntimeException ex =
                assertThrows(RuntimeException.class, () -> ordineService.creaOrdine(user));
        assertEquals("Saldo insufficiente per completare l'ordine", ex.getMessage());
    }
}

