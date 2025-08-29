package com.smartbay.progettofinale.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object per i dati dell'utente.
 * <p>
 * Utilizzato per la gestione dei dati di registrazione e login.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    /**
     * Identificativo univoco dell'utente.
     */
    private Long id;

    /**
     * Nome dell'utente.
     */
    @NotEmpty
    private String firstName;

    /**
     * Cognome dell'utente.
     */
    @NotEmpty
    private String lastName;

    /**
     * Indirizzo email dell'utente.
     */
    @NotEmpty(message = "Required field Email must not be empty..")
    @Email
    private String email;

    /**
     * Password dell'utente.
     */
    @NotEmpty(message = "Required field Password must not be empty..")
    private String password;
    
}
