package org.fungover.haze;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static org.fungover.haze.Main.logger;

public class Auth {
    private String password;


    public void setPassword(String password) {
        this.password = password;
    }

    public boolean authenticate(String password, Socket client) {

        if (this.password.equals(password)) {
            return true;
        }
        try {
            client.getOutputStream().write(printAuthError());
            client.shutdownOutput();
        } catch (Exception e) {
            logger.error(String.valueOf(e));
        }
        return false;
    }

    public static boolean authenticateClient(Auth auth, boolean isPasswordSet, Socket client, List<String> inputList, boolean clientAuthenticated) throws IOException {
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

    public static byte[] printAuthError() {
        return "-Ah ah ah, you didn't say the magic word. https://tinyurl.com/38e7yvp8".getBytes();
    }

    public boolean isPasswordSet() {
        return password != null;
    }
}
