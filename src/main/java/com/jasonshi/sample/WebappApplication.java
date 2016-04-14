package com.jasonshi.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.jasonshi.sample.entity.Device;
import com.jasonshi.sample.repository.DeviceRepository;
import com.jasonshi.sample.service.DeviceService;

@SpringBootApplication
public class WebappApplication {
	private static final Logger LOG = LoggerFactory.getLogger(WebappApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(WebappApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(DeviceRepository personRepo, DeviceService personService) {
		return (args -> {
			personRepo.save(new Device("d1", "this is device 1"));
			personRepo.save(new Device("d2", "this is device 2"));
			personRepo.save(new Device("d3", "this is device 3"));
			try {
				personRepo.save(new Device(null, "this is device 3"));
			} catch (Exception e) {
				LOG.error("device name cannot be null", e);
			}
			
			
			// fetch
			Iterable<Device> persons = personRepo.findAll();
			for (Device p : persons) {
				LOG.info(p.toString());
			}
			
			LOG.info(personRepo.findOne(1L).toString());
			LOG.info(personRepo.findByName("d2`").toString());
			
			// service
			try {
				personService.findDevice(null);	
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
			
			LOG.info(personService.findDevice("d3").toString());
		});
	}
}
