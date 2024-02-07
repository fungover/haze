package org.fungover.haze;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    static boolean serverOpen = true;
    static Logger logger = LogManager.getLogger(Main.class);


    public static void main(String[] args) {


        Initialize initialize = new Initialize();
        initialize.importCliOptions(args);
        HazeDatabase hazeDatabase = new HazeDatabase();
        HazeList hazeList = new HazeList(hazeDatabase);
        Auth auth = new Auth();
        initializeServer(args, initialize, auth);
        final boolean isPasswordSet = auth.isPasswordSet();


        Thread printingHook = new Thread(() -> shutdown(hazeDatabase));
        Runtime.getRuntime().addShutdownHook(printingHook);

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(initialize.getPort()));
            while (serverOpen) {
                var client = serverSocket.accept();
                logger.info("Application started: serverSocket.accept()");

                Runnable newThread = () -> {
                    try {
                        BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        boolean clientAuthenticated = false;
                        while (true) {
                            List<String> inputList = new ArrayList<>();

                            String firstReading = input.readLine();

                            readInputStream(input,inputList,firstReading);

                            clientAuthenticated = authenticateClient(auth, isPasswordSet, client, inputList, clientAuthenticated);

                            client.getOutputStream().write(executeCommand(hazeDatabase, inputList, hazeList).getBytes());

                            inputList.forEach(System.out::println); // For checking incoming message

                            printThreadDebug();

                            inputList.clear();
                        }

                    } catch (IOException e) {
                        logger.error(e);
                    }
                };
                Thread.startVirtualThread(newThread);
            }
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("Shutting down....");
    }

    private static void shutdown(HazeDatabase hazeDatabase) {
        SaveFile.writeOnFile(hazeDatabase.copy());
        logger.info("Shutting down....");
    }


    public static void printThreadDebug() {
        logger.debug("ThreadID " + Thread.currentThread().threadId());  // Only for Debug
        logger.debug("Is virtual Thread " + Thread.currentThread().isVirtual()); // Only for Debug

    }

    public static String executeCommand(HazeDatabase hazeDatabase, List<String> inputList, HazeList hazeList) {
        if (inputList.isEmpty() || inputList.getFirst().isEmpty()) {
            return "-ERR no command provided\r\n";
        }

        logger.debug("executeCommand: {} {} ", () -> hazeDatabase, () -> inputList);
        String command = inputList.getFirst().toUpperCase();

        Command commandEnum;

        try {
            commandEnum = Command.valueOf(command);
        } catch (IllegalArgumentException ex) {
            return "-ERR unknown command\r\n";
        }

        return switch (commandEnum) {
            case SET -> hazeDatabase.set(inputList);
            case GET -> hazeDatabase.get(inputList);
            case DEL -> hazeDatabase.delete(inputList.subList(1, inputList.size()));
            case PING -> hazeDatabase.ping(inputList);
            case SETNX -> hazeDatabase.setNX(inputList);
            case EXISTS -> hazeDatabase.exists(inputList.subList(1, inputList.size()));
            case SAVE -> SaveFile.writeOnFile(hazeDatabase.copy());
            case RPUSH -> hazeList.rPush(inputList);
            case LPUSH -> hazeList.lPush(inputList);
            case LPOP -> hazeList.callLPop(inputList);
            case RPOP -> hazeList.callRPop(inputList);
            case LLEN -> hazeList.lLen(inputList);
            case LMOVE -> hazeList.lMove(inputList);
            case LTRIM -> hazeList.callLtrim(inputList);
            case LINSERT -> hazeList.linsert(inputList);
            case AUTH -> "+OK\r\n";
        };
    }


    private static void readInputStream(BufferedReader input, List<String> inputList, String firstReading) throws
            IOException {
        logger.debug("readInputStream: {} {} {}", () -> input, () -> inputList, () -> firstReading);
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

    private static boolean authenticateClient(Auth auth, boolean isPasswordSet, Socket client, List<String> inputList, boolean clientAuthenticated) throws IOException {
        if (authCommandReceived(isPasswordSet, inputList, clientAuthenticated))
            return auth.authenticate(inputList.get(1), client);

        shutdownClientIfNotAuthenticated(client, clientAuthenticated, isPasswordSet);
        return clientAuthenticated;
    }

    private static void shutdownClientIfNotAuthenticated(Socket client, boolean clientAuthenticated, boolean isPasswordSet) throws IOException {
        if (!clientAuthenticated && isPasswordSet) {
            client.getOutputStream().write(Auth.printAuthError());
            client.shutdownOutput();
        }
    }

    private static boolean authCommandReceived(boolean isPasswordSet, List<String> inputList, boolean clientAuthenticated) {
        return isPasswordSet && !clientAuthenticated && inputList.size() == 2 && inputList.getFirst().equals("AUTH");
    }
}
