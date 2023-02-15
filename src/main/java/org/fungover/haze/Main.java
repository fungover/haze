package org.fungover.haze;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    static boolean serverOpen = true;
    public static void main(String[] args) throws IOException {


        Initialize initialize = new Initialize();
        initialize.importCliOptions(args);


        HazeDatabase hazeDatabase = new HazeDatabase();
        Thread printingHook = new Thread(Main::shutdown);
        Runtime.getRuntime().addShutdownHook(printingHook);


        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(initialize.getPort()));
            while (serverOpen) {
                var client = serverSocket.accept();
                Log4j2.debug(String.valueOf(client));
                Log4j2.info("Application started: serverSocket.accept()");

                Runnable newThread = () -> {
                    try {
                        BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));

                        List<String> inputList = new ArrayList<>();

                        String firstReading = input.readLine();
                        readInputStream(input, inputList, firstReading);

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
        } catch (IOException e) {
            Log4j2.error(String.valueOf(e));

        }
        Log4j2.info("Shutting down....");
    }

    private static void shutdown() {
        //Todo: Replace with logging messages
        System.out.println("Shutting down...");
        //Todo: Save data to file before application shuts down
        System.out.println("Shutdown Done.");
    }


    private static void printThreadDebug() {
        Log4j2.debug("ThreadID " + Thread.currentThread().threadId());  // Only for Debug
        Log4j2.debug("Is virtual Thread " + Thread.currentThread().isVirtual()); // Only for Debug
    }

    private static void executeCommand(HazeDatabase hazeDatabase, Socket client, List<String> inputList) throws
            IOException {
        Log4j2.debug("executeCommand: " + hazeDatabase + " " + client + " " + inputList);
        String command = inputList.get(0);
        String key = inputList.get(1);
        String value = getValueIfExist(inputList);

        switch (command) {
            case "SETNX" -> client.getOutputStream().write(hazeDatabase.setNX(key, value).getBytes());
            default -> client.getOutputStream().write("-ERR unknown command\r\n".getBytes());
        }
    }

    private static String getValueIfExist(List<String> inputList) {
        Log4j2.debug("getValueIfExist: " + inputList);
        if (inputList.size() == 3)
            return inputList.get(2);
        return "";
    }

    private static void readInputStream(BufferedReader input, List<String> inputList, String firstReading) throws
            IOException {
        Log4j2.debug("readInputStream: " + input + " " + inputList + " " + firstReading);
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
