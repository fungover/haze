package org.fungover.haze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Initialize initialize = new Initialize();
        initialize.importCliOptions(args);

        HazeDatabase hazeDatabase = new HazeDatabase();

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(initialize.getPort()));
            while (true) {
                Socket client = serverSocket.accept();
                handleClient(hazeDatabase, client);
            }
        } catch (IOException e) {
            Log4j2.error(String.valueOf(e));
        }
    }

    private static void handleClient(HazeDatabase hazeDatabase, Socket client) {
        Log4j2.debug(String.valueOf(client));
        Log4j2.info("Application started: serverSocket.accept()");

        Runnable newThread = () -> {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                List<String> inputList = parseInput(input);

                executeCommand(hazeDatabase, client, inputList);

                inputList.forEach(System.out::println); // For checking incoming message

                printThreadDebug();

                client.close();
                Log4j2.info("Client closed");

            } catch (IOException e) {
                Log4j2.error(String.valueOf(e));
            }
        };
        Thread.startVirtualThread(newThread);
    }

    private static List<String> parseInput(BufferedReader input) throws IOException {
        String firstReading = input.readLine();
        Log4j2.debug("parseInput: " + firstReading);

        List<String> inputList = new ArrayList<>();

        if (firstReading.startsWith("*")) {
            int size = Integer.parseInt(firstReading.substring(1)) * 2;
            for (int i = 0; i < size; i++) {
                String temp = input.readLine();
                if (!temp.contains("$"))
                    inputList.add(temp);
            }
        } else {
            String[] separated = firstReading.split("\\s");
            inputList.addAll(Arrays.asList(separated));
        }

        return inputList;
    }

    private static void executeCommand(HazeDatabase hazeDatabase, Socket client, List<String> inputList) throws IOException {
        Log4j2.debug("executeCommand: " + hazeDatabase + " " + client + " " + inputList);

        String command = inputList.get(0);
        String key = inputList.get(1);
        String value = getValueIfExist(inputList);

        if (command.equals("SETNX")) {
            client.getOutputStream().write(hazeDatabase.setNX(key, value).getBytes());
        } else {
            client.getOutputStream().write("-ERR unknown command\r\n".getBytes());
        }
    }

    private static String getValueIfExist(List<String> inputList) {
        Log4j2.debug("getValueIfExist: " + inputList);
        return inputList.size() == 3 ? inputList.get(2) : "";
    }

    private static void printThreadDebug() {
        Log4j2.debug("ThreadID " + Thread.currentThread().threadId());  // Only for Debug
        Log4j2.debug("Is virtual Thread " + Thread.currentThread().isVirtual()); // Only for Debug
    }
}
