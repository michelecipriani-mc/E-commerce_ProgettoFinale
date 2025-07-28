package com.smartbay.progettofinale.Services;

import com.smartbay.progettofinale.Models.Article;
import com.smartbay.progettofinale.Models.ArticoloOrdine;
import com.smartbay.progettofinale.Models.Carrello;
import com.smartbay.progettofinale.Models.Ordine;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.ArticleRepository;
import com.smartbay.progettofinale.Repositories.OrdineRepository;
import com.smartbay.progettofinale.Repositories.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdineService {

    private final CarrelloService carrelloService;
    private final ArticleRepository articleRepository;
    private final OrdineRepository ordineRepository;
    private final UserRepository userRepository;

    public OrdineService(CarrelloService carrelloService, ArticleRepository articleRepository,
                         OrdineRepository ordineRepository, UserRepository userRepository) {
        this.carrelloService = carrelloService;
        this.articleRepository = articleRepository;
        this.ordineRepository = ordineRepository;
        this.userRepository = userRepository;
    }

    //METODO CREA ORDINE AGGIORNATO CON SCALO DALLA BALANCE
    public Ordine creaOrdine(User user) {
    Carrello carrello = carrelloService.getCarrelloFromUtente(user.getId());

    if (carrello.getArticles().isEmpty()) {
        throw new RuntimeException("Il carrello è vuoto");
    }

    BigDecimal totale = carrello.getPrezzoTotale(articleRepository);

    if (user.getBalance() == null || user.getBalance().compareTo(totale) < 0) {
        throw new RuntimeException("Saldo insufficiente per completare l'ordine");
    }

    Ordine ordine = new Ordine();
    ordine.setUser(user);
    ordine.setDataOrdine(LocalDateTime.now());
    ordine.setTotale(totale);

    List<ArticoloOrdine> articoli = carrello.getArticles().entrySet().stream()
        .map(entry -> {
            Long idArticolo = entry.getKey();
            Integer quantita = entry.getValue();
            Article articolo = articleRepository.findById(idArticolo)
                .orElseThrow(() -> new RuntimeException("Articolo non trovato"));

            ArticoloOrdine voce = new ArticoloOrdine();
            voce.setArticoloId(idArticolo);
            voce.setTitoloArticolo(articolo.getTitle());
            voce.setQuantita(quantita);
            voce.setPrezzoSingolo(articolo.getPrice());
            voce.setOrdine(ordine);
            return voce;
        })
        .collect(Collectors.toList());

    ordine.setArticoli(articoli);

    // qui scala il saldo
    BigDecimal nuovoSaldo = user.getBalance().subtract(totale);
    user.setBalance(nuovoSaldo);

    // poi salva ordine e utente (per aggiornare la balance)
    ordineRepository.save(ordine);
    userRepository.save(user);

    // svuota il carrello
    carrelloService.svuotaCarrello(user.getId());

    return ordine;
}

    public List<Ordine> getOrdiniUtente(User user) {
        return ordineRepository.findByUser(user);
    }
}