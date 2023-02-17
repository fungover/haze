package org.fungover.haze;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    static boolean serverOpen = true;
    static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        Initialize initialize = new Initialize();
        initialize.importCliOptions(args);

        HazeDatabase hazeDatabase = new HazeDatabase();
        HazeList hazeList = new HazeList();

        Thread printingHook = new Thread(() -> shutdown(hazeDatabase));
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

                        while (true) {
                            List<String> inputList = new ArrayList<>();

                            String firstReading = input.readLine();
                            readInputStream(input, inputList, firstReading);

                            client.getOutputStream().write(executeCommand(hazeDatabase, inputList,hazeList ).getBytes());

                            inputList.forEach(System.out::println); // For checking incoming message

                            printThreadDebug();
                        }

                    } catch (IOException e) {
                        Log4j2.error(String.valueOf(e));
                    }
                    Log4j2.info("Client closed");
                };
                Thread.startVirtualThread(newThread);
            }
        } catch (IOException e) {
            Log4j2.error(String.valueOf(e));
        }
        Log4j2.info("Shutting down....");
    }

    private static void shutdown(HazeDatabase hazeDatabase) {
        SaveFile.writeOnFile(hazeDatabase.copy());
        logger.info("Shutting down....");
    }

    private static void printThreadDebug() {
        Log4j2.debug("ThreadID " + Thread.currentThread().threadId());  // Only for Debug
        Log4j2.debug("Is virtual Thread " + Thread.currentThread().isVirtual()); // Only for Debug
    }

    public static String executeCommand(HazeDatabase hazeDatabase, List<String> inputList, HazeList hazeList) {
        Log4j2.debug("executeCommand: " + hazeDatabase + " " + inputList);
        String command = inputList.get(0).toUpperCase();

        String key = null;
        if (inputList.size() > 1)
            key = inputList.get(1);

        return switch (command) {
            case "SET" -> hazeDatabase.set(inputList);
            case "GET" -> hazeDatabase.get(inputList);
            case "DEL" -> hazeDatabase.delete(inputList.subList(1, inputList.size()));
            case "PING" -> hazeDatabase.ping(inputList);
            case "SETNX" -> hazeDatabase.setNX(inputList);
            case "EXISTS" -> hazeDatabase.exists(inputList.subList(1, inputList.size()));
            case "SAVE" -> SaveFile.writeOnFile(hazeDatabase.copy());
            case "RPUSH" -> hazeList.rPush(inputList);
            case "LPUSH" -> hazeList.lPush(inputList);
            case "LPOP" -> hazeList.callLPop(inputList);
            case "RPOP" -> hazeList.callRpop(inputList);
            case "LLEN" -> hazeList.lLen(key);
            case "LMOVE" -> hazeList.lMove(inputList);
            case "LTRIM" -> hazeList.callLtrim(inputList);
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
