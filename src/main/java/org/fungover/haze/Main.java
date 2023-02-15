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
    public static void main(String[] args) throws IOException {
        Initialize initialize = new Initialize();
        initialize.importCliOptions(args);

        HazeDatabase hazeDatabase = new HazeDatabase();

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(initialize.getPort()));
            while (true) {
                var client = serverSocket.accept();
                Log4j2.debug(String.valueOf(client));
                Log4j2.info("Application started: serverSocket.accept()");

                Runnable newThread = () -> {
                    try {
                        BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));

                        while (true) {
                            List<String> inputList = new ArrayList<>();

                            String firstReading = input.readLine();
                            readInputStream(input, inputList, firstReading);

                            client.getOutputStream().write(executeCommand(hazeDatabase, inputList).getBytes());

                            inputList.forEach(System.out::println); // For checking incoming message

                            printThreadDebug();

                        }
                        } catch(IOException e){
                            Log4j2.error(String.valueOf(e));
                        }

                    Log4j2.info("Client closed");
                };
                Thread.startVirtualThread(newThread);
            }
        } catch (IOException e) {
            Log4j2.error(String.valueOf(e));
        }
    }



    private static void printThreadDebug() {
        Log4j2.debug("ThreadID " + Thread.currentThread().threadId());  // Only for Debug
        Log4j2.debug("Is virtual Thread " + Thread.currentThread().isVirtual()); // Only for Debug
    }

    public static String executeCommand(HazeDatabase hazeDatabase, List<String> inputList) {
        Log4j2.debug("executeCommand: " + hazeDatabase + " " + inputList);
        String command = inputList.get(0).toUpperCase();

        return switch (command) {
            case "SETNX" -> hazeDatabase.setNX(inputList);
            case "SAVE" -> SaveFile.writeOnFile(hazeDatabase.copy());
            case "DEL" -> hazeDatabase.delete(inputList.subList(1, inputList.size()));
            default -> "-ERR unknown command\r\n";
        };
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
