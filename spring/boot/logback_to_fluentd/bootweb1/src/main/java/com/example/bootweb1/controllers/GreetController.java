package com.example.bootweb1.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GreetController {
	
	static final Logger LOGGER = LoggerFactory.getLogger(GreetController.class);
	
	@RequestMapping("/greet/{name}")
	public String greet(@PathVariable("name") String name) {
		LOGGER.info("[Test API] Requested name is " + name);
		return "greet";
	}
}
