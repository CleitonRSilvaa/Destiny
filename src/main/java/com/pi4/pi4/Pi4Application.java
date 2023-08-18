package com.pi4.pi4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Pi4Application {

	public static void main(String[] args) {
		SpringApplication.run(Pi4Application.class, args);
	}

}
