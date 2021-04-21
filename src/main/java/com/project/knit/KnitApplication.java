package com.project.knit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class KnitApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnitApplication.class, args);
	}

}
