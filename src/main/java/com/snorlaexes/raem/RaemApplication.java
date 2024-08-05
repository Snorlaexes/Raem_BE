package com.snorlaexes.raem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
public class RaemApplication {

	public static void main(String[] args) {
		SpringApplication.run(RaemApplication.class, args);
	}

}
