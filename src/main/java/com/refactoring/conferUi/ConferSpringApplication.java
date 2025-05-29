package com.refactoring.conferUi;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.refactoring.conferUi.Model.Entity")
@ComponentScan(basePackages = "com.refactoring")
@EnableJpaRepositories("com.refactoring.conferUi.dao")
public class ConferSpringApplication {

	public static void main(String[] args) {
		// Inicializa o Spring Boot primeiro
		ConfigurableApplicationContext context = SpringApplication.run(ConferSpringApplication.class, args);

		// Configura o contexto na aplicação JavaFX
		ConferApplication.setSpringContext(context);

		// Inicializa o JavaFX
		Application.launch(ConferApplication.class, args);
	}
}