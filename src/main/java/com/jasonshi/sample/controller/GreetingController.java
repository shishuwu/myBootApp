package com.jasonshi.sample.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jasonshi.sample.model.Greeting;

@RestController
public class GreetingController {
	private static final String template = "Hello, %s!";
	private AtomicLong counter = new AtomicLong();

	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "King of the world") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
}
