package com.smartbay.progettofinale;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Classe di test per l'applicazione Spring Boot.
 * 
 * Annotata con @SpringBootTest per caricare il contesto Spring completo durante
 * i test.
 * 
 * Contiene un test di base chiamato contextLoads() che verifica semplicemente
 * che il contesto
 * dell'applicazione si carichi correttamente senza errori.
 * 
 * Questo test è utile come sanity check iniziale per assicurarsi che la
 * configurazione
 * Spring non abbia problemi.
 */

@SpringBootTest
class ProgettofinaleApplicationTests {

	@Test
	void contextLoads() {
	}

}
