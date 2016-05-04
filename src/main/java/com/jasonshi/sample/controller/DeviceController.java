package com.jasonshi.sample.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jasonshi.sample.entity.Device;
import com.jasonshi.sample.service.DeviceService;

@Controller
@RequestMapping("dvc")
public class DeviceController {
	@Autowired
	private DeviceService deviceService;

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteDevice(@RequestParam(value = "id", defaultValue = "1") Long personId) {
		deviceService.deleteDevice(personId);
	}
	
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET)
	public List<Device> findDevices() {
		return deviceService.findDevices();
	}
	
}
