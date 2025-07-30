package com.smartbay.progettofinale.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartbay.progettofinale.Models.Ordine;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Security.SecurityService;
import com.smartbay.progettofinale.Services.OrdineService;


@RestController
@RequestMapping("/ordini")
public class OrdineController {

    private final OrdineService ordineService;
    private final SecurityService securityService;

    public OrdineController(OrdineService ordineService, SecurityService securityService) {
        this.ordineService = ordineService;
        this.securityService = securityService;
    }

    @PostMapping("/conferma")
    public ResponseEntity<Ordine> confermaOrdine() {
        User utente = securityService.getActiveUser(); // deve restituire User
        Ordine ordine = ordineService.creaOrdine(utente);
        return ResponseEntity.ok(ordine);
    }

    @GetMapping("")
    public ResponseEntity<List<Ordine>> getOrdiniUtente() {
        User utente = securityService.getActiveUser();
        return ResponseEntity.ok(ordineService.getOrdiniUtente(utente));
    }
}