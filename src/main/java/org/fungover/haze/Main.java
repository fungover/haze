package org.fungover.haze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {


        try (ServerSocket serverSocket = new ServerSocket(6379)) {
            while (true) {
                var client = serverSocket.accept();

                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));


                if (input.readLine().equals("PING\r\n")) {
                    client.getOutputStream().write("+PONG\r\n".getBytes());
                }


                client.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
