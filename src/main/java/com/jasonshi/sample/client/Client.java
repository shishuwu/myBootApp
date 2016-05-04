package com.jasonshi.sample.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.jasonshi.sample.entity.Message;

/**
 * This is only a client code which should not be deployed on servlet container.
 * 
 * @author shishuwu
 */
public class Client {

	public static void main(String[] args) {

		ExecutorService exector = Executors.newCachedThreadPool();
		AtomicInteger id = new AtomicInteger(0);

		for (int i = 0; i < 100; i++) {
			exector.execute(new Runnable() {
				@Override
				public void run() {
					Message msg = new Message(id.incrementAndGet(), "content " + id.get());
					System.out.println("orignal: " + msg);

					msg = sendMessage(msg);

					System.out.println("new: " + msg);
					System.out.println("=================================");
				}
			});
		}
	}

	public static Message sendMessage(Message message) {
		URLConnection conn = null;
		Message reply = null;
		try {

			// open URL connection
			URL url = new URL("http://localhost:8080/message");
			conn = url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			// send object
			ObjectOutputStream objOut = new ObjectOutputStream(conn.getOutputStream());
			objOut.writeObject(message);
			objOut.flush();
			objOut.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		// recieve reply
		try {
			ObjectInputStream objIn = new ObjectInputStream(conn.getInputStream());
			reply = (Message) objIn.readObject();
			objIn.close();
		} catch (Exception ex) {
			// it is ok if we get an exception here
			// that means that there is no object being returned
			System.out.println("No Object Returned");
			if (!(ex instanceof EOFException))
				ex.printStackTrace();
			System.err.println("*");
		}
		return reply;
	}
}
