package com.jasonshi.sample.client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jasonshi.sample.entity.Message;

/**
 * This is only a client code which should not be deployed on servlet container.
 * 
 * @author shishuwu
 */
public class Client {

	public static String fileContent;
	public static String fileContent_100M;
	public static String fileContent_500M;

	@BeforeClass
	public static void setup() throws Exception {
		fileContent = readFile(".\\src\\main\\java\\com\\jasonshi\\sample\\client\\payload.txt");
		fileContent_100M = readFile("C:\\shishuwu\\Documents\\4.8\\HTTP\\files\\jenkins-1.616.zip");
		fileContent_500M = readFile("C:\\shishuwu\\Documents\\4.8\\HTTP\\files\\test.vmdk");
	}

	@Test
	public void testSingle() {
		createAndSendMessage(0);
	}

	@Test
	public void test100() {
		for (int i = 0; i < 100; i++) {
			createAndSendMessage(i);
		}
	}

	@Test
	public void test_concurrent() {
		ExecutorService exector = Executors.newCachedThreadPool();
		AtomicInteger id = new AtomicInteger(200);

		for (int i = 200; i < 300; i++) {
			exector.submit(new Callable<Message>() {
				@Override
				public Message call() throws Exception {
					return createAndSendMessage(id.getAndIncrement());
				}
			});
		}
	}

	@Test
	public void test_readFromFile() throws Exception {
		createAndSendMessage(0, fileContent);
	}

	// @Test
	public void test_readFromFile_100() throws Exception {
		for (int i = 100; i < 200; i++) {

			System.out.print("number: " + i + " ");
			createAndSendMessage(i, fileContent);
		}
	}

	// @Test
	public void test_readFromFile_100_concurrent() {
		ExecutorService exector = Executors.newCachedThreadPool();
		AtomicInteger id = new AtomicInteger(10000);

		for (int i = 200; i < 300; i++) {
			exector.submit(new Callable<Message>() {
				@Override
				public Message call() throws Exception {
					return createAndSendMessage(id.getAndIncrement(), fileContent);
				}
			});
		}
	}

	@Test
	public void test_readFromSingleHugeFile() {
		createAndSendMessage(0, fileContent_100M);
	}

	// ==========================================================================================================

	private static String readFile(String file) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			return stringBuilder.toString();
		} finally {
			reader.close();
		}
	}

	private static Message createAndSendMessage(int id) {
		return createAndSendMessage(id, null);
	}

	private static Message createAndSendMessage(int id, String content) {
		if (content == null) {
			content = "content " + id;
		}
		Message msg = new Message(id, content);

		msg = sendMessage(msg);

		Assert.assertTrue(msg.getContent().endsWith(" updated"));
		System.out.println("size: " + msg.getContent().length());

		return msg;
	}

	private static Message sendMessage(Message message) {
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
