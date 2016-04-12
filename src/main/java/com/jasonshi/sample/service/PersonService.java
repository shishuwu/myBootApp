package com.jasonshi.sample.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jasonshi.sample.entity.Person;
import com.jasonshi.sample.repo.PersonRepository;

@Service
public class PersonService {
	
	@Autowired
	private PersonRepository personRepo;
	
	@Transactional(readOnly = true)
	public List<Person> findPerson(String lastName) {
		if(lastName == null){
			throw new IllegalArgumentException("last name is null.");
		}
		
		return personRepo.findByLastName(lastName);
	}
	
	@Transactional
	public void deletePerson(Long personId){
		if(personId == null){
			throw new IllegalArgumentException("person id is null.");
		}
		
		personRepo.delete(personId);
	}
}
