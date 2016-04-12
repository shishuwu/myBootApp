package com.jasonshi.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.jasonshi.sample.entity.Person;
import com.jasonshi.sample.repo.PersonRepository;
import com.jasonshi.sample.service.PersonService;

@SpringBootApplication
public class WebappApplication {
	private static final Logger LOG = LoggerFactory.getLogger(WebappApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(WebappApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(PersonRepository personRepo, PersonService personService) {
		return (args -> {
			personRepo.save(new Person("Magic", "Y"));
			personRepo.save(new Person("Sky", "L"));
			personRepo.save(new Person("Ocean", "C"));
			
			// fetch
			Iterable<Person> persons = personRepo.findAll();
			for (Person p : persons) {
				LOG.info(p.toString());
			}
			
			LOG.info(personRepo.findOne(1L).toString());
			LOG.info(personRepo.findByLastName("L").toString());
			
			// service
			try {
				personService.findPerson(null);	
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
			
			LOG.info(personService.findPerson("C").toString());
		});
	}
}
