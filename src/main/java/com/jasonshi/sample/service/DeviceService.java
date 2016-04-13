package com.jasonshi.sample.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jasonshi.sample.entity.Device;
import com.jasonshi.sample.repository.DeviceRepository;

@Service
public class DeviceService {
	
	@Autowired
	private DeviceRepository deviceRepo;
	
	@Transactional(readOnly = true)
	public List<Device> findDevice(String name) {
		if(name == null){
			throw new IllegalArgumentException("device name is null.");
		}
		
		return deviceRepo.findByName(name);
	}
	
	@Transactional(readOnly = true)
	public List<Device> findDevices() {
		Iterable<Device> it = deviceRepo.findAll();
		List<Device> devices = new ArrayList<>();
		for(Device d: it){
			devices.add(d);
		}
		return devices;
	}
	
	@Transactional
	public void deleteDevice(Long personId){
		if(personId == null){
			throw new IllegalArgumentException("person id is null.");
		}
		
		deviceRepo.delete(personId);
	}
}
