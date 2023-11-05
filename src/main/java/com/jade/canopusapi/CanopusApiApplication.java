package com.jade.canopusapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CanopusApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CanopusApiApplication.class, args);
	}

	@GetMapping("/api")
	public String hello() {
		return "Hello World";
	}
}
