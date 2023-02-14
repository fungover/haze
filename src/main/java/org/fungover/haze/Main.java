package org.fungover.haze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        HazeDatabase hazeDatabase = new HazeDatabase();

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(6379));
            while (true) {
                var client = serverSocket.accept();

                Runnable newThread = () -> {
                    try {
                        BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        List<String> inputList = new ArrayList<>();

                        String firstReading = input.readLine();
                        readInputStream(input, inputList, firstReading);

                        client.getOutputStream().write(executeCommand(hazeDatabase, inputList).getBytes());

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

    public static String executeCommand(HazeDatabase hazeDatabase, List<String> inputList) {
        String command = inputList.get(0);

        return switch (command) {
            case "SETNX" -> hazeDatabase.setNX(inputList);
            default -> "-ERR unknown command\r\n";
        };
    }

    private static void readInputStream(BufferedReader input, List<String> inputList, String firstReading) throws IOException {
        int size;
        if (firstReading.startsWith("*")) {
            size = Integer.parseInt(firstReading.substring(1)) * 2;
            for (int i = 0; i < size; i++) {
                String temp = input.readLine();
                if (!temp.contains("$"))
                    inputList.add(temp);
            }
        } else {
            String[] seperated = firstReading.split("\\s");
            inputList.addAll(Arrays.asList(seperated));
        }
    }
}
