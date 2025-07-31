package com.smartbay.progettofinale;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Classe principale di avvio dell'applicazione Spring Boot.
 * 
 * Annotazioni:
 * - @SpringBootApplication: abilita la configurazione automatica, il component
 * scanning e altre funzionalità Spring Boot.
 * - @EnableAsync(proxyTargetClass = true): abilita l'esecuzione asincrona dei
 * metodi contrassegnati con @Async, usando proxy di classe.
 * - @EnableTransactionManagement: abilita la gestione delle transazioni tramite
 * annotazioni @Transactional.
 * 
 * Contiene il metodo main per avviare l'applicazione.
 * 
 * Definisce anche due bean Spring:
 * - PasswordEncoder: istanza di BCryptPasswordEncoder usata per criptare le
 * password in modo sicuro.
 * - ModelMapper: istanza di ModelMapper per facilitare la conversione tra DTO e
 * entità.
 */

@EnableAsync(proxyTargetClass = true)
@EnableTransactionManagement
@SpringBootApplication
public class ProgettofinaleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgettofinaleApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ModelMapper instanceModelMapper() {
		ModelMapper mapper = new ModelMapper();
		return mapper;
	}

}
