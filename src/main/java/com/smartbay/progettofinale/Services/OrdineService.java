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
    // inserimento di tutte le DI
    private final CarrelloService carrelloService;
    private final ArticleRepository articleRepository;
    private final OrdineRepository ordineRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final ModelMapper modelMapper;

    // costruttore
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

    // METODO CREA ORDINE AGGIORNATO CON SCALO DALLA BALANCE
    @Transactional
    public OrdineDTO creaOrdine() {

        // Ottieni l'istanza dell'utente attivo
        User userInstance = securityService.getActiveUser();
        //controllo se l'istanza dell'utente è = null
        if (userInstance == null) {
            throw new IllegalStateException("Unauthenticated users cannot place orders.");
        }

        // Ottieni un oggetto utente gestito da JPA per la persistenza
        User user = userRepository.findById(userInstance.getId())
                .orElseThrow(() -> new IllegalArgumentException("No user by this id."));
        //costruisco il carrello per utente
        Carrello carrello = carrelloService.getCarrelloFromUtente(user.getId());
        //messaggio se il careelo è vuoto
        if (carrello.getArticles().isEmpty()) {
            throw new RuntimeException("Il carrello è vuoto");
        }
        //inizializzo una variabile totale che conterrà il prezzo totale del carrello
        BigDecimal totale = carrello.getPrezzoTotale(articleRepository);
        //verifico che l'utente abbia il saldo necessario per poter concludere l'ordine
        if (user.getBalance() == null || user.getBalance().compareTo(totale) < 0) {
            throw new RuntimeException("Saldo insufficiente per completare l'ordine");
        }
        //creo l'ordine 
        Ordine ordine = new Ordine();
        //vado a settare:
        ordine.setUser(user); //Utente
        ordine.setDataOrdine(LocalDateTime.now()); //data ordine
        ordine.setTotale(totale); //trotale prezzo
        //successivamente vadoa recuperare tutti gli articoli presenti nel carrello
        List<ArticoloOrdine> articoli = carrello.getArticles().entrySet().stream()
                //effettuo un amappatura
                .map(entry -> {
                    //associo:
                    Long idArticolo = entry.getKey(); //id dell'articolo
                    Integer quantita = entry.getValue(); //quantità
                    Article articolo = articleRepository.findById(idArticolo)
                            .orElseThrow(() -> new RuntimeException("Articolo non trovato")); //l'articolo andandolo a cercare tramite id, se non trovato mi ritornerà articolo non trovato.

                    //creo l'articolo ordine
                    ArticoloOrdine voce = new ArticoloOrdine();
                    //vado a settare:
                    voce.setArticoloId(idArticolo); //ID
                    voce.setTitoloArticolo(articolo.getTitle()); //Titolo
                    voce.setQuantita(quantita); //Quantità
                    voce.setPrezzoSingolo(articolo.getPrice()); //Prezzo
                    voce.setOrdine(ordine); //Ordine
                    //ritorno l'oggetto settato
                    return voce;
                })
                //trasformo tutta la mappatura in una lista 
                .collect(Collectors.toList());
        //ora posso settare l'ordine con tutti gli articoli recuperati dal carrello e inseriti nella lista di ArticoloOrdine -> articoli
        ordine.setArticoli(articoli);

        // qui scala il saldo
        BigDecimal nuovoSaldo = user.getBalance().subtract(totale);
        user.setBalance(nuovoSaldo);

        // poi salva ordine e utente (per aggiornare la balance)
        Ordine ordineAggiornato = ordineRepository.save(ordine);
        userRepository.save(user);

        // svuota il carrello
        carrelloService.svuotaCarrello(user.getId());
        //ritorna l'entità aggiornata
        return EntityToDTO(ordineAggiornato);
    }

    public List<OrdineDTO> getOrdiniUtente() {
        // Recuperiamo l'utente attualmente autenticato tramite il servizio di sicurezza
        User user = securityService.getActiveUser();
        //recuperiamo tutti gli ordini associati a questo utente
        return ordineRepository.findByUserOrderByDataOrdineDesc(user).stream()
                //mappiamo il tutto in un entità DTO
                .map(this::EntityToDTO)
                // Collezioniamo il risultato in una lista
                .collect(Collectors.toList());
    }

    public OrdineDTO EntityToDTO(Ordine ordine) {
        //se l'ordine è 0 null ritorniamo null
        if (ordine == null) {
            return null;
        }

        // Mappatura livelli più alti: id, utenteId, dataOrdine, totale
        OrdineDTO dto = modelMapper.map(ordine, OrdineDTO.class);

        // Mappatura manuale della lista articoli
        dto.setArticoli(ordine.getArticoli().stream()
                .map(articoloOrdine -> {

                    // Inserimento Titolo, Prezzo, Quantità per storico ordini della dashboard
                    ArticleDTO articoloDTO = new ArticleDTO();
                    //vado a settare:
                    articoloDTO.setTitle(articoloOrdine.getTitoloArticolo()); //Titolo
                    articoloDTO.setPrice(articoloOrdine.getPrezzoSingolo()); //Prezzo
                    
                    //ritorno il nuovo ArticoloQuantitaDTO
                    return new ArticoloQuantitaDTO(articoloDTO, articoloOrdine.getQuantita());
                })
                //trasformo il tutto in una lista
                .collect(Collectors.toList()));
        //ritorno tutto il l'OrdineDTO chiamato dto
        return dto;
    }
}
