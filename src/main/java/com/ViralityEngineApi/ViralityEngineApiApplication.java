package com.ViralityEngineApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ViralityEngineApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ViralityEngineApiApplication.class, args);
	}

}
