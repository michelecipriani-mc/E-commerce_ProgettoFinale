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

    public OrdineController(OrdineService ordineService, SecurityService securityService) {
        this.ordineService = ordineService;
    }

    @PostMapping("/conferma")
    public ResponseEntity<Ordine> confermaOrdine() {
        Ordine ordine = ordineService.creaOrdine();
        return ResponseEntity.ok(ordine);
    }

    @GetMapping("")
    public ResponseEntity<List<Ordine>> getOrdiniUtente() {
        return ResponseEntity.ok(ordineService.getOrdiniUtente());
    }
}