package com.smartbay.progettofinale.DTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.smartbay.progettofinale.Models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per rappresentare le informazioni personali complete dell'utente attualmente autenticato.
 * 
 * Contiene dati sensibili come il saldo, ed è pensato esclusivamente per l'accesso dell'utente
 * stesso (es. nella pagina dashboard).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoDTO {

    /** Identificativo univoco dell'utente */
    private Long id;

    /** Username dell'utente */
    private String username;

    /** Indirizzo email dell'utente */
    private String email;

    /** Ruoli dell'utente (es. ROLE_USER, ROLE_ADMIN, ecc.) */
    private List<Role> roles = new ArrayList<>();

    /** Saldo attuale dell'utente (dato sensibile) */
    private BigDecimal balance;
}
