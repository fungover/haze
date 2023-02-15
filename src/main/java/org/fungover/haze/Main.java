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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) throws IOException {
        Initialize initialize = new Initialize();
        HazeDatabase hazeDatabase = new HazeDatabase();
        Auth auth = new Auth();
        Lock lock = new ReentrantLock();
        initializeServer(args, initialize, auth);

        final boolean isPasswordSet = auth.isPasswordSet();


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
    }


    private static void printThreadDebug() {
        Log4j2.debug("ThreadID " + Thread.currentThread().threadId());  // Only for Debug
        Log4j2.debug("Is virtual Thread " + Thread.currentThread().isVirtual()); // Only for Debug
    }

    private static void checkPassword(Auth auth, boolean[] passwordSet, Lock lock, Socket client, List<String> inputList) {
        if (!passwordSet[0] || inputList.contains("AUTH")) {
            lock.lock();
            passwordSet[0] = (auth.authenticate(inputList.get(1), client));
            lock.unlock();
        }
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

    private static void initializeServer(String[] args, Initialize initialize, Auth auth) {
        initialize.importCliOptions(args);
        auth.setPassword(initialize.getPassword());
    }
}
