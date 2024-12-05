package com.ecommerce.gaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class GameUniverseApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameUniverseApplication.class, args);
	}

}
