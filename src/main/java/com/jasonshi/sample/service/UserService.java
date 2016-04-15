package com.jasonshi.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jasonshi.sample.entity.User;
import com.jasonshi.sample.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepo;
	
	@Transactional(readOnly = true)
	public User findUser(String lastName) {
		if(lastName == null){
			throw new IllegalArgumentException("last name is null.");
		}
		
		return userRepo.findByLastName(lastName);
	}
}
