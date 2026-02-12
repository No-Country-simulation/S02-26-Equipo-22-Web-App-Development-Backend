package com.nocountry.equitrust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EquitrustApplication {

	public static void main(String[] args) {
		SpringApplication.run(EquitrustApplication.class, args);
	}

}
