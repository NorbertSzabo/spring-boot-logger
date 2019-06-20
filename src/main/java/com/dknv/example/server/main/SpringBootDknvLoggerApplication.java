package com.dknv.example.server.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication( scanBasePackages = {"com.dknv"})
public class SpringBootDknvLoggerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDknvLoggerApplication.class, args);
	}

}
