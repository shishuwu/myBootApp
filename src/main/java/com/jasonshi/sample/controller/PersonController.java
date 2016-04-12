package com.jasonshi.sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jasonshi.sample.service.PersonService;

@Controller
@RequestMapping("persons")
public class PersonController {
	@Autowired
	private PersonService personService;

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.DELETE)
	public void deletePerson(@RequestParam(value = "personId", defaultValue = "1") Long personId) {
		personService.deletePerson(personId);
	}
}
