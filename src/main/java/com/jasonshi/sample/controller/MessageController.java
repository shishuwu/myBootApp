package com.jasonshi.sample.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jasonshi.sample.entity.Message;

/**
 * Provide REST API for /message.
 * 
 * <pre>
 * 1. read java object
 * 2. process
 * 3. return java object
 * </pre>
 * 
 * @author shishuwu
 *
 */
@Controller
//@Scope("singleton")
@RequestMapping("message")
public class MessageController {
	
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST)
	public Message sendMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Message message = null;
		ObjectInputStream objIn = new ObjectInputStream(req.getInputStream());
		try {
			message = (Message) objIn.readObject();
			message.setContent(message.getContent() + " updated");

			ObjectOutput objOut = new ObjectOutputStream(resp.getOutputStream());
			objOut.writeObject(message);
			objOut.flush();
			objOut.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return message;
	}
}