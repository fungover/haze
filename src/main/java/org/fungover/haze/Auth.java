package org.fungover.haze;

import java.net.Socket;

public class Auth {
    private String password = System.getenv("PASSWORD");
    private static final String OK = "+OK\\r\\n\n";


    public void setPassword(String password) {
        this.password = password;
    }

    public boolean authenticate(String password, Socket client) {
        try {
            if (passwordNotSet()) {
                client.getOutputStream().write(OK.getBytes());
                return true;
            }

            if (this.password.equals(password)) {
                client.getOutputStream().write(OK.getBytes());
                return true;
            }
            client.getOutputStream().write("-Ah ah ah, you didn't say the magic word.".getBytes());
        } catch (Exception ignored) {
        }
        return false;
    }

    public String authenticate() {
        if (passwordNotSet())
            return OK;
        return "-Ah ah ah, you didn't say the magic word.";
    }

    private boolean passwordNotSet() {
        return (this.password == null || this.password.isBlank());
    }

    public boolean isPasswordSet() {
        return password != null;
    }
}
