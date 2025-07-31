package com.smartbay.progettofinale.Services;

import com.smartbay.progettofinale.DTO.ArticleDTO;
import com.smartbay.progettofinale.DTO.ArticoloQuantitaDTO;
import com.smartbay.progettofinale.DTO.OrdineDTO;
import com.smartbay.progettofinale.Models.Article;
import com.smartbay.progettofinale.Models.ArticoloOrdine;
import com.smartbay.progettofinale.Models.Carrello;
import com.smartbay.progettofinale.Models.Ordine;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.ArticleRepository;
import com.smartbay.progettofinale.Repositories.OrdineRepository;
import com.smartbay.progettofinale.Repositories.UserRepository;
import com.smartbay.progettofinale.Security.SecurityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdineService {
    // Dipendenze necessarie per la gestione degli ordini
    private final CarrelloService carrelloService;
    private final ArticleRepository articleRepository;
    private final OrdineRepository ordineRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final ModelMapper modelMapper;

    // Costruttore per l'iniezione delle dipendenze
    public OrdineService(CarrelloService carrelloService, ArticleRepository articleRepository,
            OrdineRepository ordineRepository, UserRepository userRepository, SecurityService securityService,
            ModelMapper modelMapper) {
        this.carrelloService = carrelloService;
        this.articleRepository = articleRepository;
        this.ordineRepository = ordineRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.modelMapper = modelMapper;
    }

    /**
     * Crea un ordine per l'utente attualmente autenticato.
     * Controlla che l'utente sia autenticato, che il carrello non sia vuoto e che
     * il saldo dell'utente sia sufficiente a coprire il totale dell'ordine.
     * 
     * Procedura:
     * 1. Recupera l'utente autenticato.
     * 2. Recupera il carrello dell'utente.
     * 3. Calcola il totale del carrello.
     * 4. Verifica che l'utente abbia saldo sufficiente.
     * 5. Crea un oggetto Ordine con i dettagli.
     * 6. Trasforma gli articoli nel carrello in voci di ordine.
     * 7. Aggiorna il saldo dell'utente scalando il totale.
     * 8. Salva ordine e utente (per aggiornare il saldo).
     * 9. Svuota il carrello.
     * 10. Restituisce un DTO rappresentante l'ordine creato.
     * 
     * @return OrdineDTO dell'ordine appena creato
     * @throws IllegalStateException se l'utente non è autenticato
     * @throws RuntimeException      se il carrello è vuoto o saldo insufficiente
     */
    // METODO CREA ORDINE AGGIORNATO CON SCALO DALLA BALANCE
    @Transactional
    public OrdineDTO creaOrdine() {

        // Obtain instance of active user
        User userInstance = securityService.getActiveUser();

        if (userInstance == null) {
            throw new IllegalStateException("Unauthenticated users cannot place orders.");
        }

        // Obtain a user object manged by JPA for persistence
        User user = userRepository.findById(userInstance.getId())
                .orElseThrow(() -> new IllegalArgumentException("No user by this id."));

        Carrello carrello = carrelloService.getCarrelloFromUtente(user.getId());

        if (carrello.getArticles().isEmpty()) {
            throw new RuntimeException("Il carrello è vuoto");
        }

        BigDecimal totale = carrello.getPrezzoTotale(articleRepository);

        if (user.getBalance() == null || user.getBalance().compareTo(totale) < 0) {
            throw new RuntimeException("Saldo insufficiente per completare l'ordine");
        }
        // Creazione ordine e associazione utente, data e totale
        Ordine ordine = new Ordine();
        ordine.setUser(user);
        ordine.setDataOrdine(LocalDateTime.now());
        ordine.setTotale(totale);
        // Trasformazione degli articoli del carrello in voci d'ordine
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

        // Scala il saldo dell'utente in base al totale dell'ordine
        BigDecimal nuovoSaldo = user.getBalance().subtract(totale);
        user.setBalance(nuovoSaldo);

        // poi salva ordine e utente (per aggiornare la balance)
        Ordine ordineAggiornato = ordineRepository.save(ordine);
        userRepository.save(user);

        // svuota il carrello
        carrelloService.svuotaCarrello(user.getId());

        return EntityToDTO(ordineAggiornato);
    }

    /**
     * Recupera la lista di ordini dell'utente attualmente autenticato.
     * 
     * @return lista di OrdineDTO rappresentanti gli ordini effettuati dall'utente
     */
    public List<OrdineDTO> getOrdiniUtente() {
        User user = securityService.getActiveUser();

        return ordineRepository.findByUserOrderByDataOrdineDesc(user).stream()
                .map(this::EntityToDTO)
                .collect(Collectors.toList());
    }


    /**
     * Converte un'entità Ordine in un DTO OrdineDTO, inclusi gli articoli
     * associati.
     * 
     * @param ordine entità Ordine da convertire
     * @return DTO OrdineDTO corrispondente
     */
    public OrdineDTO EntityToDTO(Ordine ordine) {
        if (ordine == null) {
            return null;
        }

        // Mappatura livelli più alti: id, utenteId, dataOrdine, totale
        OrdineDTO dto = modelMapper.map(ordine, OrdineDTO.class);

        // Mappatura manuale della lista articoli
        dto.setArticoli(ordine.getArticoli().stream()
                .map(articoloOrdine -> {
                    // Ottiene l'entità articolo completa dal DB
                    Article article = articleRepository.findById(articoloOrdine.getArticoloId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Article not found with id: " + articoloOrdine.getArticoloId()));

                    // Mappatura Article -> ArticleDTO
                    ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);

                    // Creazione ArticoloQuantitaDTO
                    return new ArticoloQuantitaDTO(articleDTO, articoloOrdine.getQuantita());
                })
                .collect(Collectors.toList()));

        return dto;
    }
}
