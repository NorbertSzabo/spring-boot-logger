package com.dknv.example.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dknv.example.beans.Greeting;

@Controller
public class GreetingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GreetingController.class);

	@RequestMapping (
			method = RequestMethod.GET,
			value = "/sayHello"
			)
	@ResponseBody
	public String getGreeting() {
		testLogger();
		Greeting greeting = new Greeting("Hello", "Joe");
		return greeting.toString();
	}

	private void testLogger() {
		LOGGER.debug("Debug Message Logged !!!");
		LOGGER.info("Info Message Logged !!!");
		LOGGER.error("Error Message Logged !!!", new NullPointerException("NullError"));
	}


}
