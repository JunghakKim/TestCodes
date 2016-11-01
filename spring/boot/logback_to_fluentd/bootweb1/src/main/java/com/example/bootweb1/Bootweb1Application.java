package com.example.bootweb1;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/*
@SpringBootApplication
public class Bootweb1Application {

	public static void main(String[] args) {
		SpringApplication.run(Bootweb1Application.class, args);
	}
}
*/

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Bootweb1Application extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Bootweb1Application.class);
	}
}
