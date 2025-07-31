package com.smartbay.progettofinale.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) per rappresentare i dati di un utente.
 * Utilizzato per trasferire informazioni tra il client e il server,
 * ad esempio durante la registrazione o la modifica del profilo.
 * 
 * Contiene campi base dell'utente con le relative annotazioni di validazione:
 * - id: identificatore univoco dell'utente
 * - firstName: nome dell'utente, obbligatorio
 * - lastName: cognome dell'utente, obbligatorio
 * - email: indirizzo email, obbligatorio e deve essere in formato valido
 * - password: password dell'utente, obbligatoria
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty(message = "Required field Email must not be empty..")
    @Email
    private String email;

    @NotEmpty(message = "Required field Password must not be empty..")
    private String password;

}
