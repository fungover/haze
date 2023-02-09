package org.fungover.haze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {

		try (ServerSocket serverSocket = new ServerSocket(6379)) {
			while (true) {
				var client = serverSocket.accept();

				Runnable newThread = () -> {
					try {


                        BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        List<String> inputList = new ArrayList<>();

                        String firstReading = input.readLine();
                        readInputStream(input, inputList, firstReading);

                        inputList.forEach(System.out::println); // For checking incoming message

						printThreadDebug();

						client.close();

					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				};
				Thread.startVirtualThread(newThread);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void printThreadDebug() {
		System.out.println("ThreadID " + Thread.currentThread().threadId());  // Only for Debug
		System.out.println("Is virtual Thread " + Thread.currentThread().isVirtual()); // Only for Debug
	}
    private static void readInputStream(BufferedReader input, List<String> inputList, String firstReading) throws IOException {
        int size;
        if (firstReading.startsWith("*")) {
            size = Integer.parseInt(firstReading.substring(1)) * 2;
            for (int i = 0; i < size; i++) {
                inputList.add(input.readLine());
            }
        } else
            inputList.add(firstReading);
    }
}
