package com.nocountry.equitrust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class EquitrustApplication {

	public static void main(String[] args) {
		SpringApplication.run(EquitrustApplication.class, args);
	}

}
