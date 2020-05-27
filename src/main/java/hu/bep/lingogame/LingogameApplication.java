package hu.bep.lingogame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"hu.bep.presentation", "hu.bep.persistence"})
public class LingogameApplication {

	public static void main(String[] args) {
		SpringApplication.run(LingogameApplication.class, args);
	}

}
