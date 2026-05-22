package com.demo.demoproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class DemoprojectApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoprojectApplication.class, args);
	}
}
