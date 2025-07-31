package com.smartbay.progettofinale.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import com.smartbay.progettofinale.DTO.OrdineDTO;
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
    public ModelAndView confermaOrdine(RedirectAttributes redirectAttributes) {
        try {
            ordineService.creaOrdine();
            redirectAttributes.addFlashAttribute("cartSuccess", "Ordine effettuato con successo.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("cartWarning", ex.getMessage());
        }
        return new ModelAndView("redirect:/carrello");
    }

    @GetMapping("")
    public ResponseEntity<List<OrdineDTO>> getOrdiniUtente() {
        return ResponseEntity.ok(ordineService.getOrdiniUtente());
    }
}