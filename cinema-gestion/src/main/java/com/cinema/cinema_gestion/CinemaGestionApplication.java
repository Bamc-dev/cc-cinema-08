package com.cinema.cinema_gestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Point d'entrée Spring Boot de l'API Cinema Gestion.
 */
@SpringBootApplication
@EnableJpaRepositories
public class CinemaGestionApplication {

	/**
	 * Démarre le contexte Spring Boot.
	 *
	 * @param args arguments de la ligne de commande
	 */
	public static void main(String[] args) {
		SpringApplication.run(CinemaGestionApplication.class, args);
	}

}
