package org.fungover.haze;

import java.net.Socket;

public class Auth {
    private String password;


    public void setPassword(String password) {
        this.password = password;
    }

    public boolean authenticate(String password, Socket client) {
        try {
            if (this.password.equals(password)) {
                return true;
            }

            client.getOutputStream().write(printAuthError());

        } catch (Exception e) {
            Log4j2.error(String.valueOf(e));
        }
        return false;
    }

    private static byte[] printAuthError() {
        return "-Ah ah ah, you didn't say the magic word. https://tinyurl.com/38e7yvp8".getBytes();
    }

    public boolean isPasswordSet() {
        return password != null;
    }
}
