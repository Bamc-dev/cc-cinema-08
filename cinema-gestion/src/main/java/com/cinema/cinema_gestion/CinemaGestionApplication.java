package com.cinema.cinema_gestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CinemaGestionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaGestionApplication.class, args);
	}

}
