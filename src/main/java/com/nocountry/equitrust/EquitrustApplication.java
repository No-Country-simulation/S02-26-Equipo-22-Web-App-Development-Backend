package com.nocountry.equitrust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.nocountry")
public class EquitrustApplication {

	public static void main(String[] args) {
		SpringApplication.run(EquitrustApplication.class, args);
	}

}
