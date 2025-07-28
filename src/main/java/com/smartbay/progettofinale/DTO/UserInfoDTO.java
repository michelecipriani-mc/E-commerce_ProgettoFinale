package com.smartbay.progettofinale.DTO;

import java.util.ArrayList;
import java.util.List;
import com.smartbay.progettofinale.Models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object per rappresentare le informazioni pubbliche di un utente.
 * 
 * Viene utilizzato per trasferire dati tra backend e frontend, per mostrare i profili di altri
 * utenti nel sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    /** Identificativo univoco dell'utente */
    private Long id;

    /** Username dell'utente */
    private String username;

    /** Indirizzo email dell'utente */
    private String email;

    /** Ruoli associati all'utente (es. ROLE_USER, ROLE_ADMIN, ecc.) */
    private List<Role> roles = new ArrayList<>();
}
