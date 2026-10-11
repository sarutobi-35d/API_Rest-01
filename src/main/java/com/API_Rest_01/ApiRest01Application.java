package com.API_Rest_01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ApiRest01Application {

	public static void main(String[] args) {
		SpringApplication.run(ApiRest01Application.class, args);
	}

}
