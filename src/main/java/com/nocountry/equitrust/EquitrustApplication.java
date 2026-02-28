package com.nocountry.equitrust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = "com.nocountry.equitrust")
@EnableJpaRepositories(basePackages = "com.nocountry.equitrust")
public class EquitrustApplication {

	public static void main(String[] args) {
		SpringApplication.run(EquitrustApplication.class, args);
	}

}
