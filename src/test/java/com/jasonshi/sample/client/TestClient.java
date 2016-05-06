package com.jasonshi.sample.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jasonshi.sample.entity.Message;

public class TestClient {
	public static String fileContent_10K;
	public static String fileContent_10M;
	public static String fileContent_100M;
	public static String fileContent_500M;

	@BeforeClass
	public static void setup() throws Exception {
		fileContent_10K = readFile(".\\src\\main\\java\\com\\jasonshi\\sample\\client\\10K.txt");
		fileContent_10M = readFile(".\\src\\main\\java\\com\\jasonshi\\sample\\client\\10M.txt");
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

		for (int i = 1; i < 10; i++) {
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
		String content = fileContent_500M + fileContent_500M;
		createAndSendMessage(0, content);
	}

	// @Test
	public void test_readFromFile_100() throws Exception {
		for (int i = 100; i < 200; i++) {

			System.out.print("number: " + i + " ");
			createAndSendMessage(i, fileContent_10M);
		}
	}

	@Test
	public void test_readFromFile_100_concurrent() throws InterruptedException, ExecutionException {
		ExecutorService exector = Executors.newCachedThreadPool();
		AtomicInteger id = new AtomicInteger(10000);

		List<Future<Message>> futures = new ArrayList<>();
		
		for (int i = 0; i < 50; i++) {
			Future<Message> future = exector.submit(new Callable<Message>() {
				@Override
				public Message call() throws Exception {
					return createAndSendMessage(id.getAndIncrement(), fileContent_10M);
				}
			});
			futures.add(future);
		}
		
		for(Future<Message> f: futures){
			System.out.println(f.get().getContent().length());
		}
	}

	@Test
	public void test_readFromSingleHugeFile() {
		//String content = fileContent_500M;
		createAndSendMessage(0, fileContent_10M);
	}

	// ========================================================================

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

		msg = Client.sendMessage(msg);

		Assert.assertTrue(msg.getContent().endsWith(" updated"));
		System.out.println("size: " + msg.getContent().length());

		return msg;
	}
}
