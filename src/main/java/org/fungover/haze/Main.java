package org.fungover.haze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {

        Initialize initialize = new Initialize();
        initialize.importCliOptions(args);

        try (ServerSocket serverSocket = new ServerSocket(initialize.setPort())) {
            while (true) {
                var client = serverSocket.accept();

                Runnable newThread = () -> {
                    try {


                        BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        System.out.println(input.readLine());
                        System.out.println(input.readLine());
                        System.out.println(input.readLine());
                        System.out.println(input.readLine());
                        System.out.println(input.readLine());


                        printThreadDebug();


                        client.getOutputStream().write("+OK\r\n".getBytes());

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
}
